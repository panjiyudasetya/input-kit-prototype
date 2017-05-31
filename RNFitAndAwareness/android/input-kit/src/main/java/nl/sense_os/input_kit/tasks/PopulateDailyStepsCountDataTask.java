package nl.sense_os.input_kit.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import nl.sense_os.input_kit.entities.Content;
import nl.sense_os.input_kit.entities.StepsCountResponse;
import nl.sense_os.input_kit.helpers.DataCacheHelper;
import nl.sense_os.input_kit.services.apis.StepsCountApiHelper;

import static nl.sense_os.input_kit.constant.Preference.STEP_COUNT_CONTENT_KEY;

/**
 * Created by panjiyudasetya on 5/15/17.
 */

public class PopulateDailyStepsCountDataTask extends AsyncTask<Void, Integer, List<Content>> {
    private static final boolean USE_DATA_AGGREGATION = true;
    private static final DataCacheHelper CACHE = new DataCacheHelper();
    private final StepsCountApiHelper mApiHelper;
    private final long endTime;

    public PopulateDailyStepsCountDataTask(@NonNull StepsCountApiHelper api, long endTime) {
        this.mApiHelper = api;
        this.endTime = endTime;
    }

    @Override
    protected List<Content> doInBackground(Void... voids) {
        List<Content> cacheContents = CACHE.load(STEP_COUNT_CONTENT_KEY);
        StepsCountResponse response = mApiHelper.getAllStepCountHistory(USE_DATA_AGGREGATION);
        if (response.isQueryOk()) {
            List<Content> contents = response.getContents();
            CACHE.save(STEP_COUNT_CONTENT_KEY, contents);
            return contents;
        } else return cacheContents;
    }

    public void run() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
