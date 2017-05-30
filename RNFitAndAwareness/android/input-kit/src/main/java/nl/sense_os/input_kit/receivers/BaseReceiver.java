package nl.sense_os.input_kit.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nl.sense_os.input_kit.helpers.AlarmHelper;
import nl.sense_os.input_kit.services.InputKitService;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public abstract class BaseReceiver extends BroadcastReceiver {
    protected Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        wakingUpServices();
    }

    protected void scheduleAlarms() {
        AlarmHelper alarmHelper = new AlarmHelper(mContext);
        alarmHelper.setNextSelfSchedulingAlarm();
        alarmHelper.startRepeatingAlarm();
    }

    protected void wakingUpServices() {
        mContext.startService(InputKitService.withContext(mContext));
    }
}