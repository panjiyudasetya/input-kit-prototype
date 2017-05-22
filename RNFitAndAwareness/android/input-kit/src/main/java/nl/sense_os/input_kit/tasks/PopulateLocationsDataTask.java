package nl.sense_os.input_kit.tasks;

import android.os.AsyncTask;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.helpers.DataCacheHelper;

import static nl.sense_os.input_kit.constant.Preference.LOCATION_UPDATE_CONTENT_KEY;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public class PopulateLocationsDataTask extends AsyncTask<Void, Integer, List<Content>> {
    private static final DataCacheHelper CACHE = new DataCacheHelper();

    @Override
    protected List<Content> doInBackground(Void... voids) {
        return CACHE.load(LOCATION_UPDATE_CONTENT_KEY);
    }

    public void run() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}