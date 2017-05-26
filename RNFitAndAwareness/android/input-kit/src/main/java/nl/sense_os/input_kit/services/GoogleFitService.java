package nl.sense_os.input_kit.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

/**
 * Created by panjiyudasetya on 5/8/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class GoogleFitService extends BaseService {
    private static final String TAG = "FIT_SERVICE";
    private static final Api[] REQUIRED_APIS = {Fitness.RECORDING_API, Fitness.HISTORY_API, Awareness.API};
    private static final Scope[] REQUIRED_SCOPES = {new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE)};
    private static int mServiceType;
    private GoogleFitServiceController mServiceController;

    public static Intent withContext(@NonNull Context context, int type) {
        mServiceType = type;
        return new Intent(context, GoogleFitService.class);
    }

    public static boolean isActive() {
        return GoogleFitServiceController.isServiceActive();
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
        return REQUIRED_SCOPES;
    }

    @Override
    protected Api[] initWithGoogleClientApis() {
        return REQUIRED_APIS;
    }

    @Override
    protected void initComponents() {
        mServiceController = new GoogleFitServiceController(getApiClient());
    }

    @Override
    protected void subscribe() {
        mServiceController.handleSubscribeEvent(mServiceType);
    }
}