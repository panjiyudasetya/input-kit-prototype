package nl.sense_os.input_kit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import nl.sense_os.input_kit.listeners.InputKitConnectionListener;

/**
 * Created by panjiyudasetya on 5/31/17.
 */

public class ConnectionObserver {
    private final ConcurrentMap<String, InputKitConnectionListener> mObserver = new ConcurrentHashMap<>();

    public void removeObserver(@Nullable String eventName) {
        mObserver.remove(eventName);
    }

    public void addObserver(@NonNull String eventName,
                            @NonNull InputKitConnectionListener listener) {
        mObserver.putIfAbsent(eventName, listener);
    }

    public void notifyAll(boolean isAccessible, @NonNull String message) {
        for (Map.Entry<String, InputKitConnectionListener> consumer : mObserver.entrySet()) {
            InputKitConnectionListener listener = consumer.getValue();
            if (listener == null) continue;
            if (isAccessible) listener.onInputKitIsAccessible();
            else listener.onInputKitIsNotAccessible(message);
        }
    }
}
