package nl.sense_os.input_kit.entities;

import android.location.Location;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.util.Map;

/**
 * Created by panjiyudasetya on 5/3/17.
 */

public class Content {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ STEPS_TYPE, GEOFENCE_TYPE, ACTIVITY_TYPE, LOCATION_UPDATE_TYPE, ALARM_TYPE})
    public @interface ContentType { }
    public static final int STEPS_TYPE = 0;
    @SuppressWarnings("SpellCheckingInspection")
    public static final int GEOFENCE_TYPE = 1;
    public static final int ACTIVITY_TYPE = 2;
    public static final int LOCATION_UPDATE_TYPE = 3;
    public static final int ALARM_TYPE = 4;

    @SerializedName("type")
    private int type;
    @SerializedName("content")
    private String content;
    @SerializedName("time_stamp")
    private long timeStamp;

    public Content(@ContentType int type, String content, long timeStamp) {
        this.type = type;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public static class StepBuilder {
        private String dataPointType;
        private String startTimeDetected;
        private String endTimeDetected;
        private Map<String, String> fields;

        public StepBuilder dataPointType(String dataPointType) {
            this.dataPointType = dataPointType;
            return this;
        }

        public StepBuilder startTimeDetected(String startTimeDetected) {
            this.startTimeDetected = startTimeDetected;
            return this;
        }

        public StepBuilder endTimeDetected(String endTimeDetected) {
            this.endTimeDetected = endTimeDetected;
            return this;
        }

        public StepBuilder fields(Map<String, String> fields) {
            this.fields = fields;
            return this;
        }

        public String build() {
            String strContent = "Data point:\n"
                    + "\tType: " + dataPointType + "\n"
                    + "\tStart: " + startTimeDetected  + "\n"
                    + "\tEnd: " + endTimeDetected + "\n";

            String strFields = "";
            if (fields != null && fields.size() > 0) {
                strFields += "\n";
                int i = 0;
                for (Map.Entry<String, String> field : fields.entrySet()) {
                    strFields += (i == 0 ? "" : "\n") + "\tField: "
                            + field.getKey() + ", Value: " + field.getValue();
                    i++;
                }
            }
            return strContent + strFields;
        }
    }

    public static class LocationBuilder {
        private String info;
        private long recordedTime;
        private static final DateFormat DT_FORMAT = DateFormat.getDateTimeInstance();

        public LocationBuilder info(String info) {
            this.info = info;
            return this;
        }

        public LocationBuilder recordedTime(long recordedTime) {
            this.recordedTime = recordedTime;
            return this;
        }

        public String build() {
            String strContent = "Geofencing Triggered :\n"
                    + "\tInfo: " + info + "\n"
                    + "\tDetected at: " + DT_FORMAT.format(recordedTime);
            return strContent;
        }
    }

    public static class ActivityBuilder {
        private String activity;
        private int confidence;
        private long recordedTime;
        private static final DateFormat DT_FORMAT = DateFormat.getDateTimeInstance();

        public ActivityBuilder activity(String activity) {
            this.activity = activity;
            return this;
        }

        public ActivityBuilder confidence(int confidence) {
            this.confidence = confidence;
            return this;
        }

        public ActivityBuilder recordedTime(long recordedTime) {
            this.recordedTime = recordedTime;
            return this;
        }

        public String build() {
            String strContent = "Some Activity Detected :\n"
                    + "\tType: " + activity + "\n"
                    + "\tConfidence: " + confidence + "/100\n"
                    + "\tDetected at: " + DT_FORMAT.format(recordedTime);
            return strContent;
        }
    }

    public static class LocationUpdateBuilder {
        private Location location;
        private static final DateFormat DT_FORMAT = DateFormat.getDateTimeInstance();

        public LocationUpdateBuilder(@NonNull Location location) {
            this.location = location;
        }

        public String build() {
            String strContent = "Update Location Detected :\n"
                    + "\tType: " + location.getProvider() + "\n"
                    + "\tLatitude: " + location.getLatitude() + "\n"
                    + "\tLongitude:" + location.getLongitude() + "\n"
                    + "\tDetected at: " + DT_FORMAT.format(location.getTime());
            return strContent;
        }
    }
}
