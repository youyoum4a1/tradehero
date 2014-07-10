package com.tradehero.th.models.intent;

import android.content.Intent;
import android.net.Uri;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THIntentTest
{
    @Before public void setUp()
    {
        THIntent.context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {
        THIntent.context = null;
    }

    @Test public void defaultActionIsView()
    {
        assertThat(THIntent.getDefaultAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(new SimpleTHIntent().getAction()).isEqualTo(Intent.ACTION_VIEW);
    }

    @Test public void constructorSetsPath()
    {
        THIntent intent = new SimpleTHIntent();
        assertThat(intent.getData() + "").isEqualTo("tradehero://");
    }

    @Test public void canConvertStringAndInteger()
    {
        assertThat(THIntent.getString(R.string.intent_scheme)).isEqualTo("tradehero");
        assertThat(THIntent.getString(R.string.intent_uri_base, THIntent.getString(R.string.intent_scheme))).isEqualTo("tradehero://");

        assertThat(THIntent.getInteger(R.integer.intent_uri_path_index_action)).isEqualTo(0);
    }

    @Test public void baseUriPathIsFixed()
    {
        assertThat(THIntent.getBaseUriPath()).isEqualTo("tradehero://");
    }

    @Test public void uriPathIsFixed()
    {
        assertThat(new SimpleTHIntent().getUriPath()).isEqualTo("tradehero://");
    }

    @Test public void uriIsParsed()
    {
        Uri uri = new SimpleTHIntent().getUri();
        assertThat(uri.getScheme()).isEqualTo("tradehero");
    }

    @Test public void hostUriIsWellFormed()
    {
        assertThat(THIntent.getHostUriPath(R.string.intent_host_profile)).isEqualTo("tradehero://profile");
    }

    @Test public void actionUriIsWellFormed()
    {
        assertThat(THIntent.getActionUriPath(R.string.intent_host_profile, R.string.intent_action_portfolio_open)).isEqualTo("tradehero://profile/open");
    }

    @Test public void actionFragmentIsNull()
    {
        assertThat(new SimpleTHIntent().getActionFragment()).isNull();
    }

    @Test public void getBundleIsEmpty()
    {
        assertThat(new SimpleTHIntent().getBundle().size()).isEqualTo(0);
    }
}
