package com.rnfitandawareness.react.packages.inputkit.modules;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.google.android.gms.common.ConnectionResult;
import com.rnfitandawareness.helpers.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;

import nl.sense_os.inputkit.InputKit;
import nl.sense_os.inputkit.entity.StepContent;

import static com.rnfitandawareness.react.packages.inputkit.constants.RequestCode.REQ_OAUTH_CODE;
import static com.rnfitandawareness.react.packages.inputkit.constants.RequestCode.REQ_REQUIRED_PERMISSIONS;
import static nl.sense_os.inputkit.entity.PermissionsHelper.getAllPermissions;
import static nl.sense_os.inputkit.entity.PermissionsHelper.isAllPermissionsGranted;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class GoogleFitBridge extends InputKitReactModule implements ActivityEventListener {
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

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == REQ_OAUTH_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: Successfully connect to OAUTH");
            } else {
                // TODO : Do we need to call mInputKit.authorize(InputKit.Callback) ?
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void isHealthAvailable(final Promise promise) {
        mInputKit.checkAvailability(new InputKit.Callback() {
            @Override
            public void onAvailable() {
                promise.resolve("Google Fit available!");
            }

            @Override
            public void onNotAvailable(@NonNull String reason) {
                promise.reject(new Throwable(reason));
            }

            @Override
            public void onConnectionRefused(@NonNull ConnectionResult connectionResult) {
                try {
                    connectionResult.startResolutionForResult(getCurrentActivity(), REQ_OAUTH_CODE);
                } catch (Exception ex) {
                    promise.reject(new Throwable(ex.getMessage()));
                }
            }
        });
    }

    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void requestPermissions(ReadableArray permissions, Promise promise) {
        // Validate action on Activity
        Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject(new Throwable("Unable to request some permissions while Application in foreground!"));
            return;
        }

        // Requesting android permissions
        try {
            JSONArray rnArrayPermissions = JsonHelper.convertArrayToJson(permissions);
            String[] requiredPermissions = getAllPermissions(rnArrayPermissions);
            if (!isAllPermissionsGranted(activity, requiredPermissions)) {
                ActivityCompat.requestPermissions(activity, requiredPermissions, REQ_REQUIRED_PERMISSIONS);
            } else {
                //showPermissionsMessageDialog(true);
                promise.resolve("All permission has been granted.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            promise.reject(new Throwable(e.getMessage()));
        }
    }

    /**
     * Get total steps count of specific range
     * @param startTime epoch for the start date
     * @param endTime   epoch for the end date
     * @param promise containing number of total steps count
     */
    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void getStepCount(long startTime, long endTime, final Promise promise) {
        mInputKit.getStepCount(startTime, endTime, new InputKit.ResultCallback<Integer>() {
            @Override
            public void onNewData(Integer data) {
                Log.d(TAG, "getStepCount#onNewData: " + data);
                promise.resolve(data);
            }
        });
    }

    /**
     *  Returns Promise contains distribution of step count value through out a specific range.
     *
     *  @param startTime    epoch for the start date of the range where the distribution should be calculated from.
     *  @param endTime      epoch for the end date of the range where the distribution should be calculated from.
     *  @param interval     Interval
     *  @return Promise containing:
     *     value: total number of steps
     *     startDate: epoch for the start date
     *     endDate: epoch for the end date
     **/
    @ReactMethod
    @SuppressWarnings("unused")//Used by React Native application
    public void getStepCountDistribution(long startTime, long endTime, String interval, final Promise promise) {
        mInputKit.getStepCountDistribution(
                startTime,
                endTime,
                interval,
                new InputKit.ResultCallback<StepContent>() {
                    @Override
                    public void onNewData(StepContent data) {
                        Log.d(TAG, "getStepCountDistribution#onNewData: " + data.toJson());
                        promise.resolve(data.toJson());
                    }
                }
        );
    }
}
