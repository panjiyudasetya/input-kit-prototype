package nl.sense_os.input_kit.constant;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class InputKitEventName {
    private InputKitEventName() { }
    public static final String PLAY_SERVICE_CONNECTION_EVENT = "PLAY_SERVICE_CONNECTION_EVENT";
    public static final String COLLECT_STEPS_COUNT_EVENT = "COLLECT_STEPS_COUNT_EVENT";
    public static final String SUBSCRIBE_STEPS_COUNT_EVENT = "SUBSCRIBE_STEPS_COUNT_EVENT";
    public static final String UNSUBSCRIBE_STEPS_COUNT_EVENT = "UNSUBSCRIBE_STEPS_COUNT_EVENT";
    public static final String SUBSCRIBE_ACTIVITY_DETECTION_EVENT = "SUBSCRIBE_ACTIVITY_DETECTION_EVENT";
    public static final String UNSUBSCRIBE_ACTIVITY_DETECTION_EVENT = "UNSUBSCRIBE_ACTIVITY_DETECTION_EVENT";
    public static final String SUBSCRIBE_GEOFENCE_EVENT = "SUBSCRIBE_GEOFENCE_EVENT";
    public static final String UNSUBSCRIBE_GEOFENCE_EVENT = "UNSUBSCRIBE_GEOFENCE_EVENT";
    public static final String COLLECT_GEOFENCE_EVENT = "COLLECT_GEOFENCE_EVENT";
    public static final String LOCATION_UPDATES_EVENT = "LOCATION_UPDATES_EVENT";
}
