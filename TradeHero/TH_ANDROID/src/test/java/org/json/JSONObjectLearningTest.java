package org.json;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by xavier on 2/4/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JSONObjectLearningTest
{
    public static final String TAG = JSONObjectLearningTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testConstructorWithMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("ab", "cd");
        JSONObject object = new JSONObject(map);
        assertEquals("{\"ab\":\"cd\"}", object.toString());
    }

    @Test public void testConstructorAlexli()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("type", "Basic");
        map.put("email", "alex+1@tradehero.mobi");
        map.put("password", "alexli");
        map.put("confirmPassword", "alexli");
        map.put("displayName", "alex+1");
        map.put("firstName", null);
        map.put("lastName", null);
        JSONObject object = new JSONObject(map);
        object.getClass();
    }
}
