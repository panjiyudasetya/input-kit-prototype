package nl.sense_os.input_kit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by panjiyudasetya on 3/29/17.
 */

public class DateUtils {
    private static final int SECOND_TO_MILLIS_MULTIPLIER = 1000;
    public static String toReadableFormat(double epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date((long) epoch * (long) SECOND_TO_MILLIS_MULTIPLIER));
    }
}
