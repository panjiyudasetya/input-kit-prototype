package nl.sense_os.inputkit.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is a constants value to define request read permissions for the given SampleType(s).
 *
 * Created by panjiyudasetya on 6/19/17.
 */

public class SampleType {
    private SampleType() { }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SLEEP,
            STEP_COUNT
    })
    public @interface SampleName {}
    public static final String SLEEP = "sleep";
    public static final String STEP_COUNT = "stepCount";
}
