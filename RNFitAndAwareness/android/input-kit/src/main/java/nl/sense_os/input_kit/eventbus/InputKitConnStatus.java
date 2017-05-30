package nl.sense_os.input_kit.eventbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;

import nl.sense_os.input_kit.constant.ConnectionStatus;
import nl.sense_os.input_kit.services.apis.InputKitApisHelper;

/**
 * Created by panjiyudasetya on 5/9/17.
 */

public class InputKitConnStatus {
    private ConnectionStatus status;
    private String message;
    private InputKitApisHelper apisHelper;
    private ConnectionResult connResult;

    public InputKitConnStatus(@NonNull ConnectionStatus status,
                              @NonNull String message) {
        this.status = status;
        this.message = message;
    }

    public InputKitConnStatus(@NonNull ConnectionStatus status,
                              @NonNull String message,
                              @NonNull ConnectionResult connResult) {
        this(status, message);
        this.connResult = connResult;
    }

    public InputKitConnStatus(@NonNull ConnectionStatus status,
                              @NonNull String message,
                              @NonNull ConnectionResult connResult,
                              @NonNull InputKitApisHelper apisHelper) {
        this(status, message, connResult);
        this.apisHelper = apisHelper;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public ConnectionResult getConnResult() {
        return connResult;
    }

    @Nullable
    public InputKitApisHelper getApisHelper() {
        return apisHelper;
    }
}
