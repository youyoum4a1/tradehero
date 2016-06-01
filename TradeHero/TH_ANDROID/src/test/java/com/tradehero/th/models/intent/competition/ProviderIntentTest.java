package com.ayondo.academy.models.intent.competition;

import android.content.res.Resources;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.fragments.dashboard.RootFragmentType;
import com.ayondo.academy.models.intent.THIntent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProviderIntentTest
{
    private Resources resources;

    @Before public void setUp()
    {
        resources = RuntimeEnvironment.application.getApplicationContext().getResources();
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
