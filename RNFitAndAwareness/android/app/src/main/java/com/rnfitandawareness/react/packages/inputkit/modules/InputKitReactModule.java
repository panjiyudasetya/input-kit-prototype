package com.rnfitandawareness.react.packages.inputkit.modules;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import org.greenrobot.eventbus.EventBus;

import nl.sense_os.input_kit.InputKit;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public abstract class InputKitReactModule extends ReactContextBaseJavaModule {
    protected ReactApplicationContext mReactContext;
    protected InputKit mInputKit;
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

    protected void connectToInputKit(@NonNull String eventName,
                                     @NonNull InputKitConnectionListener listener) {
        mInputKit.connect(eventName, listener);
    }

    protected void releaseInputKitConnectionListener(@NonNull String eventName) {
        mInputKit.removeInputKitConnectionListener(eventName);
    }
}
