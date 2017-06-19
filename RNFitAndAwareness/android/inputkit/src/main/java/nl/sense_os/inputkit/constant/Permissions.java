package nl.sense_os.inputkit.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by panjiyudasetya on 6/19/17.
 */

public class Permissions {
    private Permissions() { }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SLEEP,
            STEP_COUNT
    })
    public @interface PermissionName {}
    public static final String SLEEP = "sleep";
    public static final String STEP_COUNT = "stepCount";
}
