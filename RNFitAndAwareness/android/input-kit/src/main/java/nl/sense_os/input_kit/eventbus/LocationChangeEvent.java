package nl.sense_os.input_kit.eventbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;

/**
 * Created by panjiyudasetya on 5/12/17.
 */
@SuppressWarnings("SpellCheckingInspection")
public class LocationChangeEvent {
    private boolean isSuccessfull;
    private List<Content> contents;
    private String message;

    public LocationChangeEvent(@NonNull List<Content> contents) {
        this.contents = contents;
        this.isSuccessfull = true;
        this.message = "";
    }

    public LocationChangeEvent(boolean isSuccessfull,
                               @NonNull String message,
                               @Nullable List<Content> contents) {
        this.isSuccessfull = isSuccessfull;
        this.message = message;
        this.contents = contents;
    }

    public List<Content> getContents() {
        return contents;
    }

    public boolean isSuccessfull() {
        return isSuccessfull;
    }

    public String getMessage() {
        return message;
    }
}
