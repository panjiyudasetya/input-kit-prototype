package nl.sense_os.input_kit.listeners;

import android.support.annotation.NonNull;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public interface ResultListener<T> {
    void onResult(boolean isSuccess, @NonNull T data);
}
