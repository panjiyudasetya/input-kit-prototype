package com.rnfitandawareness.react.packages.inputkit.modules;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.google.gson.Gson;
import com.rnfitandawareness.BaseActivity;

import nl.sense_os.input_kit.InputKit;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public abstract class InputKitReactModule extends ReactContextBaseJavaModule {
    protected static final Gson GSON = new Gson();
    protected ReactApplicationContext mReactContext;
    protected InputKit mInputKit;
    public InputKitReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mInputKit = InputKit.getInstance(mReactContext);
    }

    protected void connectToInputKit(@NonNull String eventName,
                                     @NonNull InputKitConnectionListener listener) {
        mInputKit.connect(eventName, listener);
    }

    protected boolean checkPermissions(boolean callRequestPermission) {
        if (BaseActivity.class.isInstance(getCurrentActivity())) {
            if (callRequestPermission) {
                return ((BaseActivity) getCurrentActivity())
                        .checkAndCallRequiredPermissions();
            } else {
                return ((BaseActivity) getCurrentActivity())
                        .showPermissionsDialogIfNotGranted();
            }
        }
        return false;
    }
}
