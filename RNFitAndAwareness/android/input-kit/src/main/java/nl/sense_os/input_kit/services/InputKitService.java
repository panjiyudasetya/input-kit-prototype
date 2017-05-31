package nl.sense_os.input_kit.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import nl.sense_os.input_kit.constant.ConnectionStatus;
import nl.sense_os.input_kit.eventbus.InputKitConnStatus;
import nl.sense_os.input_kit.helpers.AlarmHelper;
import nl.sense_os.input_kit.services.apis.InputKitWrapperApis;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public class InputKitService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "INPUT_KIT_API_CLIENT";
    private static final Api[] REQUIRED_APIS = {Awareness.API, ActivityRecognition.API, LocationServices.API,
            Fitness.RECORDING_API, Fitness.HISTORY_API};
    private static final Scope[] REQUIRED_SCOPES = {new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE)};
    private boolean mIsConnected;
    private GoogleApiClient mClient;

    public static Intent withContext(@NonNull Context context) {
        return new Intent(context, InputKitService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isPlayServiceConnected()) mClient.connect();
        else handlePlayServiceConnection(ConnectionStatus.CONNECTED, "Connected!", null);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setPlayServiceConnection(false);
        releaseGoogleApiClient();

        new AlarmHelper(this).setNextSelfSchedulingAlarm();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setPlayServiceConnection(true);
        String message = "Connected!";
        Log.i(TAG, "Connected!");
        handlePlayServiceConnection(ConnectionStatus.CONNECTED, message, null);
    }

    @Override
    public void onConnectionSuspended(int clientCode) {
        setPlayServiceConnection(false);
        String message = "Connection lost. Cause : Unknown.";
        // If your connection to the sensor gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (clientCode == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            message = "Connection lost.  Cause: Network Lost.";
            Log.w(TAG, message);
        } else if (clientCode == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            message = "Connection lost.  Reason: Service Disconnected.";
            Log.w(TAG, message);
        }

        handlePlayServiceConnection(ConnectionStatus.CONN_SUSPENDED, message, null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        setPlayServiceConnection(false);

        String message = "Exception while connecting to Google Play services: "
                + connectionResult.getErrorMessage();

        Log.w(TAG, message);

        ConnectionStatus status = ConnectionStatus.CONN_FAILED;
        if (connectionResult.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED)
            status = ConnectionStatus.SIGN_IN_REQUIRED;

        handlePlayServiceConnection(status, message, connectionResult);
    }

    /**
     * Build a {@link GoogleApiClient} to authenticate the user and allow the application
     * to connect to the Fitness APIs. The included scopes should match the scopes needed
     * by your app (see the documentation for details).
     * Use the {@link GoogleApiClient.OnConnectionFailedListener}
     * to resolve authentication failures (for example, the user has not signed in
     * before, or has multiple accounts and must specify which account to use).
     */
    private void buildGoogleApiClient() {
        // Create the Google API Client
        if (mClient == null) {
            mClient = buildGoogleApiClientWithApisAndScopes(REQUIRED_APIS, REQUIRED_SCOPES);
            mClient.connect();
        }
    }

    private void releaseGoogleApiClient() {
        mClient.disconnect();
        mClient = null;
    }

    /**
     * Helper function to create {@link GoogleApiClient} based on specific Apis and Scopes
     *
     * @param apis      Array of Api Client {@link Api}
     * @param scopes    Array of {@link Scope} Api Client
     * @return {@link GoogleApiClient}
     * @throws IllegalArgumentException
     */
    private GoogleApiClient buildGoogleApiClientWithApisAndScopes(@NonNull Api[] apis,
                                                                  @Nullable Scope[] scopes)
            throws IllegalArgumentException {
        if (apis.length == 0) {
            String message = "Unable to continue this action, Google Api Client should contain at least one Api";
            throw new IllegalArgumentException(message);
        }
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        for (Api api : apis) builder.addApi(api);
        if (scopes != null && scopes.length > 0) {
            for (Scope scope : scopes) builder.addScope(scope);
        }
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        return builder.build();
    }

    private void handlePlayServiceConnection(@NonNull ConnectionStatus status,
                                             @NonNull String message,
                                             @Nullable ConnectionResult connectionResult) {
        EventBus.getDefault()
                .post(new InputKitConnStatus(
                        status,
                        message,
                        connectionResult,
                        new InputKitWrapperApis(this, mClient))
                );
    }

    private void setPlayServiceConnection(boolean isConnected) {
        mIsConnected = isConnected;
    }

    private boolean isPlayServiceConnected() {
        return mIsConnected;
    }
}
