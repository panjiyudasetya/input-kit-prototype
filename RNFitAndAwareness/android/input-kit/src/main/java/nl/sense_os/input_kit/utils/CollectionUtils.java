package nl.sense_os.input_kit.utils;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.sense_os.input_kit.entities.Content;

/**
 * Created by panjiyudasetya on 5/8/17.
 */

public class CollectionUtils {
    public static void sortDesc(@NonNull List<Content> contents) {
        Collections.sort(contents, new Comparator<Content>() {
            @Override
            public int compare(Content content1, Content content2) {
                // Sort descending
                if (content1.getTimeStamp() > content2.getTimeStamp()) return -1;
                else {
                    if (content1.getTimeStamp() < content2.getTimeStamp()) return 1;
                    else return 0;
                }
            }
        });
    }
}
