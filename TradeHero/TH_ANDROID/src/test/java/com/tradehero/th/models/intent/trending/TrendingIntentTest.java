package com.tradehero.th.models.intent.trending;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class TrendingIntentTest
{
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
        assertThat(new TrendingIntent().getUriPath()).isEqualTo("tradehero://trending");
    }

    @Test public void typeIsDashboard()
    {
        assertThat(new TrendingIntent().getDashboardType()).isEqualTo(DashboardTabType.TRENDING);
    }
}
