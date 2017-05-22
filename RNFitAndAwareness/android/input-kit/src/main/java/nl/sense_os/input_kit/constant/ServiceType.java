package nl.sense_os.input_kit.constant;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public class ServiceType {
    private ServiceType() { }

    public class Awareness {
        private Awareness() { }

        @SuppressWarnings("SpellCheckingInspection")
        public static final int GEOFENCING = 1;
        @SuppressWarnings("SpellCheckingInspection")
        public static final int STOP_GEOFENCING = 2;
        public static final int ACTIVITIES = 3;
        public static final int STOP_ACTIVITIES = 4;
        public static final int LOCATION_UPDATES = 5;
        public static final int STOP_LOCATION_UPDATES = 6;
        public static final int ALL = 7;
        public static final int STOP_ALL = 8;
    }

    public class Fitness {
        private Fitness() { }
        public static final int STEPS_COUNT = 9;
        public static final int STOP_STEPS_COUNT = 10;
        public static final int ALL = 11;
        public static final int STOP_ALL = 12;
    }
}
