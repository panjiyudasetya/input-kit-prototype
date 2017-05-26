package nl.sense_os.input_kit.services;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.eventbus.DetectedStepsCountEvent;
import nl.sense_os.input_kit.eventbus.GAClientConnReceivedEvent;
import nl.sense_os.input_kit.services.apis.StepsCountApiHelper;
import nl.sense_os.input_kit.tasks.PopulateStepsCountDataTask;

import static nl.sense_os.input_kit.constant.InputKitType.START_ALL;
import static nl.sense_os.input_kit.constant.InputKitType.STOP_ALL;
import static nl.sense_os.input_kit.constant.InputKitType.StepsCount;

/**
 * Created by panjiyudasetya on 5/26/17.
 */

public class GoogleFitServiceController {
    private static final String TAG = "GFSC";
    private static final String KEY_ACTIVATED = String.format("%s_IS_ACTIVE", TAG);
    private GoogleApiClient mClient;
    private StepsCountApiHelper mStepsCountHelper;
    private boolean mIsStepsCountApiSubscribed;
    private int mActionType;

    public GoogleFitServiceController(@NonNull GoogleApiClient client) {
        this.mClient = client;
        this.mStepsCountHelper = new StepsCountApiHelper(client);
    }

    public void handleSubscribeEvent(int actionType) {
        mActionType = actionType;
        if (!mIsStepsCountApiSubscribed) subscribeStepsCountApi();
        else handleSubscribeServiceType(mActionType);
    }

    public static boolean isServiceActive() {
        return Hawk.get(KEY_ACTIVATED, false);
    }

    public void setIsServiceActive(boolean isServiceActive) {
        Hawk.put(KEY_ACTIVATED, isServiceActive);
    }

    /**
     * Record step data by requesting a subscription to background step data.
     */
    private void subscribeStepsCountApi() {
        mStepsCountHelper.subscribeStepsCountApi(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    mIsStepsCountApiSubscribed = true;
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

                    handleSubscribeServiceType(StepsCount.GET_STEPS_COUNT);
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

    private void handleSubscribeServiceType(int type) {
        if (isSubscribeActions()) {
            startFitApi();
            return;
        }

        if (isUnsubscribeActions()) {
            stopFitApi();
            return;
        }

        if (type == StepsCount.GET_STEPS_COUNT) {
            consumeStepsCountHistory();
            return;
        }
    }

    private boolean isSubscribeActions() {
        return mActionType == START_ALL || mActionType == StepsCount.SUBSCRIBE;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private boolean isUnsubscribeActions() {
        return mActionType == STOP_ALL || mActionType == StepsCount.UNSUBSCRIBE;
    }

    private void startFitApi() {
        if (mActionType == StepsCount.SUBSCRIBE) subscribeStepsCountApi();
        else if (mActionType == START_ALL) {
            subscribeStepsCountApi();
            //TODO : Probably we will start more api here
        }
    }

    private void stopFitApi() {
        if (mActionType == StepsCount.UNSUBSCRIBE) mStepsCountHelper.unsubscribeStepsCountApi();
        else if (mActionType == STOP_ALL) {
            mStepsCountHelper.unsubscribeStepsCountApi();
            //TODO : Probably we will stop more api here
        }
    }

    private void consumeStepsCountHistory() {
        new PopulateStepsCountDataTask(mClient) {
            @Override
            protected void onPostExecute(List<Content> contents) {
                super.onPostExecute(contents);
                if (contents != null) {
                    EventBus.getDefault()
                            .post(new DetectedStepsCountEvent(contents));
                }
            }
        }.run();
    }
}
