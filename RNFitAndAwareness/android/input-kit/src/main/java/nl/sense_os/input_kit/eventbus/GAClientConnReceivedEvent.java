package nl.sense_os.input_kit.eventbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by panjiyudasetya on 5/9/17.
 */

public class GAClientConnReceivedEvent {
    private Status status;
    private String message;
    private ConnectionResult connResult;

    public enum Status {
        CONNECTED,
        CONN_SUSPENDED,
        CONN_FAILED,
        SUCCESSFULLY_SUBSCRIBED,
        ALREADY_SUBSCRIBED,
        FAILURE_TO_SUBSCRIBE,
        SIGN_IN_REQUIRED
    }

    public GAClientConnReceivedEvent(@NonNull Status status,
                                     @NonNull String message) {
        this.status = status;
        this.message = message;
    }

    public GAClientConnReceivedEvent(@NonNull Status status,
                                     @NonNull String message,
                                     @NonNull ConnectionResult connResult) {
        this(status, message);
        this.connResult = connResult;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public ConnectionResult getConnResult() {
        return connResult;
    }
}
