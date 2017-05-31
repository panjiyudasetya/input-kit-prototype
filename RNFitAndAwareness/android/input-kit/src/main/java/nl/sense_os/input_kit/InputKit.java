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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.sense_os.input_kit.constant.ConnectionStatus;
import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.InputKitConnStatus;
import nl.sense_os.input_kit.listeners.InputKitConnectionListener;
import nl.sense_os.input_kit.listeners.ResultListener;
import nl.sense_os.input_kit.services.InputKitService;
import nl.sense_os.input_kit.services.apis.AwarenessApiHelper;
import nl.sense_os.input_kit.services.apis.InputKitWrapperApis;
import nl.sense_os.input_kit.services.apis.MonitoringGeofenceApiHelper;
import nl.sense_os.input_kit.services.apis.StepsCountApiHelper;
import nl.sense_os.input_kit.tasks.PopulateDailyStepsCountDataTask;
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
    private InputKitWrapperApis mInputKitWrapperApis;
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

        final AwarenessApiHelper api = mInputKitWrapperApis.getAwarenessApi();
        api.startActivityRecognition();

        Log.d(TAG, "subscribeActivityDetection: subscribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void unsubscribeActivityDetection() {
        validateActions();

        final AwarenessApiHelper api = mInputKitWrapperApis.getAwarenessApi();
        api.stopActivityRecognition();
        Log.d(TAG, "unsubscribeActivityDetection: unsubcribed");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void subscribeGeofencing(final @NonNull ResultListener<String> listener) {
        validateActions();

        final MonitoringGeofenceApiHelper api = mInputKitWrapperApis.getMonitoringGeofenceApi();
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

        final MonitoringGeofenceApiHelper api = mInputKitWrapperApis.getMonitoringGeofenceApi();
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

        final StepsCountApiHelper api = mInputKitWrapperApis.getStepsCountApi();
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

        final StepsCountApiHelper api = mInputKitWrapperApis.getStepsCountApi();
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

        final StepsCountApiHelper api = mInputKitWrapperApis.getStepsCountApi();
        api.subscribeStepsCountApi(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    new PopulateDailyStepsCountDataTask(api, endTime) {
                        @Override
                        protected void onPostExecute(List<Content> contents) {
                            listener.onResult(true, contents);
                        }
                    }.run();
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
        mInputKitWrapperApis = event.getApisHelper();

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

    private synchronized void notifyAll(boolean isAccessible, @NonNull String message) {
        Iterator<Map.Entry<String, InputKitConnectionListener>> iterator
                = mInputKitListeners.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, InputKitConnectionListener> item = iterator.next();
            if (isAccessible) item.getValue().onInputKitIsAccessible();
            else item.getValue().onInputKitIsNotAccessible(message);
        }
    }

    private void validateActions() {
        String errMessage = "Unable to perform this Action until Input Kit available to use.";
        if (mInputKitWrapperApis == null) throw new IllegalStateException(errMessage);
    }
}
