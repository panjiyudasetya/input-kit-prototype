package nl.sense_os.inputkit.googlefit.sensor;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.OnDataPointListener;
import static nl.sense_os.inputkit.googlefit.sensor.SensorApi.SubscribeListener;
import static nl.sense_os.inputkit.googlefit.sensor.SensorApi.UnsubscribeListener;

/**
 * Created by panjiyudasetya on 6/15/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class Validator {
    private Validator() { }

    public static void validateApiClient(GoogleApiClient client) {
        if (client == null) {
            throw new IllegalStateException("Google Api client must be provided!");
        }
    }

    public static void validateDataType(DataType dataType) {
        if (dataType == null) {
            throw new IllegalStateException("Sensor data type must be provided!");
        }
    }

    public static void validateSensorListener(OnDataPointListener listener) {
        if (listener == null) {
            throw new IllegalStateException("Sensor listener must be provided!");
        }
    }

    public static void validateSensorSubscribeListener(SubscribeListener listener) {
        if (listener == null) {
            throw new IllegalStateException("Sensor subscribe listener must be provided!");
        }
    }

    public static void validateSensorUnsubscribeListener(UnsubscribeListener listener) {
        if (listener == null) {
            throw new IllegalStateException("Sensor unsubscribe listener must be provided!");
        }
    }

    public static void validateSensorOptions(SensorOptions options) {
        if (options == null) {
            throw new IllegalStateException("Sensor options must be provided!");
        }
    }
}
