package nl.sense_os.input_kit.receivers;

import android.content.Context;
import android.content.Intent;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public class BootReceiver extends BaseReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        scheduleAlarms();
    }
}
