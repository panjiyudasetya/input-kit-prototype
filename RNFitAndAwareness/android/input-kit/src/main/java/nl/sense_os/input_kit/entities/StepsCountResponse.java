package nl.sense_os.input_kit.entities;

import java.util.List;

/**
 * Created by panjiyudasetya on 5/3/17.
 */

public class StepsCountResponse {
    private boolean isQueryOk;
    private List<Content> contents;

    public StepsCountResponse(boolean isQueryOk, List<Content> contents) {
        this.isQueryOk = isQueryOk;
        this.contents = contents;
    }

    public boolean isQueryOk() {
        return isQueryOk;
    }

    public List<Content> getContents() {
        return contents;
    }
}
