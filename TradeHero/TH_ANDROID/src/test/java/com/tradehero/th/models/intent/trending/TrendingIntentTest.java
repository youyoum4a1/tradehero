package com.tradehero.th.models.intent.trending;

import com.tradehero.TestConstants;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by xavier on 1/14/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = TestConstants.TRADEHERO_MANIFEST_PATH)
public class TrendingIntentTest
{
    public static final String TAG = TrendingIntentTest.class.getSimpleName();

    @Before public void setUp()
    {
        THIntent.context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {
        THIntent.context = null;
    }

    @Test public void constructorSetsPath()
    {
        THIntent intent = new TrendingIntent();
        Assert.assertEquals("tradehero://trending", intent.getData() + "");
    }

    @Test public void uriPathIsWellFormed()
    {
        assertEquals("tradehero://trending", new TrendingIntent().getUriPath());
    }

    @Test public void typeIsDashboard()
    {
        assertEquals(DashboardTabType.TRENDING, new TrendingIntent().getDashboardType());
    }
}
