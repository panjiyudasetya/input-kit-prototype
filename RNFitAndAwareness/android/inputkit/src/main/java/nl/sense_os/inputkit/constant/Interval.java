package nl.sense_os.inputkit.constant;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by panjiyudasetya on 6/19/17.
 */

public class Interval {
    private Interval() { }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            WEEK,
            DAY,
            HOUR,
            HALF_HOUR,
            TEN_MINUTE
    })
    public @interface IntervalName {}
    public static final String WEEK = "week";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String HALF_HOUR = "halfHour";
    public static final String TEN_MINUTE = "tenMinute";
}
