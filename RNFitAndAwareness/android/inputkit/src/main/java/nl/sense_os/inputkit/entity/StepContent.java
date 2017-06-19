package nl.sense_os.inputkit.entity;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

/**
 * Created by panjiyudasetya on 6/15/17.
 */

public class StepContent {
    private static final Gson GSON = new Gson();
    private boolean isQueryOk;
    private List<Step> steps;

    public StepContent(boolean isQueryOk, List<Step> steps) {
        this.isQueryOk = isQueryOk;
        this.steps = steps;
    }

    public String toJson() {
        return GSON.toJson(steps == null ? Collections.emptyList() : steps);
    }

    public int getTotalSteps() {
        int total = 0;
        if (steps != null) {
            for (Step step : steps) {
                total += step.getValue();
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return "StepContent{"
                + "isQueryOk="
                + isQueryOk
                + ", steps="
                + steps + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StepContent)) return false;

        StepContent that = (StepContent) o;

        if (isQueryOk != that.isQueryOk) return false;
        return steps != null ? steps.equals(that.steps) : that.steps == null;

    }

    @Override
    public int hashCode() {
        int result = (isQueryOk ? 1 : 0);
        result = 31 * result + (steps != null ? steps.hashCode() : 0);
        return result;
    }
}
