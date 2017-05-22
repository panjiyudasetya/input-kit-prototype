package nl.sense_os.input_kit.services.apis;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.concurrent.TimeUnit;

import nl.sense_os.input_kit.receivers.ActivityReceiver;

public class AwarenessApiHelper {
    private final Context context;
    private final GoogleApiClient googleApiClient;
    @SuppressWarnings("SpellCheckingInspection")
    private final PendingIntent activitiesPendingIntent;

    public AwarenessApiHelper(Context context, GoogleApiClient googleApiClient) {
        this.context = context;
        this.googleApiClient = googleApiClient;
        this.activitiesPendingIntent = createActivityIntentReceiver();
    }

    public void requestUpdateActivity() {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                googleApiClient,
                TimeUnit.MINUTES.toMillis(5),
                activitiesPendingIntent
        );
    }

    public void stopRequestUpdateActivity() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                googleApiClient,
                activitiesPendingIntent
        );
    }

    private PendingIntent createActivityIntentReceiver() {
        Intent activityIntentReceiver = new Intent(context, ActivityReceiver.class);
        return PendingIntent.getBroadcast(context, 0, activityIntentReceiver, 0);
    }
}
