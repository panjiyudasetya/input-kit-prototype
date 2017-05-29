package nl.sense_os.input_kit.receivers;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Locale;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.helpers.AlarmHelper;
import nl.sense_os.input_kit.helpers.DataCacheHelper;

import static nl.sense_os.input_kit.constant.Actions.REPEATING_ALARM;
import static nl.sense_os.input_kit.constant.Actions.SELF_SCHEDULING_ALARM;
import static nl.sense_os.input_kit.constant.Extras.EXPECTED_FIRING_TIME;
import static nl.sense_os.input_kit.constant.Extras.EXPECTED_INTERVAL;
import static nl.sense_os.input_kit.constant.Extras.SCHEDULING_TIME;
import static nl.sense_os.input_kit.constant.Preference.ALARM_HISTORY_CONTENT_KEY;

public class AlarmReceiver extends BaseReceiver {
    private static final DataCacheHelper CACHE = new DataCacheHelper();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (action == null) return;
        if (REPEATING_ALARM.equals(action)) {
            processRepeatingAlarm(intent);
        } else if (SELF_SCHEDULING_ALARM.equals(action)) {
            processSelfSchedulingAlarm(intent);
        }
    }

    private void processSelfSchedulingAlarm(Intent intent) {
        logSelfSchedulingAlarmMessage(intent);
        scheduleNextAlarm();
    }

    private void logSelfSchedulingAlarmMessage(Intent intent) {
        final String message = String.format(Locale.US,
                "Scheduled at %tT, expected firing at %tT",
                intent.getLongExtra(SCHEDULING_TIME, 0),
                intent.getLongExtra(EXPECTED_FIRING_TIME, 0));

        saveHistory(message);
    }

    private void scheduleNextAlarm() {
        AlarmHelper alarmHelper = new AlarmHelper(mContext);
        alarmHelper.setNextSelfSchedulingAlarm();
    }

    private void processRepeatingAlarm(Intent intent) {
        final String message = String.format(Locale.US,
                "Scheduled at %tT, expected first firing at %tT, repeating every %d minutes",
                intent.getLongExtra(SCHEDULING_TIME, 0),
                intent.getLongExtra(EXPECTED_FIRING_TIME, 0),
                intent.getIntExtra(EXPECTED_INTERVAL, 0));

        saveHistory(message);
    }

    private void saveHistory(@NonNull String message) {
        CACHE.save(ALARM_HISTORY_CONTENT_KEY, new Content(
                Content.ALARM_TYPE,
                message,
                System.currentTimeMillis()
        ));
    }
}
