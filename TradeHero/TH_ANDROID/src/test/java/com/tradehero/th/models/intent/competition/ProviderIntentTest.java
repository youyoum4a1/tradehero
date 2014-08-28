package com.tradehero.th.models.intent.competition;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.models.intent.THIntent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class ProviderIntentTest
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
        THIntent intent = new ProviderIntent();
        assertThat(intent.getData() + "").isEqualTo("tradehero://providers");
    }

    @Test public void uriPathIsWellFormed()
    {
        assertThat(new ProviderIntent().getUriPath()).isEqualTo("tradehero://providers");
    }

    @Test public void typeIsDashboard()
    {
        assertThat(new ProviderIntent().getDashboardType()).isEqualTo(RootFragmentType.COMMUNITY);
    }
}
