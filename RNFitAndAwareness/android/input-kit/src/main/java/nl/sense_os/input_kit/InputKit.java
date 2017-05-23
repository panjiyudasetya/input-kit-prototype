package nl.sense_os.input_kit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import nl.sense_os.input_kit.constant.ServiceType;
import nl.sense_os.input_kit.services.AwarenessService;
import nl.sense_os.input_kit.services.GoogleFitService;

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
                        ServiceType.Awareness.ACTIVITIES
                )
        );
        Log.d(TAG, "subscribeActivityDetection: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeActivityDetection() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.STOP_ACTIVITIES
                )
        );
        Log.d(TAG, "unsubscribeActivityDetection: unsubcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeLocationUpdates() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.LOCATION_UPDATES
                )
        );
        Log.d(TAG, "subscribeLocationUpdates: subcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeLocationUpdates() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.STOP_LOCATION_UPDATES
                )
        );
        Log.d(TAG, "unsubscribeLocationUpdates: unsubcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeGeofencing() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.GEOFENCING
                )
        );
        Log.d(TAG, "subscribeGeofencing: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeGeofencing() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.STOP_GEOFENCING
                )
        );
        Log.d(TAG, "unsubscribeGeofencing: unsubribed");
    }


    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeDailyStepsCount() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        ServiceType.Fitness.STEPS_COUNT
                )
        );
        Log.d(TAG, "subscribeDailyStepsCount: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeDailyStepsCount() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        ServiceType.Fitness.STOP_STEPS_COUNT
                )
        );
        Log.d(TAG, "unsubscribeDailyStepsCount: unsubcribed");
    }

    public void release() {

    }

}
