package com.rnfitandawareness;

import android.os.Bundle;

import nl.sense_os.input_kit.helpers.AlarmHelper;

public class MainActivity extends BaseActivity {
    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "RNFitAndAwareness";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAlarm();
    }

    private void setAlarm() {
        AlarmHelper alarmHelper = new AlarmHelper(this);
        alarmHelper.startRepeatingAlarm();
        alarmHelper.setNextSelfSchedulingAlarm();
    }
}
