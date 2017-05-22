package nl.sense_os.input_kit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import nl.sense_os.input_kit.constant.ServiceType;
import nl.sense_os.input_kit.eventbus.DetectedStepsCountEvent;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;
import nl.sense_os.input_kit.services.AwarenessService;
import nl.sense_os.input_kit.services.GoogleFitService;

/**
 * Created by panjiyudasetya on 5/22/17.
 */

public class InputKit {
    private static final String TAG = "INPUT_KIT";
    private Context mContext;

    private InputKit(@NonNull Context context) {
        mContext = context;
        Hawk.init(context).build();
        EventBus.getDefault().register(this);
    }

    public static InputKit init(@NonNull Context context) {
        return new InputKit(context);
    }

    @SuppressWarnings("SpellCheckingInspection")//This function being used by EventBus
    public void subscribeGeofencing() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.GEOFENCING
                )
        );
    }

    @SuppressWarnings("SpellCheckingInspection")//This function being used by EventBus
    public void unsubscribeGeofencing() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.STOP_GEOFENCING
                )
        );
    }


    @SuppressWarnings("SpellCheckingInspection")//This function being used by EventBus
    public void subscribeDailyStepsCount() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        ServiceType.Fitness.STEPS_COUNT
                )
        );
    }

    @SuppressWarnings("SpellCheckingInspection")//This function being used by EventBus
    public void unsubscribeDailyStepsCount() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        ServiceType.Fitness.STOP_STEPS_COUNT
                )
        );
    }

    public void release() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    @SuppressWarnings("unused")//This function being used by EventBus
    public void onDetectedStepsCountEvent(@Nullable DetectedStepsCountEvent event) {
    }

    @Subscribe
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//This function being used by EventBus
    public void onGeofenceEvent(GeofenceEvent event) {
    }
}
