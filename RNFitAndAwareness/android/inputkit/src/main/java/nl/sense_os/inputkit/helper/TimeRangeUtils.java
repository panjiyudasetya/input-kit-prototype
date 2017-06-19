package nl.sense_os.inputkit.helper;

import android.util.Log;
import android.util.Pair;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by panjiyudasetya on 6/19/17.
 */

public class TimeRangeUtils {
    private static final String TAG = "SensorOptions";
    private static final DateFormat DATE_TIME_INSTANCE = DateFormat.getDateTimeInstance();
    /**
     * Helper function to determine time range of one day before given end time.
     *
     * @param endTime   End time of step detection
     * @return  Pair
     */
    public static Pair<Long, Long> populateOneDayBeforeTimeRange(long endTime) {
        long startTime = getOneDayDifference(true, endTime);

        Log.i(TAG, "Range Start: " + DATE_TIME_INSTANCE.format(startTime));
        Log.i(TAG, "Range End: " + DATE_TIME_INSTANCE.format(endTime));

        return Pair.create(startTime, endTime);
    }
    /**
     * Helper function to determine time range of one day after given end time.
     *
     * @param endTime   End time of step detection
     * @return  Pair
     */
    public static Pair<Long, Long> populateOneDayAfterTimeRange(long endTime) {
        long startTime = getOneDayDifference(false, endTime);

        Log.i(TAG, "Range Start: " + DATE_TIME_INSTANCE.format(startTime));
        Log.i(TAG, "Range End: " + DATE_TIME_INSTANCE.format(endTime));

        return Pair.create(startTime, endTime);
    }

    private static long getOneDayDifference(boolean isOneDayBefore, long endTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        cal.add(Calendar.DAY_OF_MONTH, isOneDayBefore ? -1 : 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }
}
