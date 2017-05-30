package com.rnfitandawareness.react.packages.inputkit.modules;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.rnfitandawareness.helpers.JsonHelper;

import org.greenrobot.eventbus.Subscribe;

import nl.sense_os.input_kit.listeners.ResultListener;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;

import static nl.sense_os.input_kit.constant.InputKitEventName.SUBSCRIBE_GEOFENCE_EVENT;
import static nl.sense_os.input_kit.constant.InputKitEventName.UNSUBSCRIBE_GEOFENCE_EVENT;
import static com.rnfitandawareness.react.packages.inputkit.constants.InputKitEmitterEvents.GEOFENCING_EVENT_LISTENER;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class AwarenessBridge extends InputKitReactModule {
    private static final String AWARENESS_MODULE_NAME = "AwarenessBridge";
    private static final String TAG = AWARENESS_MODULE_NAME;

    @SuppressWarnings("unused") // Used by React Native
    public AwarenessBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return AWARENESS_MODULE_NAME;
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void requestPermissions(Promise promise) {

    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void startGeoFencing(final Callback callback) {
        connectToInputKit(SUBSCRIBE_GEOFENCE_EVENT, new InputKitConnectionListener() {
            @Override
            public void onInputKitIsAccessible() {
                mInputKit.subscribeGeofencing(new ResultListener<String>() {
                    @Override
                    public void onResult(boolean isSuccess, @NonNull String data) {
                        if (isSuccess) callback.invoke(data);
                        releaseInputKitConnectionListener(SUBSCRIBE_GEOFENCE_EVENT);
                    }
                });
            }

            @Override
            public void onInputKitIsNotAccessible(String reason) {
                releaseInputKitConnectionListener(SUBSCRIBE_GEOFENCE_EVENT);
            }
        });
    }

    @ReactMethod
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//Used by React Native application
    public void stopGeoFencing(final Callback callback) {
        connectToInputKit(UNSUBSCRIBE_GEOFENCE_EVENT, new InputKitConnectionListener() {
            @Override
            public void onInputKitIsAccessible() {
                mInputKit.subscribeGeofencing(new ResultListener<String>() {
                    @Override
                    public void onResult(boolean isSuccess, @NonNull String data) {
                        if (isSuccess) callback.invoke(data);
                        releaseInputKitConnectionListener(UNSUBSCRIBE_GEOFENCE_EVENT);
                    }
                });
            }

            @Override
            public void onInputKitIsNotAccessible(String reason) {
                releaseInputKitConnectionListener(UNSUBSCRIBE_GEOFENCE_EVENT);
            }
        });
    }

    @Subscribe
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//Used by React Native application
    public void onGefenceEventReceived(final GeofenceEvent event) {
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
                        .emit(GEOFENCING_EVENT_LISTENER, contentsJson);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
