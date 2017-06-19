package nl.sense_os.inputkit.googlefit.sensor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;

import java.util.concurrent.TimeUnit;
import static nl.sense_os.inputkit.googlefit.sensor.SamplingDefault.DEFAULT_SAMPLING_TIME_UNIT;
import static nl.sense_os.inputkit.googlefit.sensor.SamplingDefault.DEFAULT_TIME_SAMPLING_RATE;
import static nl.sense_os.inputkit.googlefit.sensor.Validator.validateApiClient;
import static nl.sense_os.inputkit.googlefit.sensor.Validator.validateDataType;
import static nl.sense_os.inputkit.googlefit.sensor.Validator.validateSensorListener;

/**
 * Created by panjiyudasetya on 6/15/17.
 */

public class SensorOptions {
    private GoogleApiClient mClient;
    private DataType mDataType;
    private DataSourcesRequest mDataSourcesRequest;
    private int mSamplingRate;
    private TimeUnit mSamplingTimeUnit;
    private OnDataPointListener mSensorListener;

    private SensorOptions(@NonNull GoogleApiClient client,
                          @NonNull DataType dataType,
                          @NonNull DataSourcesRequest dataSourcesRequest,
                          int timeSampling,
                          @NonNull TimeUnit samplingTimeUnit,
                          @NonNull OnDataPointListener sensorListener) {
        mClient = client;
        mDataType = dataType;
        mDataSourcesRequest = dataSourcesRequest;
        mSamplingRate = timeSampling;
        mSamplingTimeUnit = samplingTimeUnit;
        mSensorListener = sensorListener;
    }

    public GoogleApiClient getApiClient() {
        return mClient;
    }

    public DataType getDataType() {
        return mDataType;
    }

    public DataSourcesRequest getDataSourcesRequest() {
        return mDataSourcesRequest;
    }

    public int getSamplingRate() {
        return mSamplingRate;
    }

    public TimeUnit getSamplingTimeUnit() {
        return mSamplingTimeUnit;
    }

    public OnDataPointListener getSensorListener() {
        return mSensorListener;
    }

    public static class Builder {
        private GoogleApiClient newClient;
        private DataType newDataType;
        private DataSourcesRequest newDataSourcesRequest;
        private int newSamplingRate;
        private TimeUnit newSamplingTimeUnit;
        private OnDataPointListener newSensorListener;

        public Builder apiClient(@NonNull GoogleApiClient apiClient) {
            newClient = apiClient;
            return this;
        }

        public Builder dataType(@NonNull DataType dataType) {
            newDataType = dataType;
            newDataSourcesRequest = new DataSourcesRequest.Builder()
                    .setDataTypes(dataType)
                    .setDataSourceTypes(DataSource.TYPE_RAW)
                    .build();
            return this;
        }

        public Builder samplingRate(int samplingRate) {
            newSamplingRate = samplingRate;
            return this;
        }

        public Builder samplingTimeUnit(@Nullable TimeUnit samplingTimeUnit) {
            newSamplingTimeUnit = samplingTimeUnit;
            return this;
        }

        public Builder sensorListener(@NonNull OnDataPointListener sensorListener) {
            newSensorListener = sensorListener;
            return this;
        }

        public SensorOptions build() {
            validateApiClient(newClient);
            validateDataType(newDataType);
            validateSensorListener(newSensorListener);

            return new SensorOptions(
                    newClient,
                    newDataType,
                    newDataSourcesRequest,
                    newSamplingRate == 0 ? DEFAULT_TIME_SAMPLING_RATE : newSamplingRate,
                    newSamplingTimeUnit == null ? DEFAULT_SAMPLING_TIME_UNIT : newSamplingTimeUnit,
                    newSensorListener
            );
        }
    }
}
