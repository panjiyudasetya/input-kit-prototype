package nl.sense_os.input_kit.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

/**
 * Created by panjiyudasetya on 5/8/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class AwarenessService extends BaseService implements LocationListener {
    private static final String TAG = "AWARENESS_SERVICE";
    private static final Api[] REQUIRED_APIS = {Awareness.API, ActivityRecognition.API, LocationServices.API};
    private static int mActionType;
    private AwarenessServiceController mServiceController;

    public static Intent withContext(@NonNull Context context, int type) {
        mActionType = type;
        return new Intent(context, AwarenessService.class);
    }

    public static boolean isActive() {
        return AwarenessServiceController.isServiceActive();
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceController.setIsServiceActive(true);
    }

    @Override
    public void onDestroy() {
        mServiceController.setIsServiceActive(false);
        super.onDestroy();
    }

    @Override
    protected Scope[] initWithGoogleClientScopes() {
        return null;
    }

    @Override
    protected Api[] initWithGoogleClientApis() {
        return REQUIRED_APIS;
    }

    @Override
    protected void initComponents() {
        mServiceController = new AwarenessServiceController(this, getApiClient(), this);
    }

    @Override
    protected void subscribe() {
        mServiceController.handleSubscribeEvent(mActionType);
    }

    @Override
    public void onLocationChanged(Location location) {
        mServiceController.onNewLocationDetected(location);
    }
}
