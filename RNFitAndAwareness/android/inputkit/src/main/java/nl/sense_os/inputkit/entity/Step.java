package nl.sense_os.inputkit.entity;

import com.google.gson.Gson;

/**
 * Created by panjiyudasetya on 6/15/17.
 */

public class Step {
    private static final Gson GSON = new Gson();
    private int value;
    private long startDate;
    private long endData;

    public Step(int value, long startDate, long endData) {
        this.value = value;
        this.startDate = startDate;
        this.endData = endData;
    }

    public int getValue() {
        return value;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    @Override
    public String toString() {
        return "Step{"
                + "value="
                + value
                + ", startDate="
                + startDate
                + ", endData="
                + endData + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Step)) return false;

        Step step = (Step) o;

        if (value != step.value) return false;
        if (startDate != step.startDate) return false;
        return endData == step.endData;

    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (int) (startDate ^ (startDate >>> 32));
        result = 31 * result + (int) (endData ^ (endData >>> 32));
        return result;
    }
}
