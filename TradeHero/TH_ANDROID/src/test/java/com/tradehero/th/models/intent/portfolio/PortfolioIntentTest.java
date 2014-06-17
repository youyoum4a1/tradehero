package com.tradehero.th.models.intent.portfolio;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class PortfolioIntentTest
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
        THIntent intent = new PortfolioIntent();
        Assert.assertEquals("tradehero://portfolio", intent.getData() + "");
    }

    @Test public void uriPathIsWellFormed()
    {
        assertEquals("tradehero://portfolio", new PortfolioIntent().getUriPath());
    }

    @Test public void typeIsDashboard()
    {
        assertEquals(DashboardTabType.PORTFOLIO, new PortfolioIntent().getDashboardType());
    }
}
