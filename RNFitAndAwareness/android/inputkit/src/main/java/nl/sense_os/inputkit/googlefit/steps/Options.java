package nl.sense_os.inputkit.googlefit.steps;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static nl.sense_os.inputkit.googlefit.steps.Options.Validator.validateEndTime;
import static nl.sense_os.inputkit.googlefit.steps.Options.Validator.validateStartTime;
import static nl.sense_os.inputkit.helper.TimeRangeUtils.populateOneDayBeforeTimeRange;

/**
 * Created by panjiyudasetya on 6/19/17.
 */

public class Options {
    private static final int DEFAULT_TIME_AGGREGATE = 10;
    private static final TimeUnit DEFAULT_TIME_UNIT_AGGREGATE = TimeUnit.MINUTES;

    private long startTime;
    private long endTime;
    private boolean useDataAggregation;
    private int timeAggregation;
    private TimeUnit timeUnitAggregation;

    private Options(long startTime,
                    long endTime,
                    boolean useDataAggregation,
                    int timeAggregation,
                    TimeUnit timeUnitAggregation) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.useDataAggregation = useDataAggregation;
        this.timeAggregation = timeAggregation;
        this.timeUnitAggregation = timeUnitAggregation;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isUseDataAggregation() {
        return useDataAggregation;
    }

    public int getTimeAggregation() {
        return timeAggregation;
    }

    public TimeUnit getTimeUnitAggregation() {
        return timeUnitAggregation;
    }

    public static Options getDefaultOptions() {
        long startTime = new Date().getTime();
        Pair<Long, Long> timeRange = populateOneDayBeforeTimeRange(startTime);
        return new Options(
                timeRange.first,
                timeRange.second,
                true,
                DEFAULT_TIME_AGGREGATE,
                DEFAULT_TIME_UNIT_AGGREGATE
        );
    }

    public static class Builder {
        private long newStartTime;
        private long newEndTime;
        private boolean newUseDataAggregation;
        private int newTimeAggregation;
        private TimeUnit newTimeUnitAggregation;

        /**
         * Set start time of steps history.
         * @param startTime epoch
         * @return Builder SensorOptions Builder
         */
        public Builder startTime(long startTime) {
            this.newStartTime = startTime;
            return this;
        }

        /**
         * Set end time of steps history.
         * @param endTime epoch
         * @return Builder SensorOptions Builder
         */
        public Builder endTime(long endTime) {
            this.newEndTime = endTime;
            return this;
        }

        /**
         * It will aggregating steps count data history by specific time and time unit.
         * @return Builder SensorOptions Builder
         */
        public Builder useDataAgregation() {
            this.newUseDataAggregation = true;
            return this;
        }

        /**
         * If time aggregation not provided it will be set to 10.
         * @param timeAggregation time aggregation
         * @return Builder SensorOptions Builder
         */
        public Builder timeAggregation(int timeAggregation) {
            this.newTimeAggregation = timeAggregation;
            return this;
        }

        /**
         * If time unit aggregation not provided it will be set to {@link TimeUnit#MINUTES}
         * @param timeUnitAggregation time unit aggregation
         * @return Builder SensorOptions Builder
         */
        public Builder timeUnitAggregation(@NonNull TimeUnit timeUnitAggregation) {
            this.newTimeUnitAggregation = timeUnitAggregation;
            return this;
        }

        public Options build() {
            newStartTime = validateStartTime(newStartTime);
            newEndTime = validateEndTime(newStartTime, newEndTime);

            if (!newUseDataAggregation) {
                newTimeAggregation = DEFAULT_TIME_AGGREGATE;
                newTimeUnitAggregation = DEFAULT_TIME_UNIT_AGGREGATE;
            }

            return new Options(newStartTime,
                    newEndTime,
                    newUseDataAggregation,
                    newTimeAggregation == 0 ? DEFAULT_TIME_AGGREGATE : newTimeAggregation,
                    newTimeUnitAggregation == null ? DEFAULT_TIME_UNIT_AGGREGATE : newTimeUnitAggregation
            );
        }
    }

    static class Validator {
        /**
         * Validate start time value. If lower than 0, it will be set to 0
         * @param startTime epoch
         * @return valid start time
         */
        static long validateStartTime(long startTime) {
            return startTime < 0 ? 0 : startTime;
        }

        /**
         * Validate end time value. If end time lower than 0, it will be set to 0
         *
         * @param startTime epoch
         * @param endTime epoch
         * @return valid end time
         * @throws IllegalStateException if end time lower than start time
         */
        static long validateEndTime(long startTime, long endTime) {
            endTime = endTime < 0 ? 0 : endTime;

            if (endTime < startTime) {
                throw new IllegalStateException("End time cannot be lower than start time!");
            }
            return endTime;
        }
    }
}
