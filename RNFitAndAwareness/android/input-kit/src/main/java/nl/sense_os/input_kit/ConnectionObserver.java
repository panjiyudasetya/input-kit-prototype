package nl.sense_os.input_kit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import nl.sense_os.input_kit.listeners.InputKitConnectionListener;

/**
 * Created by panjiyudasetya on 5/31/17.
 */

public class ConnectionObserver {
    private final Map<String, InputKitConnectionListener> mObserver = new HashMap<>();

    /**
     * To avoid {@link java.util.ConcurrentModificationException} instead of removing the elements,
     * just set observer value to null.
     *
     * @param eventName Observer event name
     */
    private void removeObserver(@Nullable String eventName) {
        mObserver.put(eventName, null);
    }

    /**
     * Add Input Kit connection observer.
     * @param eventName Observer event name
     * @param listener  Input Kit connection listener
     */
    public void addObserver(@NonNull String eventName,
                            @NonNull InputKitConnectionListener listener) {
        mObserver.put(eventName, listener);
    }

    /**
     * Notifying all of connection observers and remove it immediately from the tree elements to avoid
     * Illegal callback invocation from React Native module error.
     * @param isAccessible  True if an input kit is accessible, False otherwise
     * @param message       Additional connection messages
     */
    public void notifyAll(boolean isAccessible, @NonNull String message) {
        for (Map.Entry<String, InputKitConnectionListener> consumer : mObserver.entrySet()) {
            InputKitConnectionListener listener = consumer.getValue();
            if (listener == null) continue;
            if (isAccessible) listener.onInputKitIsAccessible();
            else listener.onInputKitIsNotAccessible(message);
            removeObserver(consumer.getKey());
        }
    }
}
