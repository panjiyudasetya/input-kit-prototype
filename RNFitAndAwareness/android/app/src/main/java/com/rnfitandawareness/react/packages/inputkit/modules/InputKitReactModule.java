package com.rnfitandawareness.react.packages.inputkit.modules;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;

/**
 * Created by panjiyudasetya on 5/19/17.
 */

public class InputKitReactModule extends ReactContextBaseJavaModule {
    private static final String INPUT_KIT_MODULE_NAME = "InputKitModule";
    private static final String TAG = INPUT_KIT_MODULE_NAME;
    private Context mContext;

    @SuppressWarnings("unused") // Used by React Native
    public InputKitReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return INPUT_KIT_MODULE_NAME;
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void requestPermissions() {
        String message = "Request Permission clicked";
        Log.d(TAG, message);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void startMeasurements(ReadableArray measurements) {
        String message = "Start Measurements " + measurements;
        Log.d(TAG, message);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @ReactMethod
    @SuppressWarnings("unused") // This is a public API, used by React App
    public void stopMeasurements(ReadableArray measurements) {
        String message = "Stop Measurements " + measurements;
        Log.d(TAG, message);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}