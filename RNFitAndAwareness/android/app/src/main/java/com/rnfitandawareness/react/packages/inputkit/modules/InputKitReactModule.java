package com.rnfitandawareness.react.packages.inputkit.modules;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.rnfitandawareness.R;

import nl.sense_os.inputkit.InputKit;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public abstract class InputKitReactModule extends ReactContextBaseJavaModule {
    private static final String TAG = "InputKitReactModule";
    protected ReactApplicationContext mReactContext;
    protected InputKit mInputKit;

    public InputKitReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mInputKit = InputKit.getInstance(mReactContext);
    }

    protected void showPermissionsMessageDialog(boolean isAllGranted) {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            new Throwable("Unable to request show a dialog while Application in foreground!").printStackTrace();
            return;
        }
        String message = activity.getString(isAllGranted ? R.string.all_permission_granted : R.string.permission_denied);
        new AlertDialog.Builder(activity)
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
