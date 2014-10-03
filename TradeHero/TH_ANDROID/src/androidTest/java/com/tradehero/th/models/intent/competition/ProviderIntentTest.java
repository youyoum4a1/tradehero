package com.tradehero.th.models.intent.competition;

import android.content.res.Resources;
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
    private Resources resources;

    @Before public void setUp()
    {
        resources = Robolectric.getShadowApplication().getApplicationContext().getResources();
    }

    @After public void tearDown()
    {
    }

    @Test public void constructorSetsPath()
    {
        THIntent intent = new ProviderIntent(resources);
        assertThat(intent.getData() + "").isEqualTo("tradehero://providers");
    }

    @Test public void uriPathIsWellFormed()
    {
        assertThat(new ProviderIntent(resources).getUriPath()).isEqualTo("tradehero://providers");
    }

    @Test public void typeIsDashboard()
    {
        assertThat(new ProviderIntent(resources).getDashboardType()).isEqualTo(RootFragmentType.COMMUNITY);
    }
}
