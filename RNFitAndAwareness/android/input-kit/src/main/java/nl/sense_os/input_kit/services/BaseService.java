package nl.sense_os.input_kit.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.greenrobot.eventbus.EventBus;

import nl.sense_os.input_kit.eventbus.GAClientConnReceivedEvent;

import static nl.sense_os.input_kit.eventbus.GAClientConnReceivedEvent.Status;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public abstract class BaseService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Tag must be unique because it will be used as an ID to track down lifecycle of child service.
     * @return Tag string.
     */
    protected abstract String tag();

    /**
     * This action required to add collection of {@link Api}s into Google Api Client.
     */
    protected abstract Api[] initWithGoogleClientApis();

    /**
     * This action required to add collection of {@link Scope}s into Google Api Client.
     */
    protected abstract Scope[] initWithGoogleClientScopes();

    /**
     * This action required to initialise some component which required on {@see #subscribe} event.
     */
    protected abstract void initComponents();

    /**
     * This action will be triggered whenever {@see #onConnected(Bundle)} event being called.
     * Other than that, whenever {@see #onStartCommand(Intent, int, int)} function being called,
     * this action also will be triggered if only {@see #mIsConnected} already true.
     */
    protected abstract void subscribe();

    private GoogleApiClient mClient;
    private boolean mIsConnected;

    public GoogleApiClient getApiClient() {
        return mClient;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsConnected) mClient.connect();
        else subscribe();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient(initWithGoogleClientApis(), initWithGoogleClientScopes());
        initComponents();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setPlayServiceConnection(false);
        releaseGoogleApiClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setPlayServiceConnection(true);
        Log.i(tag(), "Connected!");
        EventBus.getDefault()
                .post(new GAClientConnReceivedEvent(
                        Status.CONNECTED,
                        "Connected!")
                );
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int clientCode) {
        setPlayServiceConnection(false);
        String message = null;
        // If your connection to the sensor gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (clientCode == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            message = "Connection lost.  Cause: Network Lost.";
            Log.w(tag(), message);
        } else if (clientCode == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            message = "Connection lost.  Reason: Service Disconnected";
            Log.w(tag(), message);
        }

        if (!TextUtils.isEmpty(message)) {
            EventBus.getDefault()
                    .post(new GAClientConnReceivedEvent(
                            GAClientConnReceivedEvent.Status.CONN_SUSPENDED,
                            message)
                    );
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        setPlayServiceConnection(false);
        Log.w(tag(), "Google Play services connection failed. Cause: "
                + connectionResult.toString());

        Status status = Status.CONN_FAILED;
        if (connectionResult.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED)
            status = Status.SIGN_IN_REQUIRED;

        EventBus.getDefault()
                .post(new GAClientConnReceivedEvent(
                        status,
                        "Exception while connecting to Google Play services: "
                                + connectionResult.getErrorMessage(),

                        connectionResult)
                );
    }

    /**
     * Build a {@link GoogleApiClient} to authenticate the user and allow the application
     * to connect to the Fitness APIs. The included scopes should match the scopes needed
     * by your app (see the documentation for details).
     * Use the {@link GoogleApiClient.OnConnectionFailedListener}
     * to resolve authentication failures (for example, the user has not signed in
     * before, or has multiple accounts and must specify which account to use).
     *
     * @param apis      Array of Api Client {@link Api}
     * @param scopes    Array of {@link Scope} Api Client
     */
    private void buildGoogleApiClient(@NonNull Api[] apis, @Nullable Scope[] scopes) {
        // Create the Google API Client
        if (mClient == null) {
            mClient = buildGoogleApiClientWithApisAndScopes(apis, scopes);
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

    private void setPlayServiceConnection(boolean isConnected) {
        mIsConnected = isConnected;
    }
}
