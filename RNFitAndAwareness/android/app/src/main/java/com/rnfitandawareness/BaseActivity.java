package com.rnfitandawareness;

import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.google.android.gms.common.ConnectionResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.sense_os.input_kit.eventbus.GAClientConnReceivedEvent;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

/**
 * Created by panjiyudasetya on 5/22/17.
 */

public abstract class BaseActivity extends ReactActivity {
    private static final String TAG = "BASE_ACTIVITY";
    private static final int PERMISSIONS_REQ_CODE = 103;
    private static final String[] PERMISSIONS = {
            INTERNET,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
    };

    public void requestAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isAllPermissionGranted(PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQ_CODE);
            } else showPermissionsMessageDialog(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQ_CODE:
                if (!isAllPermissionGranted(PERMISSIONS)) showPermissionsMessageDialog(false);
                break;
            default: return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    @SuppressWarnings("unused")//This function being used by EventBus
    public void onInputKitConnReceivedEvent(GAClientConnReceivedEvent event) {
        // receiving Awareness connection event
        if (event == null) return;

        GAClientConnReceivedEvent.Status status = event.getStatus();
        String message = event.getMessage();
        if (status.equals(GAClientConnReceivedEvent.Status.CONNECTED)) {
            Log.i(TAG, "Connected!!!");
        } else if (status.equals(GAClientConnReceivedEvent.Status.SIGN_IN_REQUIRED)) {
            ConnectionResult connectionResult = event.getConnResult();
            if (connectionResult != null) resolvePlayServiceCredentialProblem(connectionResult);
        } else {
            Log.w(TAG, message);
        }
    }

    private void resolvePlayServiceCredentialProblem(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, ConnectionResult.SIGN_IN_REQUIRED);
        } catch (IntentSender.SendIntentException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    /**
     * Helper function to detect all permission has been granted by user.
     *
     * @return True if permissions granted, False otherwise.
     */
    protected boolean isAllPermissionGranted(String[] permissions) {
        if (permissions == null || permissions.length == 0) return true;

        try {
            PackageInfo info = getPackageManager()
                    .getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);

            List<String> grantedPermissions = new ArrayList<>();
            if (info.requestedPermissions != null) {
                for (int i = 0; i < info.requestedPermissions.length; i++) {
                    if ((info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        grantedPermissions.add(info.requestedPermissions[i]);
                    }
                }
            }

            return grantedPermissions.containsAll(Arrays.asList(PERMISSIONS));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAllPermissionsGranted() {
        boolean isGranted = isAllPermissionGranted(PERMISSIONS);
        if (!isGranted) showPermissionsMessageDialog(false);
        else requestAllPermissions();
        return isGranted;
    }

    private void showPermissionsMessageDialog(boolean isAllGranted) {
        String message = getString(isAllGranted ? R.string.all_permission_granted : R.string.permission_denied);
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();

    }
}
