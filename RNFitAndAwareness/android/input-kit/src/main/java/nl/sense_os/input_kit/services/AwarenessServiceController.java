package nl.sense_os.input_kit.services;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.DetectedActivityEvent;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;
import nl.sense_os.input_kit.eventbus.LocationChangeEvent;
import nl.sense_os.input_kit.helpers.DataCacheHelper;
import nl.sense_os.input_kit.services.apis.AwarenessApiHelper;
import nl.sense_os.input_kit.services.apis.LocationUpdateApiHelper;
import nl.sense_os.input_kit.services.apis.MonitoringGeofenceApiHelper;
import nl.sense_os.input_kit.tasks.PopulateActivityDataTask;
import nl.sense_os.input_kit.tasks.PopulateGeofenceDataTask;
import nl.sense_os.input_kit.tasks.PopulateLocationsDataTask;

import static nl.sense_os.input_kit.constant.InputKitType.Activities;
import static nl.sense_os.input_kit.constant.InputKitType.Geofencing;
import static nl.sense_os.input_kit.constant.InputKitType.LocationUpdates;
import static nl.sense_os.input_kit.constant.InputKitType.START_ALL;
import static nl.sense_os.input_kit.constant.InputKitType.STOP_ALL;
import static nl.sense_os.input_kit.constant.Preference.LOCATION_UPDATE_CONTENT_KEY;

/**
 * Created by panjiyudasetya on 5/26/17.
 */
@SuppressWarnings("SpellCheckingInspection")
public class AwarenessServiceController {
    private static final String TAG = "ASC";
    private static final String KEY_ACTIVATED = String.format("%s_IS_ACTIVE", TAG);
    private static final DataCacheHelper CACHE = new DataCacheHelper();

    private GoogleApiClient mClient;
    private AwarenessApiHelper mAwarenessHelper;
    private MonitoringGeofenceApiHelper mGeofenceHelper;
    private LocationUpdateApiHelper mLocationHelper;
    private int mActionType;

    public AwarenessServiceController(@NonNull Context context,
                                      @NonNull GoogleApiClient client,
                                      @NonNull LocationListener locationListener) {
        this.mClient = client;
        this.mAwarenessHelper = new AwarenessApiHelper(context, mClient);
        this.mGeofenceHelper = new MonitoringGeofenceApiHelper(context, mClient);
        this.mLocationHelper = new LocationUpdateApiHelper(locationListener, mClient);
    }

    public static boolean isServiceActive() {
        return Hawk.get(KEY_ACTIVATED, false);
    }

    public void setIsServiceActive(boolean isServiceActive) {
        Hawk.put(KEY_ACTIVATED, isServiceActive);
    }

    public void handleSubscribeEvent(int actionType) {
        mActionType = actionType;
        if (isSubscribeActions()) {
            startAwareness();
            return;
        }

        if (isUnsubscribeActions()) {
            stopAwareness();
            return;
        }

        if (mActionType == Geofencing.GET_GEOFENCING_HISTORY) {
            consumeGeofencingData();
            return;
        }
    }

    public void onNewLocationDetected(Location location) {
        Content content = new Content(
                Content.LOCATION_UPDATE_TYPE,
                new Content.LocationUpdateBuilder(location).build(),
                System.currentTimeMillis()
        );

        CACHE.save(LOCATION_UPDATE_CONTENT_KEY, content);
        consumeLocationsData(false);
    }

    private boolean isSubscribeActions() {
        return mActionType == START_ALL || mActionType == Activities.SUBSCRIBE
                || mActionType == Geofencing.SUBSCRIBE || mActionType == LocationUpdates.SUBSCRIBE;
    }

    private boolean isUnsubscribeActions() {
        return mActionType == STOP_ALL || mActionType == Activities.UNSUBSCRIBE
                || mActionType == Geofencing.UNSUBSCRIBE || mActionType == LocationUpdates.UNSUBSCRIBE;
    }

    private void startAwareness() {
        if (mActionType == Activities.SUBSCRIBE) consumeActivityData();
        else if (mActionType == Geofencing.SUBSCRIBE) consumeGeofencingData();
        else if (mActionType == LocationUpdates.SUBSCRIBE) consumeLocationsData(true);
        else if (mActionType == START_ALL) {
            consumeActivityData();
            consumeGeofencingData();
            consumeLocationsData(true);
        }
    }

    private void stopAwareness() {
        if (mActionType == Activities.UNSUBSCRIBE) mAwarenessHelper.stopActivityRecognition();
        else if (mActionType == Geofencing.UNSUBSCRIBE) mGeofenceHelper.stopSensingSenseHQGeofences();
        else if (mActionType == LocationUpdates.UNSUBSCRIBE) mLocationHelper.stopLocationUpdates();
        else if (mActionType == STOP_ALL) {
            mAwarenessHelper.stopActivityRecognition();
            mGeofenceHelper.stopSensingSenseHQGeofences();
            mLocationHelper.stopLocationUpdates();
        }
    }

    private void consumeActivityData() {
        mAwarenessHelper.startActivityRecognition();
        new PopulateActivityDataTask() {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                EventBus.getDefault().post(new DetectedActivityEvent(contents));
            }
        }.run();
    }

    private void consumeGeofencingData() {
        mGeofenceHelper.startSensingSenseHQGeofences();
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
