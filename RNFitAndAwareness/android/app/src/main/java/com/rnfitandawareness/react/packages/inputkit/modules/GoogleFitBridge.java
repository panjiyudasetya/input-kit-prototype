package com.rnfitandawareness.react.packages.inputkit.modules;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.rnfitandawareness.react.packages.inputkit.constants.Measurement;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;
import nl.sense_os.input_kit.listeners.ResultListener;

import static nl.sense_os.input_kit.constant.InputKitEventName.COLLECT_STEPS_COUNT_EVENT;
import static nl.sense_os.input_kit.constant.InputKitEventName.PLAY_SERVICE_CONNECTION_EVENT;
import static nl.sense_os.input_kit.constant.InputKitEventName.SUBSCRIBE_STEPS_COUNT_EVENT;
import static nl.sense_os.input_kit.constant.InputKitEventName.UNSUBSCRIBE_STEPS_COUNT_EVENT;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class GoogleFitBridge extends InputKitReactModule {
    private static final String GOOGLE_FIT_MODULE_NAME = "GoogleFitBridge";
    private static final String TAG = GOOGLE_FIT_MODULE_NAME;

    @SuppressWarnings("unused") // Used by React Native
    public GoogleFitBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return GOOGLE_FIT_MODULE_NAME;
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void isGoogleApiClientConnected(final Promise promise) {
        connectToInputKit(
                PLAY_SERVICE_CONNECTION_EVENT,
                new InputKitConnectionListener() {
                    @Override
                    public void onInputKitIsAccessible() {
                        promise.resolve("Successfully connect to Input Kit");
                        releaseInputKitConnectionListener(PLAY_SERVICE_CONNECTION_EVENT);
                    }

                    @Override
                    public void onInputKitIsNotAccessible(String reason) {
                        promise.reject(new Throwable(reason));
                        releaseInputKitConnectionListener(PLAY_SERVICE_CONNECTION_EVENT);
                    }
                }
        );
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void requestPermissions(ReadableArray permissions, Promise promise) {
        if (checkPermissions(true)) promise.resolve("All permission has been granted.");
        else promise.reject(new Throwable("All permission should be granted."));
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    /**
     * Set promise resolve into stringify of json object when successful
     * {
     *     "value" : 2,
     *     "startDate" : 1403654400000L,
     *     "endDate" : 1403654400000L
     * }
     */
    public void getStepCount(final float date, final Promise promise) {
        if (!validateAction(promise)) return;

        connectToInputKit(COLLECT_STEPS_COUNT_EVENT, new InputKitConnectionListener() {
            @Override
            public void onInputKitIsAccessible() {
                mInputKit.getDailyStepsCountHistory((long) date, new ResultListener<List<Content>>() {
                    @Override
                    public void onResult(boolean isSuccess, @NonNull List<Content> data) {
                        if (isSuccess) promise.resolve(GSON.toJson(data));
                        else promise.reject(new Throwable("Unable to get daily steps counts"));
                        releaseInputKitConnectionListener(COLLECT_STEPS_COUNT_EVENT);
                    }
                });
            }

            @Override
            public void onInputKitIsNotAccessible(String reason) {
                promise.reject(new Throwable(reason));
                releaseInputKitConnectionListener(COLLECT_STEPS_COUNT_EVENT);
            }
        });
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void startMonitoring(final String type, final Promise promise) {
        if (!validateAction(promise)) return;

        if (type.equals(Measurement.STEPS_COUNT)) {
            connectToInputKit(SUBSCRIBE_STEPS_COUNT_EVENT, new InputKitConnectionListener() {
                @Override
                public void onInputKitIsAccessible() {
                    mInputKit.subscribeDailyStepsCount(new ResultListener<String>() {
                        @Override
                        public void onResult(boolean isSuccess, @NonNull String data) {
                            if (isSuccess) promise.resolve(data);
                            else promise.reject(new Throwable(data));
                        }
                    });
                    releaseInputKitConnectionListener(SUBSCRIBE_STEPS_COUNT_EVENT);
                }

                @Override
                public void onInputKitIsNotAccessible(String reason) {
                    promise.reject(new Throwable(reason));
                    releaseInputKitConnectionListener(SUBSCRIBE_STEPS_COUNT_EVENT);
                }
            });
        } else promise.reject(new Throwable("Unknown monitoring type : " + type));
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void stopMonitoring(final String type, final Promise promise) {
        if (!validateAction(promise)) return;

        if (type.equals(Measurement.STEPS_COUNT)) {
            connectToInputKit(UNSUBSCRIBE_STEPS_COUNT_EVENT, new InputKitConnectionListener() {
                @Override
                public void onInputKitIsAccessible() {
                    mInputKit.unsubscribeDailyStepsCount(new ResultListener<String>() {
                        @Override
                        public void onResult(boolean isSuccess, @NonNull String data) {
                            if (isSuccess) promise.resolve(data);
                            else promise.reject(new Throwable(data));
                            releaseInputKitConnectionListener(UNSUBSCRIBE_STEPS_COUNT_EVENT);
                        }
                    });
                }

                @Override
                public void onInputKitIsNotAccessible(String reason) {
                    promise.reject(new Throwable(reason));
                    releaseInputKitConnectionListener(UNSUBSCRIBE_STEPS_COUNT_EVENT);
                }
            });
        } else promise.reject(new Throwable("Unknown monitoring type : " + type));
    }

    private boolean validateAction(Promise promise) {
        if (!checkPermissions(false)) {
            promise.reject(new Throwable("Unable to perform this action due to some permission issue"));
            return false;
        }
        return true;
    }
}
