package com.rnfitandawareness.react.packages.inputkit.modules;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.rnfitandawareness.BaseActivity;
import com.rnfitandawareness.helpers.JsonHelper;
import com.rnfitandawareness.react.packages.inputkit.constants.Measurement;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import nl.sense_os.input_kit.InputKit;
import nl.sense_os.input_kit.eventbus.DetectedStepsCountEvent;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;

/**
 * Created by panjiyudasetya on 5/19/17.
 */

public class InputKitReactModule extends ReactContextBaseJavaModule {
    private static final String INPUT_KIT_MODULE_NAME = "InputKitModule";
    private static final String STEPS_COUNT_EMIT_EVENT = "StepsCountEmitEvent";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String GEOFENCING_EMIT_EVENT = "GeofencingEmitEvent";
    private static final String TAG = INPUT_KIT_MODULE_NAME;
    private ReactApplicationContext mReactContext;
    private InputKit mInputKit;

    @SuppressWarnings("unused") // Used by React Native
    public InputKitReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mInputKit = InputKit.getInstance(mReactContext);
    }

    @Override
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getName() {
        return INPUT_KIT_MODULE_NAME;
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void requestPermissions() {
        String message = "Request Permission clicked";
        checkAndCallRequiredPermissions(false);
        Log.d(TAG, message);
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void startMeasurements(ReadableArray measurements) {
        String message = "Start Measurements " + measurements;
        Log.d(TAG, message);

        if (checkAndCallRequiredPermissions(true)) {
            checkMeasurementsArguments(measurements);
            startMonitoring(measurements);
        }

        Toast.makeText(mReactContext, message, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void stopMeasurements(ReadableArray measurements) {
        String message = "Stop Measurements " + measurements;
        Log.d(TAG, message);

        if (checkAndCallRequiredPermissions(true)) {
            checkMeasurementsArguments(measurements);
            stopMonitoring(measurements);
        }

        Toast.makeText(mReactContext, message, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void getStepsCountHistory() {
        mInputKit.getStepsCountHistory();
    }

    @ReactMethod
    @SuppressWarnings({"unused", "SpellCheckingInspection"}) // This is a public API, used by React App
    public void getGeofencingHistory() {
        mInputKit.getGeofencingHistory();
    }

    @Subscribe
    @SuppressWarnings("unused")//This function being used by EventBus
    public void onDetectedStepsCountEvent(final @Nullable DetectedStepsCountEvent event) {
        Log.d(TAG, "onDetectedStepsCountEvent: " + event.toString());
        new AsyncTask<Void, Integer, Exception>() {
            private String contentsJson;
            @Override
            protected Exception doInBackground(Void... voids) {
                contentsJson = JsonHelper.toJson("steps_count_event", event.getContents());
                return null;
            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                Log.d(TAG, "onPostExecute: StepsCountEvent Triggered - " + contentsJson);
                if (contentsJson == null || e != null || mReactContext == null) return;

                mReactContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(STEPS_COUNT_EMIT_EVENT, contentsJson);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//This function being used by EventBus
    public void onGeofenceEvent(final @Nullable GeofenceEvent event) {
        Log.d(TAG, "onGeofenceEvent: " + event.toString());
        new AsyncTask<Void, Integer, Exception>() {
            private String contentsJson;
            @Override
            protected Exception doInBackground(Void... voids) {
                contentsJson = JsonHelper.toJson("geofence_event", event.getContents());
                return null;
            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                Log.d(TAG, "onPostExecute: GeofenceEvent Triggered - " + contentsJson);
                if (contentsJson == null || e != null || mReactContext == null) return;

                mReactContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(GEOFENCING_EMIT_EVENT, contentsJson);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void checkMeasurementsArguments(@Nullable ReadableArray measurements) {
        if (measurements == null) return;

        String illegalArgs = "Measurement types should be in number format collections!";
        for (int i = 0; i < measurements.size(); i++) {
            ReadableType typeIndex = measurements.getType(i);
            if (typeIndex != ReadableType.Number) throw new IllegalArgumentException(illegalArgs);
        }
    }

    private boolean checkAndCallRequiredPermissions(boolean isOnlyCheck) {
        Activity activity = getCurrentActivity();
        if (activity != null && BaseActivity.class.isInstance(activity)) {
            if (!isOnlyCheck) return ((BaseActivity) activity).checkAndCallRequiredPermissions();
            else return ((BaseActivity) activity).checkAndShowPermissionsDialog();
        }
        return false;
    }

    private void startMonitoring(ReadableArray measurements) {
        for (int i = 0; i < measurements.size(); i++) {
            int measurementType = measurements.getInt(i);
            switch (measurementType) {
                case Measurement.STEPS_COUNT:
                    mInputKit.subscribeDailyStepsCount();
                    break;
                case Measurement.GEOFENCING:
                    mInputKit.subscribeGeofencing();
                    break;
                default: break;
            }
        }
    }

    private void stopMonitoring(ReadableArray measurements) {
        for (int i = 0; i < measurements.size(); i++) {
            int measurementType = measurements.getInt(i);
            switch (measurementType) {
                case Measurement.STEPS_COUNT:
                    mInputKit.unsubscribeDailyStepsCount();
                    break;
                case Measurement.GEOFENCING:
                    mInputKit.unsubscribeGeofencing();
                    break;
                default: break;
            }
        }
    }
}