package com.rnfitandawareness.react.packages.inputkit.modules;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.GeofenceEvent;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;
import nl.sense_os.input_kit.listeners.ResultListener;

import static com.rnfitandawareness.react.packages.inputkit.constants.InputKitEmitterEvents.GEOFENCING_EVENT_LISTENER;
import static nl.sense_os.input_kit.constant.InputKitEventName.COLLECT_GEOFENCE_EVENT;
import static nl.sense_os.input_kit.constant.InputKitEventName.SUBSCRIBE_GEOFENCE_EVENT;
import static nl.sense_os.input_kit.constant.InputKitEventName.UNSUBSCRIBE_GEOFENCE_EVENT;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class AwarenessBridge extends InputKitReactModule {
    private static final String AWARENESS_MODULE_NAME = "AwarenessBridge";
    private static final String TAG = AWARENESS_MODULE_NAME;
    private static final String[] POSSIBILITY_REGISTERED_EVENTS = {
            SUBSCRIBE_GEOFENCE_EVENT,
            UNSUBSCRIBE_GEOFENCE_EVENT,
            COLLECT_GEOFENCE_EVENT
    };

    @SuppressWarnings("unused") // Used by React Native
    public AwarenessBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return AWARENESS_MODULE_NAME;
    }

    @Override
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void requestPermissions(Promise promise) {

    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void startGeoFencing(final Promise promise) {
        if (!checkPermissions(false)) return;

        connectToInputKit(SUBSCRIBE_GEOFENCE_EVENT, new InputKitConnectionListener() {
            @Override
            public void onInputKitIsAccessible() {
                mInputKit.subscribeGeofencing(new ResultListener<String>() {
                    @Override
                    public void onResult(boolean isSuccess, @NonNull String data) {
                        System.out.println("Subscribing geofence : " + isSuccess + ", " + data);
                        if (isSuccess) promise.resolve(data);
                        else promise.reject(new Throwable(data));
                    }
                });
            }

            @Override
            public void onInputKitIsNotAccessible(String reason) {
                promise.reject(new Throwable(reason));
            }
        });
    }

    @ReactMethod
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//Used by React Native application
    public void stopGeoFencing(final Promise promise) {
        if (!checkPermissions(false)) return;

        connectToInputKit(UNSUBSCRIBE_GEOFENCE_EVENT, new InputKitConnectionListener() {
            @Override
            public void onInputKitIsAccessible() {
                mInputKit.unsubscribeGeofencing(new ResultListener<String>() {
                    @Override
                    public void onResult(boolean isSuccess, @NonNull String data) {
                        System.out.println("Unsubscribing geofence : " + isSuccess + ", " + data);
                        if (isSuccess) promise.resolve(data);
                        else promise.reject(new Throwable(data));
                    }
                });
            }

            @Override
            public void onInputKitIsNotAccessible(String reason) {
                promise.reject(new Throwable(reason));
            }
        });
    }

    @ReactMethod
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//Used by React Native application
    public void getGeoFencingHistory(final Promise promise) {
        if (!checkPermissions(false)) return;

        connectToInputKit(COLLECT_GEOFENCE_EVENT, new InputKitConnectionListener() {
            @Override
            public void onInputKitIsAccessible() {
                mInputKit.getGeofencingHistory(new ResultListener<List<Content>>() {
                    @Override
                    public void onResult(boolean isSuccess, @NonNull List<Content> data) {
                        System.out.println("Receiving geofence data : " + GSON.toJson(data));
                        if (isSuccess) promise.resolve(GSON.toJson(data));
                        else promise.reject(new Throwable("Unable to get geofencing history"));
                    }
                });
            }

            @Override
            public void onInputKitIsNotAccessible(String reason) {
                promise.reject(new Throwable(reason));
            }
        });
    }

    @Subscribe
    @SuppressWarnings({"unused", "SpellCheckingInspection"})//Used by React Native application
    public void onGefenceEventReceived(final GeofenceEvent event) {
        new AsyncTask<Void, Integer, Exception>() {
            private String contentsJson = null;
            @Override
            protected Exception doInBackground(Void... voids) {
                contentsJson = GSON.toJson(event.getContents());
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
