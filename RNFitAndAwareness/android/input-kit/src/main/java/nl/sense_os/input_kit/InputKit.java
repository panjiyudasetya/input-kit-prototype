package nl.sense_os.input_kit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.sense_os.input_kit.constant.ConnectionStatus;
import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.entities.StepsCountResponse;
import nl.sense_os.input_kit.eventbus.InputKitConnStatus;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;
import nl.sense_os.input_kit.listeners.ResultListener;
import nl.sense_os.input_kit.services.InputKitService;
import nl.sense_os.input_kit.services.apis.AwarenessApiHelper;
import nl.sense_os.input_kit.services.apis.InputKitApisHelper;
import nl.sense_os.input_kit.services.apis.MonitoringGeofenceApiHelper;
import nl.sense_os.input_kit.services.apis.StepsCountApiHelper;
import nl.sense_os.input_kit.tasks.PopulateGeofenceDataTask;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class InputKit {
    private static final String TAG = "INPUT_KIT_API";
    private final Map<String, InputKitConnectionListener> mInputKitListeners = new HashMap<>();
    private static InputKit mInputKitInstance;
    private Context mContext;
    private EventBus mEventBus;
    private InputKitApisHelper mInputKitApisHelper;
    private ConnectionStatus mConnStatus;

    private InputKit(@NonNull Context context) {
        mContext = context;
        mEventBus = EventBus.getDefault();
        if (!mEventBus.isRegistered(this)) mEventBus.register(InputKit.this);
    }

    public static synchronized InputKit getInstance(@NonNull Context context) {
        if (mInputKitInstance == null) {
            mInputKitInstance = new InputKit(context);
        }
        return mInputKitInstance;
    }

    public void connect(@NonNull String eventName,
                        @NonNull InputKitConnectionListener listener) {
        addInputKitConnectionListener(eventName, listener);
        mContext.startService(
                InputKitService.withContext(mContext)
        );
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeActivityDetection() {
        validateActions();

        final AwarenessApiHelper api = mInputKitApisHelper.getAwarenessApi();
        api.startActivityRecognition();

        Log.d(TAG, "subscribeActivityDetection: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeActivityDetection() {
        validateActions();

        final AwarenessApiHelper api = mInputKitApisHelper.getAwarenessApi();
        api.stopActivityRecognition();
        Log.d(TAG, "unsubscribeActivityDetection: unsubcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeGeofencing(final @NonNull ResultListener<String> listener) {
        validateActions();

        final MonitoringGeofenceApiHelper api = mInputKitApisHelper.getMonitoringGeofenceApi();
        api.stopSensingSenseHQGeofences(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) listener.onResult(true, "subscribeGeofencing: subscribed.");
                else {
                    listener.onResult(false,
                            "subscribeGeofencing: fail to subscribed. Reason : "
                                    + status.getStatusMessage());
                }
            }
        });
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeGeofencing(final @NonNull ResultListener<String> listener) {
        validateActions();

        final MonitoringGeofenceApiHelper api = mInputKitApisHelper.getMonitoringGeofenceApi();
        api.stopSensingSenseHQGeofences(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) listener.onResult(true, "unsubscribeGeofencing: unsubribed.");
                else {
                    listener.onResult(false,
                            "unsubscribeGeofencing: fail to unsubribed. Reason : "
                                    + status.getStatusMessage());
                }
            }
        });
    }


    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeDailyStepsCount(final @NonNull ResultListener<String> listener) {
        validateActions();

        final StepsCountApiHelper api = mInputKitApisHelper.getStepsCountApi();
        api.subscribeStepsCountApi(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) listener.onResult(true, "subscribeDailyStepsCount: subscribed.");
                else {
                    listener.onResult(false,
                            "subscribeDailyStepsCount: fail to subscribed. Reason : "
                                    + status.getStatusMessage());
                }
            }
        });
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeDailyStepsCount(final @NonNull ResultListener<String> listener) {
        validateActions();

        final StepsCountApiHelper api = mInputKitApisHelper.getStepsCountApi();
        api.unsubscribeStepsCountApi(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) listener.onResult(true, "unsubscribeDailyStepsCount: unsubscribed.");
                else {
                    listener.onResult(false,
                            "unsubscribeDailyStepsCount: fail to unsubscribed. Reason : "
                                    + status.getStatusMessage());
                }
            }
        });
    }

    public void getDailyStepsCountHistory(final long endTime,
                                          final @NonNull ResultListener<List<Content>> listener) {
        validateActions();
        final StepsCountApiHelper api = mInputKitApisHelper.getStepsCountApi();
        api.subscribeStepsCountApi(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    StepsCountResponse response = api.getDailyStepCount(endTime, true);
                    listener.onResult(true, response.getContents());
                } else listener.onResult(false, Collections.<Content>emptyList());
            }
        });
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void getGeofencingHistory(final @NonNull ResultListener<List<Content>> listener) {
        validateActions();
        new PopulateGeofenceDataTask() {
            @Override
            protected void onPostExecute(List<Content> contents) {
                listener.onResult(true, contents);
            }
        }.run();
    }

    public void release() {
        mEventBus.unregister(this);
    }

    @Subscribe
    @SuppressWarnings("unused")//This function being used by EventBus
    public void onInputKitConnectionStatusReceived(InputKitConnStatus event) {
        // receiving Awareness connection event
        if (event == null) return;

        String message = event.getMessage();
        mConnStatus = event.getStatus();
        mInputKitApisHelper = event.getApisHelper();

        if (mConnStatus.equals(ConnectionStatus.CONNECTED)) notifyAll(true, message);
        else notifyAll(false, message);
    }

    public void removeInputKitConnectionListener(@NonNull String eventName) {
        mInputKitListeners.remove(eventName);
    }

    private void addInputKitConnectionListener(@NonNull String eventName,
                                               @NonNull InputKitConnectionListener listener) {
        mInputKitListeners.put(eventName, listener);
    }

    private void notifyAll(boolean isAccessible, @NonNull String message) {
        if (isAccessible) {
            for (Map.Entry<String, InputKitConnectionListener> listener : mInputKitListeners.entrySet()) {
                listener.getValue().onInputKitIsAccessible();
            }
        } else {
            for (Map.Entry<String, InputKitConnectionListener> listener : mInputKitListeners.entrySet()) {
                listener.getValue().onInputKitIsNotAccessible(message);
            }
        }
    }

    private void validateActions() {
        String errMessage = "Unable to perform this Action until Input Kit available to use.";
        if (mInputKitApisHelper == null) throw new IllegalStateException(errMessage);
    }
}
