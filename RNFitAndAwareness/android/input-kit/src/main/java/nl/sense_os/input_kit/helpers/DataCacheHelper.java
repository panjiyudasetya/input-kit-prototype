package nl.sense_os.input_kit.helpers;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.sense_os.input_kit.entities.Content;

import static nl.sense_os.input_kit.utils.CollectionUtils.sortDesc;

/**
 * Created by panjiyudasetya on 5/5/17.
 */

public class DataCacheHelper {
    private static final Gson GSON = new Gson();

    public void save(@NonNull String key, @NonNull Content newCache) {
        List<Content> contents = load(key);
        if (contents.isEmpty()) contents = new ArrayList<>();
        contents.add(newCache);
        save(key, contents);
    }

    public void save(@NonNull String key, @NonNull List<Content> newCache) {
        if (newCache != null) sortDesc(newCache);
        Hawk.put(key, GSON.toJson(newCache));
    }

    @NonNull
    public List<Content> load(@NonNull String key) {
        String cache = Hawk.get(key, "");
        if (TextUtils.isEmpty(cache)) return Collections.emptyList();

        Type token = new TypeToken<List<Content>>() { }.getType();
        List<Content> results = GSON.fromJson(cache, token);
        if (results != null) sortDesc(results);
        return results == null ? Collections.<Content>emptyList() : results;
    }
}
