package nl.sense_os.input_kit;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.orhanobut.hawk.Hawk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Before
    public void setup() {
        Hawk.init(InstrumentationRegistry.getTargetContext()).build();
    }

    @After
    public void tearDown() {
        Hawk.deleteAll();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("nl.sense_os.input_kit.test", appContext.getPackageName());
    }

    @Test
    public void testMapHawk() {
        String TAG = "map";
        Map<Integer, Boolean> map = new HashMap<>();
        map.put(1, true);
        map.put(2, true);

        Hawk.put(TAG, map);
        Log.d(TAG, "testPutMapHawk: " + map);

        Map<Integer, Boolean> result = Hawk.get(TAG);

        Log.d(TAG, "testGetMapHawk: " + result);
    }
}
