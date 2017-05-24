package nl.sense_os.input_kit.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.sense_os.input_kit.services.apis.AwarenessApiHelper;
import nl.sense_os.input_kit.services.apis.LocationUpdateApiHelper;
import nl.sense_os.input_kit.services.apis.MonitoringGeofenceApiHelper;
import nl.sense_os.input_kit.constant.ServiceType;
import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.DetectedActivityEvent;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;
import nl.sense_os.input_kit.eventbus.LocationChangeEvent;
import nl.sense_os.input_kit.helpers.DataCacheHelper;
import nl.sense_os.input_kit.tasks.PopulateActivityDataTask;
import nl.sense_os.input_kit.tasks.PopulateGeofenceDataTask;
import nl.sense_os.input_kit.tasks.PopulateLocationsDataTask;

import static nl.sense_os.input_kit.constant.Preference.LOCATION_UPDATE_CONTENT_KEY;

/**
 * Created by panjiyudasetya on 5/8/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class AwarenessService extends BaseService implements LocationListener {
    private static final String TAG = "AWARENESS_SERVICE";
    private static final String TRACKER_KEY = String.format("%s_TRACKER_KEY", TAG);
    private static final String KEY_ACTIVATED = String.format("%s_IS_ACTIVE", TAG);
    private static final Api[] REQUIRED_APIS = {Awareness.API, ActivityRecognition.API, LocationServices.API};
    private static final DataCacheHelper CACHE = new DataCacheHelper();
    private static int mServiceType;

    private GoogleApiClient mClient;
    private AwarenessApiHelper mAwarenessHelper;
    private MonitoringGeofenceApiHelper mGeofenceHelper;
    private LocationUpdateApiHelper mLocationHelper;
    private static Map<Integer, Boolean> mStateAwarenessTracker;

    public static Intent withContext(@NonNull Context context, int type) {
        mServiceType = type;
        return new Intent(context, AwarenessService.class);
    }

    public static boolean isActive() {
        return Hawk.get(KEY_ACTIVATED, false);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.put(KEY_ACTIVATED, true);
    }

    @Override
    public void onDestroy() {
        Hawk.put(KEY_ACTIVATED, false);
        super.onDestroy();
    }

    @Override
    protected Scope[] initWithGoogleClientScopes() {
        return null;
    }

    @Override
    protected Api[] initWithGoogleClientApis() {
        return REQUIRED_APIS;
    }

    @Override
    protected void initComponents() {
        mClient = getApiClient();
        mAwarenessHelper = new AwarenessApiHelper(this, mClient);
        mGeofenceHelper = new MonitoringGeofenceApiHelper(this, mClient);
        mLocationHelper = new LocationUpdateApiHelper(this, mClient);
    }

    @Override
    protected void subscribe() {
        mStateAwarenessTracker = Hawk.get(TRACKER_KEY, new HashMap<Integer, Boolean>());
        handleSubscribeServiceType();
    }

    @Override
    public void onLocationChanged(Location location) {
        Content content = new Content(
                Content.LOCATION_UPDATE_TYPE,
                new Content.LocationUpdateBuilder(location).build(),
                System.currentTimeMillis()
        );

        CACHE.save(LOCATION_UPDATE_CONTENT_KEY, content);
        consumeLocationsData();
    }

    private void handleSubscribeServiceType() {
        switch (mServiceType) {
            case ServiceType.Awareness.ALL:
            case ServiceType.Awareness.ACTIVITIES:
            case ServiceType.Awareness.GEOFENCING:
            case ServiceType.Awareness.LOCATION_UPDATES:
                startAwareness();
                break;
            case ServiceType.Awareness.STOP_ALL:
            case ServiceType.Awareness.STOP_ACTIVITIES:
            case ServiceType.Awareness.STOP_GEOFENCING:
            case ServiceType.Awareness.STOP_LOCATION_UPDATES:
                stopAwareness();
                break;
            default: break;
        }
    }

    private void startAwareness() {
        switch (mServiceType) {
            case ServiceType.Awareness.ACTIVITIES:
                saveServiceState(ServiceType.Awareness.ACTIVITIES, true);
                consumeActivityData();
                break;
            case ServiceType.Awareness.GEOFENCING:
                saveServiceState(ServiceType.Awareness.GEOFENCING, true);
                consumeGeofencingData();
                break;
            case ServiceType.Awareness.LOCATION_UPDATES:
                saveServiceState(ServiceType.Awareness.LOCATION_UPDATES, true);
                consumeLocationsData(true);
                break;
            case ServiceType.Awareness.ALL:
                handleStartAllAwareness();
                break;
            default: break;
        }
    }

    private void stopAwareness() {
        switch (mServiceType) {
            case ServiceType.Awareness.STOP_ACTIVITIES:
                saveServiceState(ServiceType.Awareness.STOP_ACTIVITIES, false);
                mAwarenessHelper.stopRequestUpdateActivity();
                break;
            case ServiceType.Awareness.STOP_GEOFENCING:
                saveServiceState(ServiceType.Awareness.STOP_GEOFENCING, false);
                mGeofenceHelper.stopSensingSenseHQGeofences();
                break;
            case ServiceType.Awareness.STOP_LOCATION_UPDATES:
                saveServiceState(ServiceType.Awareness.LOCATION_UPDATES, false);
                mLocationHelper.stopLocationUpdates();
                break;
            case ServiceType.Awareness.STOP_ALL:
                mAwarenessHelper.stopRequestUpdateActivity();
                mGeofenceHelper.stopSensingSenseHQGeofences();
                mLocationHelper.stopLocationUpdates();
            break;
            default: break;
        }
    }

    private void saveServiceState(int serviceType, boolean state) {
        mStateAwarenessTracker.put(serviceType, state);
        Hawk.put(TRACKER_KEY, mStateAwarenessTracker);
    }

    private void handleStartAllAwareness() {
        Map<Integer, Boolean> savedState = Hawk.get(TRACKER_KEY, new HashMap<Integer, Boolean>());
        for (Map.Entry<Integer, Boolean> serviceState : savedState.entrySet()) {
            switch (serviceState.getKey()) {
                case ServiceType.Awareness.ACTIVITIES:
                    consumeActivityData();
                    break;
                case ServiceType.Awareness.GEOFENCING:
                    consumeGeofencingData();
                    break;
                case ServiceType.Awareness.LOCATION_UPDATES:
                    consumeLocationsData(true);
                    break;
                default: break;
            }
        }
    }

    private void consumeActivityData() {
        mAwarenessHelper.requestUpdateActivity();
        new PopulateActivityDataTask() {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                EventBus.getDefault().post(new DetectedActivityEvent(contents));
            }
        }.run();
    }

    private void consumeGeofencingData() {
        mGeofenceHelper.addSenseHQGeofences();
        new PopulateGeofenceDataTask() {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                EventBus.getDefault()
                        .post(new GeofenceEvent(contents));
            }
        }.run();
    }

    private void consumeLocationsData(boolean trackNewUpdates) {
        if (trackNewUpdates) {
            consumeLocationsData();
            startLocationsUpdate();
        } else consumeLocationsData();
    }

    private void consumeLocationsData() {
        new PopulateLocationsDataTask() {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                EventBus.getDefault()
                        .post(new LocationChangeEvent(contents));
            }
        }.run();
    }

    private void startLocationsUpdate() {
        mLocationHelper.startLocationUpdates(new LocationUpdateApiHelper.OnLocationUpdateFailureListener() {
            @Override
            public void onPermissionNeeded(String message) {
                EventBus.getDefault().post(new LocationChangeEvent(
                        false, message, null
                ));
            }

            @Override
            public void onSettingChangeUnavailable(String message) {
                EventBus.getDefault().post(new LocationChangeEvent(
                        false, message, null
                ));
            }
        });
    }
}
