package com.ayondo.academy.models.intent;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class THIntentTest
{
    private Resources resources;

    @Before public void setUp()
    {
        resources = RuntimeEnvironment.application.getApplicationContext().getResources();
    }

    @After public void tearDown()
    {
    }

    @Test public void defaultActionIsView()
    {
        assertThat(THIntent.getDefaultAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(new SimpleTHIntent(resources).getAction()).isEqualTo(Intent.ACTION_VIEW);
    }

    @Test public void constructorSetsPath()
    {
        THIntent intent = new SimpleTHIntent(resources);
        assertThat(intent.getData() + "").isEqualTo("tradehero://");
    }

    @Test public void canConvertStringAndInteger()
    {
        assertThat(resources.getString(R.string.intent_scheme)).isEqualTo("tradehero");
        assertThat(resources.getString(R.string.intent_uri_base, resources.getString(R.string.intent_scheme))).isEqualTo("tradehero://");

        assertThat(resources.getInteger(R.integer.intent_uri_path_index_action)).isEqualTo(0);
    }

    @Test public void baseUriPathIsFixed()
    {
        assertThat(THIntent.getBaseUriPath(resources)).isEqualTo("tradehero://");
    }

    @Test public void uriPathIsFixed()
    {
        assertThat(new SimpleTHIntent(resources).getUriPath()).isEqualTo("tradehero://");
    }

    @Test public void uriIsParsed()
    {
        Uri uri = new SimpleTHIntent(resources).getUri();
        assertThat(uri.getScheme()).isEqualTo("tradehero");
    }

    @Test public void hostUriIsWellFormed()
    {
        assertThat(THIntent.getHostUriPath(resources, R.string.intent_host_profile)).isEqualTo("tradehero://profile");
    }

    @Test public void actionUriIsWellFormed()
    {
        assertThat(THIntent.getActionUriPath(resources, R.string.intent_host_profile, R.string.intent_action_portfolio_open)).isEqualTo("tradehero://profile/open");
    }

    @Test public void actionFragmentIsNull()
    {
        assertThat(new SimpleTHIntent(resources).getActionFragment()).isNull();
    }

    @Test public void getBundleIsEmpty()
    {
        assertThat(new SimpleTHIntent(resources).getBundle().size()).isEqualTo(0);
    }
}
