package nl.sense_os.input_kit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import nl.sense_os.input_kit.services.AwarenessService;
import nl.sense_os.input_kit.services.GoogleFitService;

import static nl.sense_os.input_kit.constant.InputKitType.Activities;
import static nl.sense_os.input_kit.constant.InputKitType.Geofencing;
import static nl.sense_os.input_kit.constant.InputKitType.LocationUpdates;
import static nl.sense_os.input_kit.constant.InputKitType.StepsCount;

/**
 * Created by panjiyudasetya on 5/22/17.
 */

public class InputKit {
    private static final String TAG = "INPUT_KIT";
    private Context mContext;
    private static InputKit mInputKitInstance;

    private InputKit(@NonNull Context context) {
        mContext = context;
    }

    public static InputKit getInstance(@NonNull Context context) {
        if (mInputKitInstance == null) {
            mInputKitInstance = new InputKit(context);
        }
        return mInputKitInstance;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeActivityDetection() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        Activities.SUBSCRIBE
                )
        );
        Log.d(TAG, "subscribeActivityDetection: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeActivityDetection() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        Activities.UNSUBSCRIBE
                )
        );
        Log.d(TAG, "unsubscribeActivityDetection: unsubcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeLocationUpdates() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        LocationUpdates.SUBSCRIBE
                )
        );
        Log.d(TAG, "subscribeLocationUpdates: subcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeLocationUpdates() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        LocationUpdates.UNSUBSCRIBE
                )
        );
        Log.d(TAG, "unsubscribeLocationUpdates: unsubcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeGeofencing() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        Geofencing.SUBSCRIBE
                )
        );
        Log.d(TAG, "subscribeGeofencing: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeGeofencing() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        Geofencing.UNSUBSCRIBE
                )
        );
        Log.d(TAG, "unsubscribeGeofencing: unsubribed");
    }


    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeDailyStepsCount() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        StepsCount.SUBSCRIBE
                )
        );
        Log.d(TAG, "subscribeDailyStepsCount: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeDailyStepsCount() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        StepsCount.UNSUBSCRIBE
                )
        );
        Log.d(TAG, "unsubscribeDailyStepsCount: unsubcribed");
    }

    public void getStepsCountHistory() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        StepsCount.GET_STEPS_COUNT
                )
        );
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void getGeofencingHistory() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        Geofencing.GET_GEOFENCING_HISTORY
                )
        );
    }

    public void release() {

    }

}
