package nl.sense_os.input_kit.services.apis;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.concurrent.TimeUnit;

/**
 * Created by panjiyudasetya on 5/12/17.
 */

public class LocationUpdateApiHelper {
    private static final String TAG = "L.U.A.H";
    private GoogleApiClient googleApiClient;
    private final LocationRequest locationRequest;
    private final LocationSettingsRequest locationSettingRequest;
    private final LocationListener locationListener;

    public interface OnLocationUpdateFailureListener {
        void onPermissionNeeded(String message);
        void onSettingChangeUnavailable(String message);
    }

    public LocationUpdateApiHelper(@NonNull LocationListener locationListener,
                                   @NonNull GoogleApiClient googleApiClient) {
        this.locationRequest = createLocationRequest();
        this.locationSettingRequest = createLocationSettingRequest();
        this.googleApiClient = googleApiClient;
        this.locationListener = locationListener;
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates(@NonNull final OnLocationUpdateFailureListener listener) {
        LocationServices.SettingsApi.checkLocationSettings(
                googleApiClient,
                locationSettingRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                String message;
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                googleApiClient, locationRequest, locationListener);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        message = "Location settings are not satisfied. Attempting to upgrade "
                                + "location settings ";
                        Log.i(TAG, message);
                        listener.onPermissionNeeded(message);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        message = "Location settings are inadequate, and cannot be "
                                + "fixed here. Fix in Settings.";
                        Log.e(TAG, message);
                        listener.onSettingChangeUnavailable(message);
                        break;
                    default: break;
                }
            }
        });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                locationListener
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                //do nothing
            }
        });
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private static LocationRequest createLocationRequest() {
        return new LocationRequest()
                // Sets the desired interval for active location updates. This interval is
                // inexact. You may not receive updates at all if no location sources are available, or
                // you may receive them slower than requested. You may also receive updates faster than
                // requested if other applications are requesting location at a faster interval.
                .setInterval(TimeUnit.MINUTES.toMillis(30))
                // Sets the fastest rate for active location updates. This interval is exact, and your
                // application will never receive updates faster than this value.
                .setFastestInterval(TimeUnit.MINUTES.toMillis(15))
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private static LocationSettingsRequest createLocationSettingRequest() {
        return new LocationSettingsRequest
                .Builder()
                .addLocationRequest(createLocationRequest())
                .build();
    }
}
