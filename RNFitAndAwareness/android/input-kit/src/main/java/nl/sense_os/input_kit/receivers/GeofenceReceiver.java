package nl.sense_os.input_kit.receivers;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import nl.sense_os.input_kit.constant.Preference;
import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;
import nl.sense_os.input_kit.helpers.DataCacheHelper;
import nl.sense_os.input_kit.tasks.PopulateGeofenceDataTask;

import static nl.sense_os.input_kit.helpers.NotificationHelper.createNotification;

@SuppressWarnings("SpellCheckingInspection")
public class GeofenceReceiver extends BaseReceiver {
    private static final String TAG = "GEOFENCE_RECEIVER";
    private static final DataCacheHelper CACHE = new DataCacheHelper();
    private static final String GEOFENCE_EVENT_TITLE = "Geofence Event Triggered";
    private static final int GEOFENCE_EVENT_NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "Event received");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String message = getErrorString(geofencingEvent.getErrorCode());
            Log.w(TAG, "Error received. " + message);
            logErrorEvent(message);
            return;
        }

        handleGeofence(geofencingEvent);
    }

    private void handleGeofence(@NonNull GeofencingEvent geofencingEvent) {
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    geofenceTransition,
                    triggeringGeofences
            );

            saveGeofenceEvent(geofenceTransitionDetails);
        }
    }

    private void logErrorEvent(@NonNull String errorMessage) {
        long recordedTime = System.currentTimeMillis();
        saveGeofenceEvent(new Content(
                Content.GEOFENCE_TYPE,
                new Content.LocationBuilder()
                        .info(errorMessage)
                        .recordedTime(recordedTime)
                        .build(),
                recordedTime
        ));
    }

    private void saveGeofenceEvent(@NonNull String enterMessage) {
        long recordedTime = System.currentTimeMillis();
        saveGeofenceEvent(new Content(
                Content.GEOFENCE_TYPE,
                new Content.LocationBuilder()
                        .info(enterMessage)
                        .recordedTime(recordedTime)
                        .build(),
                recordedTime
        ));
    }

    private void saveGeofenceEvent(@NonNull Content content) {
        // Save geofence event
        CACHE.save(Preference.GEOFENCE_CONTENT_KEY, content);

        // Broadcast geofence event
        consumeGeofenceData();

        // Create notification
        createNotification(
                mContext,
                GEOFENCE_EVENT_TITLE,
                content.getContent(),
                GEOFENCE_EVENT_NOTIFICATION_ID
        );
    }

    private void consumeGeofenceData() {
        new PopulateGeofenceDataTask() {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                EventBus.getDefault()
                        .post(new GeofenceEvent(contents));
            }
        }.run();
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            @NonNull List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + " : " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "User entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "User exited";
            default:
                return "Unknown geofence transition";
        }
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your app has registered too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have provided too many PendingIntents to the addGeofences() call";
            default:
                return "Unknown error: the Geofence service is not available now";
        }
    }
}
