package nl.sense_os.inputkit.googlefit.steps;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.sense_os.inputkit.InputKit;
import nl.sense_os.inputkit.entity.Step;
import nl.sense_os.inputkit.entity.StepContent;

/**
 * Created by panjiyudasetya on 6/15/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class StepHistory {
    private static final String TAG = "StepHistory";
    private static final int LIMIT_OF_HISTORICAL_DATA = 1000;

    private GoogleApiClient mClient;

    public StepHistory(@NonNull GoogleApiClient client) {
        this.mClient = client;
    }

    /**
     * Subscribe historical steps count from Fitness API
     * @param resultCallback        Callback
     */
    public void subscribe(ResultCallback<Status> resultCallback) {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi
                .subscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(resultCallback);
    }

    /**
     * Stop subscribing historical steps count from Fitness API
     * @param resultCallback        Callback
     */
    public void unsubscribe(ResultCallback<Status> resultCallback) {
        Fitness.RecordingApi
                .unsubscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(resultCallback);
    }

    /**
     * Get daily total step count.
     * @param callback {@link nl.sense_os.inputkit.InputKit.ResultCallback}
     * @return Total step count
     */
    public void getStepCount(@NonNull final InputKit.ResultCallback<Integer> callback) {
        Fitness.HistoryApi
                .readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<DailyTotalResult>() {
                    @Override
                    public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                        int total = 0;
                        if (dailyTotalResult.getStatus().isSuccess()) {
                            DataSet totalSet = dailyTotalResult.getTotal();
                            total = totalSet.isEmpty()
                                    ? 0
                                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        } else Log.w(TAG, "There was a problem getting the step count.");
                        callback.onNewData(total);
                    }
                });
    }

    /**
     * Get total steps count of specific range
     * @param options               Steps count options
     * @param callback {@link InputKit.ResultCallback <Integer>} containing number of total steps count
     */
    @SuppressWarnings("unused")//This is a public API
    @NonNull
    public void getStepCount(@NonNull final Options options,
                             @NonNull final InputKit.ResultCallback<Integer> callback) {
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        readStepHistory(
                options.getStartTime(),
                options.getEndTime(),
                options.isUseDataAggregation(),
                options.getTimeAggregation(),
                options.getTimeUnitAggregation(),
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(@NonNull DataReadResult dataReadResult) {
                        if (dataReadResult.getStatus().isSuccess()) {
                            List<Step> contents = extractHistory(dataReadResult, options.isUseDataAggregation());
                            StepContent stepContent = new StepContent(true, contents);
                            callback.onNewData(stepContent.getTotalSteps());
                        } else {
                            StepContent stepContent = new StepContent(false, Collections.<Step>emptyList());
                            callback.onNewData(0);
                            Log.w(TAG, "There was a problem getting the step count.");
                        }
                    }
                }
        );
    }

    /**
     * Get distribution step count history by specific time period.
     * This function should be called within asynchronous process because of
     * reading historical data through {@link Fitness#HistoryApi} will be executed on main
     * thread by default.
     *
     * @param options               Steps count options
     * @param callback {@link nl.sense_os.inputkit.InputKit.ResultCallback}
     */
    @SuppressWarnings("unused")//This is a public API
    @NonNull
    public void getStepCountDistribution(@NonNull final Options options,
                                         @NonNull final InputKit.ResultCallback<StepContent> callback) {
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        readStepHistory(
                options.getStartTime(),
                options.getEndTime(),
                options.isUseDataAggregation(),
                options.getTimeAggregation(),
                options.getTimeUnitAggregation(),
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(@NonNull DataReadResult dataReadResult) {
                        if (dataReadResult.getStatus().isSuccess()) {
                            List<Step> contents = extractHistory(dataReadResult, options.isUseDataAggregation());
                            StepContent stepContent = new StepContent(true, contents);
                            callback.onNewData(stepContent);
                        } else {
                            StepContent stepContent = new StepContent(false, Collections.<Step>emptyList());
                            callback.onNewData(stepContent);
                            Log.w(TAG, "There was a problem getting the step count.");
                        }
                    }
                }
        );
    }

    /**
     * Helper function to read steps count history from Fitness API.
     *
     * @param startTime             start time cumulative steps count
     * @param endTime               end time cumulative steps count
     * @param useDataAggregation    Set true to aggregate existing data by a bucket of time periods
     * @param timeAggregation       Time aggregation
     * @param timeUnitAggregation   Time unit aggregation
     */
    private void readStepHistory(long startTime,
                            long endTime,
                            boolean useDataAggregation,
                            int timeAggregation,
                            @NonNull TimeUnit timeUnitAggregation,
                            @NonNull ResultCallback<DataReadResult> callback) {
        DataReadRequest.Builder requestBuilder = new DataReadRequest.Builder();
        if (useDataAggregation) {
            requestBuilder
                    // The data request can specify multiple data types to return, effectively
                    // combining multiple data queries into one call.
                    // In this example, it's very unlikely that the request is for several hundred
                    // data points each consisting of a few steps and a timestamp.  The more likely
                    // scenario is wanting to see how many steps were walked per day, for several days.
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                    // bucketByTime allows for a time span, whereas bucketBySession would allow
                    // bucketing by "sessions", which would need to be defined in code.
                    .bucketByTime(timeAggregation, timeUnitAggregation);
        } else requestBuilder.read(DataType.TYPE_STEP_COUNT_DELTA);

        DataReadRequest request = requestBuilder
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .setLimit(LIMIT_OF_HISTORICAL_DATA)
                .build();

        Fitness.HistoryApi
                .readData(mClient, request)
                .setResultCallback(callback);
    }

    /**
     * Helper function to extract step count history based on {@link DataReadResult} and aggregation
     * key
     *
     * @param dataReadResult        {@link DataReadResult} of Step count history
     * @param useDataAggregation    Set true to aggregate existing data by a bucket of time periods
     * @return {@link List<Step>}
     */
    private List<Step> extractHistory(DataReadResult dataReadResult, boolean useDataAggregation) {
        if (useDataAggregation) {
            return historyFromBucket(dataReadResult.getBuckets());
        } else {
            return historyFromDataSet(dataReadResult.getDataSets());
        }
    }

    /**
     * Helper function to extact step history from {@link Bucket}
     * @param buckets {@link List<Bucket>}
     * @return {@link List<Step>}
     */
    private List<Step> historyFromBucket(@Nullable List<Bucket> buckets) {
        if (buckets == null || buckets.isEmpty()) return Collections.emptyList();

        List<Step> contents = new ArrayList<>();
        int startFormIndex = 0;
        for (Bucket bucket : buckets) {
            List<DataSet> dataSets = bucket.getDataSets();
            contents.addAll(startFormIndex, historyFromDataSet(dataSets));
            startFormIndex = contents.size();
        }
        return contents;
    }

    /**
     * Helper function to extact step history from {@link DataSet}
     * @param dataSet {@link DataSet}
     * @return {@link List<Step>}
     */
    private List<Step> historyFromDataSet(@Nullable DataSet dataSet) {
        if (dataSet == null) return Collections.emptyList();

        List<Step> contents = new ArrayList<>();
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());

        for (DataPoint dp : dataSet.getDataPoints()) {
            contents.add(new Step(
                    getStep(dp),
                    dp.getStartTime(TimeUnit.MILLISECONDS),
                    dp.getEndTime(TimeUnit.MILLISECONDS)
            ));
        }
        return contents;
    }

    /**
     * Helper function to extact step history from {@link DataSet}
     * @param dataSets {@link DataSet}
     * @return {@link List<Step>}
     */
    private List<Step> historyFromDataSet(@Nullable List<DataSet> dataSets) {
        if (dataSets == null) return Collections.emptyList();

        List<Step> contents = new ArrayList<>();
        int startFormIndex = 0;
        for (DataSet dataSet : dataSets) {
            contents.addAll(startFormIndex, historyFromDataSet(dataSet));
            startFormIndex = contents.size();
        }

        return contents;
    }

    /**
     * Helper function to get value step from data point.
     * @param dataPoint Detected step in {@link DataPoint}
     * @return Step count
     */
    private int getStep(@NonNull DataPoint dataPoint) {
        List<Field> fields = dataPoint.getDataType().getFields();
        if (fields == null || fields.isEmpty()) return 0;

        // Usually this fields only contains one row, so we can directly return the value
        for (Field field : fields) return dataPoint.getValue(field).asInt();
        return 0;
    }
}
