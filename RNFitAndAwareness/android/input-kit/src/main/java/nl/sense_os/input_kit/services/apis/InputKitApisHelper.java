package nl.sense_os.input_kit.services.apis;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class InputKitApisHelper {
    private final AwarenessApiHelper mAwarenessApiHelper;
    @SuppressWarnings("SpellCheckingInspection")
    private final MonitoringGeofenceApiHelper mMonitoringGeofenceApiHelper;
    private final StepsCountApiHelper mStepsCountApiHelper;
    public InputKitApisHelper(Context context, GoogleApiClient googleApiClient) {
        mAwarenessApiHelper = new AwarenessApiHelper(context, googleApiClient);
        mMonitoringGeofenceApiHelper = new MonitoringGeofenceApiHelper(context, googleApiClient);
        mStepsCountApiHelper = new StepsCountApiHelper(googleApiClient);
    }

    public AwarenessApiHelper getAwarenessApi() {
        return mAwarenessApiHelper;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public MonitoringGeofenceApiHelper getMonitoringGeofenceApi() {
        return mMonitoringGeofenceApiHelper;
    }

    public StepsCountApiHelper getStepsCountApi() {
        return mStepsCountApiHelper;
    }
}
