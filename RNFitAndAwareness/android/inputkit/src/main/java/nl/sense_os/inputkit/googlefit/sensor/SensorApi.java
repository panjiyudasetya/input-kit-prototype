package nl.sense_os.inputkit.googlefit.sensor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by panjiyudasetya on 6/15/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class SensorApi {
    private SensorOptions mOptions;
    //private SubscribeListener mSubscribeListener;
    //private UnsubscribeListener mUnsubscribeListener;
    private Map<String, SubscribeListener> mSubscriber;
    private Map<String, UnsubscribeListener> mUnsubscriber;

    public interface SubscribeListener {
        void onSubscribe();
        void onSubscribeFailure(@NonNull String reason);
    }

    public interface UnsubscribeListener {
        void onUnsubscribe();
        void onUnsubscribeFailure(@NonNull String reason);
    }

    public SensorApi(@NonNull SensorOptions options) {
        this.mOptions = options;
        this.mSubscriber = new HashMap<>();
        this.mUnsubscriber = new HashMap<>();
    }

    /**
     * Subscribing relevant Sensor
     * @param subscriberName subscriber name
     * @param listener subscribe listener
     */
    public void subscribe(@NonNull String subscriberName, @NonNull SubscribeListener listener) {
        mSubscriber.put(subscriberName, listener);
        Fitness.SensorsApi
                .findDataSources(mOptions.getApiClient(), mOptions.getDataSourcesRequest())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                        DataSource dataSource = findDataSource(dataSourcesResult);
                        if (dataSource == null) {
                            String message = "No Data sources available for " + mOptions.getDataType().getName();
                            notifySubscribers(false, message);
                        } else registerSensorListener(dataSource);
                    }
                });
    }

    /**
     * Stop subscribing data from relevant Sensor
     * @param unsubscriberName unsubscriber name
     * @param listener unsubscribe listener
     */
    public void unsubscribe(@NonNull String unsubscriberName, @NonNull UnsubscribeListener listener) {
        mUnsubscriber.put(unsubscriberName, listener);
        Fitness.SensorsApi
                .remove(mOptions.getApiClient(), mOptions.getSensorListener())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (!status.isSuccess()) {
                            notifyUnsubscribers(false, status.getStatusMessage());
                        } else notifyUnsubscribers(true, status.getStatusMessage());
                    }
                });
    }

    /**
     * Helper function to create Sensor Request on specific {@link DataSource}
     * @param dataSource Sensor {@link DataSource}
     * @return  {@link SensorRequest}
     */
    private SensorRequest buildSensorRequest(@NonNull DataSource dataSource) {
        return new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(mOptions.getDataType())
                .setSamplingRate(mOptions.getSamplingRate(), mOptions.getSamplingTimeUnit())
                .build();
    }

    /**
     * Helper function to register sensor listener into Sensor API
     * @param dataSource Sensor {@link DataSource}
     */
    private void registerSensorListener(@NonNull DataSource dataSource) {
        SensorRequest request = buildSensorRequest(dataSource);
        Fitness.SensorsApi
                .add(mOptions.getApiClient(), request, mOptions.getSensorListener())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (!status.isSuccess()) {
                            String message = "Unable to sensing " + mOptions.getDataType().getName()
                                    + ". \n" + status.getStatusMessage();
                            notifySubscribers(false, message);
                        } else notifySubscribers(true, status.getStatusMessage());
                    }
                });
    }

    /**
     * Helper function to find a correct {@link DataSource} for relevant sensor.
     * @param dataSourcesResult {@link DataSourcesResult}
     * @return {@link DataSource}
     */
    private DataSource findDataSource(@Nullable DataSourcesResult dataSourcesResult) {
        if (dataSourcesResult == null) return null;
        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
            if (mOptions.getDataType().equals(dataSource.getDataType()))
                return dataSource;
        }
        return null;
    }

    /**
     * Notify all of subscriber and remove it immediately from the tree elements to avoid
     * Illegal callback invocation from React Native module error.
     * @param isSuccess     True successfully subscribed, False otherwise
     * @param message       Additional connection messages
     */
    public void notifySubscribers(boolean isSuccess, String message) {
        for (Map.Entry<String, SubscribeListener> consumer : mSubscriber.entrySet()) {
            SubscribeListener listener = consumer.getValue();
            if (listener == null) continue;
            if (isSuccess) listener.onSubscribe();
            else listener.onSubscribeFailure(message);
            mSubscriber.put(consumer.getKey(), null);
        }
    }

    /**
     * Notify all of unsubscriber and remove it immediately from the tree elements to avoid
     * Illegal callback invocation from React Native module error.
     * @param isSuccess     True successfully subscribed, False otherwise
     * @param message       Additional connection messages
     */
    public void notifyUnsubscribers(boolean isSuccess, String message) {
        for (Map.Entry<String, UnsubscribeListener> consumer : mUnsubscriber.entrySet()) {
            UnsubscribeListener listener = consumer.getValue();
            if (listener == null) continue;
            if (isSuccess) listener.onUnsubscribe();
            else listener.onUnsubscribeFailure(message);
            mSubscriber.put(consumer.getKey(), null);
        }
    }
}
