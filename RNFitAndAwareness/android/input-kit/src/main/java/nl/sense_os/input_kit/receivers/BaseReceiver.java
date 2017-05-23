package nl.sense_os.input_kit.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.hawk.Hawk;

import nl.sense_os.input_kit.services.AwarenessService;
import nl.sense_os.input_kit.services.GoogleFitService;
import nl.sense_os.input_kit.constant.ServiceType;
import nl.sense_os.input_kit.helpers.AlarmHelper;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public abstract class BaseReceiver extends BroadcastReceiver {
    protected Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Hawk.init(mContext).build();
        wakingUpServices();
    }

    protected void scheduleAlarms() {
        AlarmHelper alarmHelper = new AlarmHelper(mContext);
        alarmHelper.setNextSelfSchedulingAlarm();
        alarmHelper.startRepeatingAlarm();
    }

    protected void wakingUpServices() {
        if (!AwarenessService.isActive()) wakingUpAwarenessService();
        if (!GoogleFitService.isActive()) wakingUpFitnessService();
    }

    private void wakingUpAwarenessService() {
        mContext.startService(
                AwarenessService.withContext(
                        mContext,
                        ServiceType.Awareness.ALL
                )
        );
    }

    private void wakingUpFitnessService() {
        mContext.startService(
                GoogleFitService.withContext(
                        mContext,
                        ServiceType.Awareness.ALL
                )
        );
    }
}