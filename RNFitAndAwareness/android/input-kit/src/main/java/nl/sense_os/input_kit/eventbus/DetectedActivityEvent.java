package nl.sense_os.input_kit.eventbus;

import android.support.annotation.NonNull;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;

/**
 * Created by panjiyudasetya on 5/8/17.
 */

public class DetectedActivityEvent {
    private List<Content> contents;

    public DetectedActivityEvent(@NonNull List<Content> contents) {
        this.contents = contents;
    }

    public List<Content> getContents() {
        return contents;
    }
}
