package nl.sense_os.input_kit.constant;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public class InputKitType {
    private InputKitType() { }
    public static final int START_ALL = 1;
    public static final int STOP_ALL = 2;
    @SuppressWarnings("SpellCheckingInspection")
    public static class Geofencing {
        private static final String GEOFENCING = "Geofencing";
        public static final int SUBSCRIBE = GEOFENCING.hashCode() + 1;
        public static final int UNSUBSCRIBE = GEOFENCING.hashCode() + 2;
        public static final int GET_GEOFENCING_HISTORY = GEOFENCING.hashCode() + 3;
    }
    @SuppressWarnings("SpellCheckingInspection")
    public static class Activities {
        private static final String ACTIVITIES = "Activities";
        private Activities() { }
        public static final int SUBSCRIBE = ACTIVITIES.hashCode() + 1;
        public static final int UNSUBSCRIBE = ACTIVITIES.hashCode() + 2;

    }
    @SuppressWarnings("SpellCheckingInspection")
    public static class LocationUpdates {
        private static final String LOCATION_UPDATES = "LocationUpdates";
        private LocationUpdates() { }
        public static final int SUBSCRIBE = LOCATION_UPDATES.hashCode() + 1;
        public static final int UNSUBSCRIBE = LOCATION_UPDATES.hashCode() + 2;
    }
    @SuppressWarnings("SpellCheckingInspection")
    public static class StepsCount {
        private static final String STEPS_COUNT = "StepsCount";
        private StepsCount() { }
        public static final int SUBSCRIBE = STEPS_COUNT.hashCode() + 1;
        public static final int UNSUBSCRIBE = STEPS_COUNT.hashCode() + 2;
        public static final int GET_STEPS_COUNT = STEPS_COUNT.hashCode() + 3;
    }
}
