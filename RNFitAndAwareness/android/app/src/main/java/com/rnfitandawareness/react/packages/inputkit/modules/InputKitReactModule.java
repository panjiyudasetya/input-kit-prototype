package com.rnfitandawareness.react.packages.inputkit.modules;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.rnfitandawareness.BaseActivity;

import nl.sense_os.input_kit.InputKit;

/**
 * Created by panjiyudasetya on 5/19/17.
 */

public class InputKitReactModule extends ReactContextBaseJavaModule {
    private static final String INPUT_KIT_MODULE_NAME = "InputKitModule";
    private static final String TAG = INPUT_KIT_MODULE_NAME;
    private ReactApplicationContext mReactContext;
    private InputKit mInputKit;

    @SuppressWarnings("unused") // Used by React Native
    public InputKitReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mInputKit = InputKit.init(mReactContext);
    }

    @Override
    public String getName() {
        return INPUT_KIT_MODULE_NAME;
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void requestPermissions() {
        String message = "Request Permission clicked";
        Activity activity = getCurrentActivity();
        if (activity != null && BaseActivity.class.isInstance(activity)) {
            ((BaseActivity) activity).requestAllPermissions();
        }
        Log.d(TAG, message);
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void startMeasurements(ReadableArray measurements) {
        String message = "Start Measurements " + measurements;
        Log.d(TAG, message);
        Toast.makeText(mReactContext, message, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void stopMeasurements(ReadableArray measurements) {
        String message = "Stop Measurements " + measurements;
        Log.d(TAG, message);
        Toast.makeText(mReactContext, message, Toast.LENGTH_SHORT).show();
    }
}