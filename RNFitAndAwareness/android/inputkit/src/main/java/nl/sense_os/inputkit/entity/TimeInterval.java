package nl.sense_os.inputkit.entity;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import nl.sense_os.inputkit.constant.Interval;

import static nl.sense_os.inputkit.constant.Interval.DAY;
import static nl.sense_os.inputkit.constant.Interval.HALF_HOUR;
import static nl.sense_os.inputkit.constant.Interval.HOUR;
import static nl.sense_os.inputkit.constant.Interval.TEN_MINUTE;
import static nl.sense_os.inputkit.constant.Interval.WEEK;

/**
 * Created by panjiyudasetya on 6/19/17.
 */

public class TimeInterval {
    private int mValue;
    private TimeUnit mTimeUnit;

    public TimeInterval(@NonNull @Interval.IntervalName String type) {
        setValue(type);
    }

    public int getValue() {
        return mValue;
    }

    public TimeUnit getTimeUnit() {
        return mTimeUnit;
    }

    private void setValue(@Interval.IntervalName String type) {
        if (type.equals(WEEK)) {
            mValue = 7;
            mTimeUnit = TimeUnit.DAYS;
        } else if (type.equals(DAY)) {
            mValue = 1;
            mTimeUnit = TimeUnit.DAYS;
        } else if (type.equals(HOUR)) {
            mValue = 1;
            mTimeUnit = TimeUnit.HOURS;
        } else if (type.equals(HALF_HOUR)) {
            mValue = 30;
            mTimeUnit = TimeUnit.MINUTES;
        } else if (type.equals(TEN_MINUTE)) {
            mValue = 10;
            mTimeUnit = TimeUnit.MINUTES;
        } else {
            mValue = 1;
            mTimeUnit = TimeUnit.DAYS;
        }
    }
}
