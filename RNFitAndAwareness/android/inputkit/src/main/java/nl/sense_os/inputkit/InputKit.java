package nl.sense_os.inputkit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import nl.sense_os.inputkit.constant.Interval;
import nl.sense_os.inputkit.entity.StepContent;
import nl.sense_os.inputkit.entity.TimeInterval;
import nl.sense_os.inputkit.googlefit.sensor.SensorApi;
import nl.sense_os.inputkit.googlefit.sensor.SensorOptions;
import nl.sense_os.inputkit.googlefit.steps.Options;
import nl.sense_os.inputkit.googlefit.steps.StepHistory;

/**
 * Created by panjiyudasetya on 6/14/17.
 */

public class InputKit {
    private static InputKit sInputKit;
    private static final Api[] REQUIRED_APIS = {LocationServices.API, Fitness.RECORDING_API, Fitness.HISTORY_API};
    private static final Scope[] REQUIRED_SCOPES = {new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE)};

    private GoogleApiClient mClient;
    private StepHistory mStepHistory;
    private SensorApi mStepSensor;
    private Context mContext;

    public interface Callback {
        /**
         * This action will be triggered when successfully connected to Input Kit Service.
         */
        void onAvailable();

        /**
         * This event will be triggered when there is a problem on your device internet connection.
         * @param reason Either of your network connection loss
         *               or Input Kit services has been disconnected.
         */
        void onNotAvailable(@NonNull String reason);

        /**
         * This event will be triggered whenever connection to Input Kit service has been rejected.
         * In any case, the problem probably solved by call {@link ConnectionResult#startResolutionForResult(Activity, int)}
         * which int value should be referred to {@link ConnectionResult#getErrorCode()}.
         * But this action required UI interaction, so be careful with it.
         * @param connectionResult {@link ConnectionResult}
         */
        void onConnectionRefused(@NonNull ConnectionResult connectionResult);
    }

    public interface ResultCallback<T> {
        void onNewData(T data);
    }

    private InputKit(@NonNull Context context) {
        mContext = context.getApplicationContext();
        mStepHistory = new StepHistory(mClient);
        mStepSensor = new SensorApi(new SensorOptions
                .Builder()
                .apiClient(mClient)
                .dataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .samplingRate(10)
                .samplingTimeUnit(TimeUnit.MINUTES)
                .sensorListener(new OnDataPointListener() {
                    @Override
                    public void onDataPoint(DataPoint dataPoint) {
                        // Do something with sensor listener here.
                        // Eg. broadcast an event to the parent usage
                    }
                })
                .build()
        );
    }

    public static InputKit getInstance(@NonNull Context context) {
        if (sInputKit == null) sInputKit = new InputKit(context);
        return sInputKit;
    }

    /**
     * Authorize Input Kit service connections.
     * @param callback event listener
     */
    public void authorize(@NonNull final Callback callback) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mContext);
        addApis(builder);
        addScopes(builder);
        builder.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                callback.onAvailable();
            }

            @Override
            public void onConnectionSuspended(int cause) {
                String message;
                if (cause == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST)
                    message = "Network lost";
                else
                    message = "Service disconnected";
                callback.onNotAvailable(message);
            }
        });
        builder.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                callback.onConnectionRefused(connectionResult);
            }
        });
        mClient = builder.build();
        mClient.connect();
    }

    /**
     * Check play service availability. By default Google API Client will try to connect asynchronously,
     * so {@link Callback} is required to listen all event during this process.
     * @param callback event listener
     */
    public void checkAvailability(@NonNull Callback callback) {
        if (mClient == null || !mClient.isConnected()) authorize(callback);
        else callback.onAvailable();
    }

    /**
     * Get total Today steps count.
     * @param callback {@link ResultCallback<Integer>} containing number of total steps count
     */
    public void getStepCount(@NonNull ResultCallback<Integer> callback) {
        mStepHistory.getStepCount(callback);
    }

    /**
     * Get total steps count of specific range
     * @param startTime epoch for the start date
     * @param endTime   epoch for the end date
     * @param callback {@link ResultCallback<Integer>} containing number of total steps count
     */
    public void getStepCount(long startTime, long endTime, ResultCallback<Integer> callback) {
        Options options = new Options.Builder()
                .startTime(startTime)
                .endTime(endTime)
                .timeAggregation(10)
                .timeUnitAggregation(TimeUnit.MINUTES)
                .useDataAgregation()
                .build();
        mStepHistory.getStepCount(options, callback);
    }

    /**
     * Get distribution step count history by specific time period.
     * This function should be called within asynchronous process because of
     * reading historical data through {@link Fitness#HistoryApi} will be executed on main
     * thread by default.
     *
     * @param startTime epoch for the start date
     * @param endTime   epoch for the end date
     * @param interval  on of any {@link nl.sense_os.inputkit.constant.Interval.IntervalName}
     * @param callback {@link nl.sense_os.inputkit.InputKit.ResultCallback}
     */
    @SuppressWarnings("unused")//This is a public API
    @NonNull
    public void getStepCountDistribution(@NonNull long startTime,
                                         @NonNull long endTime,
                                         @NonNull @Interval.IntervalName String interval,
                                         @NonNull ResultCallback<StepContent> callback) {

        TimeInterval timeInterval = new TimeInterval(interval);
        Options options = new Options.Builder()
                .startTime(startTime)
                .endTime(endTime)
                .timeAggregation(timeInterval.getValue())
                .timeUnitAggregation(timeInterval.getTimeUnit())
                .useDataAgregation()
                .build();
        mStepHistory.getStepCountDistribution(options, callback);
    }

    /**
     * Helper function to add required apis into Google API Client Builder
     * @param builder Google Api Client Builder
     */
    private void addApis(GoogleApiClient.Builder builder) {
        if (REQUIRED_APIS == null || REQUIRED_APIS.length == 0) {
            String message = "Unable to continue this action, Google Api Client should contain at least one Api";
            throw new IllegalStateException(message);
        }
        for (Api api : REQUIRED_APIS) builder.addApi(api);
    }

    /**
     * Helper function to add required scopes into Google Api Client Builder
     * @param builder Google Api Client Builder
     */
    private void addScopes(GoogleApiClient.Builder builder) {
        if (REQUIRED_SCOPES == null || REQUIRED_SCOPES.length == 0) return;
        for (Scope scope : REQUIRED_SCOPES) builder.addScope(scope);
    }
}
