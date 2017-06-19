package com.rnfitandawareness.react.packages.inputkit.modules;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.google.gson.Gson;

import nl.sense_os.inputkit.InputKit;

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
}
