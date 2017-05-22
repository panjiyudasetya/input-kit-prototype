package nl.sense_os.input_kit.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import nl.sense_os.input_kit.constant.ServiceType;
import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.DetectedStepsCountEvent;
import nl.sense_os.input_kit.eventbus.GAClientConnReceivedEvent;
import nl.sense_os.input_kit.tasks.PopulateStepsCountDataTask;

/**
 * Created by panjiyudasetya on 5/8/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class GoogleFitService extends BaseService {
    private static final String TAG = "FIT_SERVICE";
    private static final String KEY_ACTIVATED = String.format("%s_IS_ACTIVE", TAG);
    private static final Api[] REQUIRED_APIS = {Fitness.RECORDING_API, Fitness.HISTORY_API, Awareness.API};
    private static final Scope[] REQUIRED_SCOPES = {new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE)};
    private static int mServiceType;

    private GoogleApiClient mClient;
    private boolean mIsStepsCountApiSubscribed;

    public static Intent withContext(@NonNull Context context, int type) {
        mServiceType = type;
        return new Intent(context, GoogleFitService.class);
    }

    public static boolean isActive() {
        return Hawk.get(KEY_ACTIVATED, false);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.put(KEY_ACTIVATED, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Hawk.put(KEY_ACTIVATED, false);
    }

    @Override
    protected Scope[] initWithGoogleClientScopes() {
        return REQUIRED_SCOPES;
    }

    @Override
    protected Api[] initWithGoogleClientApis() {
        return REQUIRED_APIS;
    }

    @Override
    protected void initComponents() {
        mClient = getApiClient();
    }

    @Override
    protected void subscribe() {
        if (!mIsStepsCountApiSubscribed) subscribeStepsCountApi();
        else handleSubscribeServiceType();
    }

    /**
     * Record step data by requesting a subscription to background step data.
     */
    private void subscribeStepsCountApi() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi
                .subscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(com.google.android.gms.common.api.Status status) {
                        mIsStepsCountApiSubscribed = status.isSuccess();
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                EventBus.getDefault()
                                        .post(new GAClientConnReceivedEvent(
                                                GAClientConnReceivedEvent.Status.ALREADY_SUBSCRIBED,
                                                "Existing subscription for activity detected."
                                        ));
                            } else {
                                EventBus.getDefault()
                                        .post(new GAClientConnReceivedEvent(
                                                GAClientConnReceivedEvent.Status.SUCCESSFULLY_SUBSCRIBED,
                                                "Successfully subscribed!"
                                        ));
                            }

                            handleSubscribeServiceType();
                        } else {
                            EventBus.getDefault()
                                    .post(new GAClientConnReceivedEvent(
                                            GAClientConnReceivedEvent.Status.FAILURE_TO_SUBSCRIBE,
                                            "There was a problem subscribing."
                                    ));
                        }
                    }
                });
    }

    private void unsubscribeStepsCountApi() {
        Fitness.RecordingApi
                .unsubscribe(mClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // TODO: Do we need to handle this ?
                    }
                });
    }

    private void handleSubscribeServiceType() {
        switch (mServiceType) {
            case ServiceType.Fitness.ALL:
            case ServiceType.Fitness.STEPS_COUNT:
                subscribeDataFromFitnessApi();
                break;
            case ServiceType.Fitness.STOP_ALL:
            case ServiceType.Fitness.STOP_STEPS_COUNT:
                unsubscribeDataFromFitnessApi();
                break;
        }
    }

    private void subscribeDataFromFitnessApi() {
        switch (mServiceType) {
            case ServiceType.Fitness.ALL:
                //TODO: On this point probably we do more an action to subscribing some data on Fitness API
                consumeStepsCountHistory();
                break;
            case ServiceType.Fitness.STEPS_COUNT:
                consumeStepsCountHistory();
                break;
        }
    }

    private void unsubscribeDataFromFitnessApi() {
        switch (mServiceType) {
            case ServiceType.Fitness.STOP_ALL:
                //TODO: On this point probably we do more an action to stop subscribing some data from Fitness API
                unsubscribeStepsCountApi();
                break;
            case ServiceType.Fitness.STOP_STEPS_COUNT:
                unsubscribeStepsCountApi();
                break;
        }
    }

    private void consumeStepsCountHistory() {
        new PopulateStepsCountDataTask(mClient) {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                if (contents != null) postContents(contents);
            }
        }.run();
    }

    private void postContents(@NonNull List<Content> contents) {
        EventBus.getDefault()
                .post(new DetectedStepsCountEvent(contents));
    }
}