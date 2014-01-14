package com.tradehero.th.models.intent;

import android.content.Intent;
import com.fasterxml.jackson.databind.Module;
import com.tradehero.th.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by xavier on 1/13/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
//@Config(manifest= Config.DEFAULT)
public class THIntentTest
{
    public static final String TAG = THIntentTest.class.getSimpleName();

    @Before public void setUp()
    {
        THIntent.context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {
    }

    @Test public void defaultActionIsView()
    {
        assertEquals(Intent.ACTION_VIEW, THIntent.getDefaultAction());
        assertEquals(Intent.ACTION_VIEW, new SimpleTHIntent().getAction());
    }

    @Test public void canConvertString()
    {
        assertEquals("tradehero", THIntent.getString(R.string.intent_scheme));
    }
}
