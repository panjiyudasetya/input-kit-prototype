package nl.sense_os.input_kit.services.apis;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.sense_os.input_kit.constant.InputKitType;
import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.entities.StepsCountResponse;
import nl.sense_os.input_kit.eventbus.GAClientConnReceivedEvent;

/**
 * Created by panjiyudasetya on 5/5/17.
 */

public class StepsCountApiHelper {
    private static final String TAG = "STEP_COUNT_TASK";
    private static final int START_TIME = 0;
    private static final int END_TIME = 1;

    // This is the date of Google Fit first release.
    private static final long BEGINNING_OF_THE_TIME = 1403654400000L;

    private GoogleApiClient mClient;
    private DateFormat mDateFormat;
    private DateFormat mDateTimeFormat;

    public StepsCountApiHelper(@NonNull GoogleApiClient client) {
        this.mClient = client;
        this.mDateFormat = DateFormat.getDateInstance();
        this.mDateTimeFormat = DateFormat.getDateTimeInstance();
    }

    public void subscribeStepsCountApi(ResultCallback<Status> resultCallback) {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi
                .subscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(resultCallback);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeStepsCountApi() {
        Fitness.RecordingApi
                .unsubscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // TODO: Do we need to handle this ?
                    }
                });
    }

    /**
     * Get weekly steps count history.
     * This function should be called within asynchronous process because of
     * reading historical data through {@link Fitness#HistoryApi} will be executed on main
     * thread by default.
     *
     * @return {@link List<Content>} Historical steps content
     */
    @SuppressWarnings("unused")//This is a public API
    @NonNull
    public StepsCountResponse getWeeklyStepCount(boolean useDataAggregation) {
        long[] range = getTimeRangeHistory();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        DataReadResult dataReadResult = readStepCountHistory(range[START_TIME], range[END_TIME], useDataAggregation);
        List<Content> contents = Collections.emptyList();
        if (dataReadResult.getStatus().isSuccess()) {
            return new StepsCountResponse(true, contents);
        } else Log.w(TAG, "There was a problem getting the step count.");
        return new StepsCountResponse(false, contents);
    }

    /**
     * Get all steps count history.
     * This function should be called within asynchronous process because of
     * reading historical data through {@link Fitness#HistoryApi} will be executed on main
     * thread by default.
     * @param useDataAggregation Set to True to fetch aggregated history of steps count, False otherwise
     * @return {@link StepsCountResponse} Historical steps content
     */
    @NonNull
    public StepsCountResponse getAllStepCountHistory(boolean useDataAggregation) {
        long[] range = getTimeRangeHistory();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        DataReadResult dataReadResult = readStepCountHistory(BEGINNING_OF_THE_TIME, range[END_TIME], useDataAggregation);
        List<Content> contents = Collections.emptyList();
        if (dataReadResult.getStatus().isSuccess()) {
            if (useDataAggregation) {
                contents = createContentFromBucket(dataReadResult.getBuckets());
                return new StepsCountResponse(true, contents);
            } else {
                contents = dumpDataSets(dataReadResult.getDataSets());
                return new StepsCountResponse(true, contents);
            }
        } else Log.w(TAG, "There was a problem getting the step count.");
        return new StepsCountResponse(false, contents);
    }

    private DataReadResult readStepCountHistory(long startTime, long endTime, boolean useDataAggregation) {
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
                    .bucketByTime(1, TimeUnit.DAYS);
        } else requestBuilder.read(DataType.TYPE_STEP_COUNT_DELTA);

        DataReadRequest request = requestBuilder
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        return Fitness.HistoryApi.readData(mClient, request).await(1, TimeUnit.MINUTES);
    }

    private List<Content> createContentFromBucket(@Nullable List<Bucket> buckets) {
        if (buckets == null || buckets.isEmpty()) return Collections.emptyList();

        List<Content> contents = new ArrayList<>();
        int startFormIndex = 0;
        for (Bucket bucket : buckets) {
            List<DataSet> dataSets = bucket.getDataSets();
            contents.addAll(startFormIndex, dumpDataSets(dataSets));
            startFormIndex = contents.size();
        }
        return contents;
    }

    private List<Content> dumpDataSet(@Nullable DataSet dataSet) {
        if (dataSet == null) return Collections.emptyList();

        List<Content> contents = new ArrayList<>();
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());

        for (DataPoint dp : dataSet.getDataPoints()) {
            contents.add(new Content(
                    Content.STEPS_TYPE,
                    new Content.StepBuilder()
                            .dataPointType(dp.getDataType().getName())
                            .startTimeDetected(mDateTimeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                            .endTimeDetected(mDateTimeFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            .fields(extractPairOfFields(dp))
                            .build(),
                    dp.getTimestamp(TimeUnit.MILLISECONDS)
            ));
        }
        return contents;
    }

    private List<Content> dumpDataSets(@Nullable List<DataSet> dataSets) {
        if (dataSets == null) return Collections.emptyList();

        List<Content> contents = new ArrayList<>();
        int startFormIndex = 0;
        for (DataSet dataSet : dataSets) {
            contents.addAll(startFormIndex, dumpDataSet(dataSet));
            startFormIndex = contents.size();
        }

        return contents;
    }

    private long[] getTimeRangeHistory() {
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        Log.i(TAG, "Range Start: " + mDateFormat.format(startTime));
        Log.i(TAG, "Range End: " + mDateFormat.format(endTime));

        return new long[] {startTime, endTime};
    }

    @Nullable
    private Map<String, String> extractPairOfFields(@NonNull DataPoint dataPoint) {
        List<Field> fields = dataPoint.getDataType().getFields();
        if (fields == null || fields.isEmpty()) return null;

        Map<String, String> output = new HashMap<>();
        for (Field field : fields) output.put(field.getName(), dataPoint.getValue(field).toString());

        return output;
    }
}
