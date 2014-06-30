package com.tradehero.th.models.intent;

import android.content.Intent;
import android.net.Uri;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.thm.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

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
        assertEquals(Intent.ACTION_VIEW, THIntent.getDefaultAction());
        assertEquals(Intent.ACTION_VIEW, new SimpleTHIntent().getAction());
    }

    @Test public void constructorSetsPath()
    {
        THIntent intent = new SimpleTHIntent();
        assertEquals("tradehero://", intent.getData() + "");
    }

    @Test public void canConvertStringAndInteger()
    {
        assertEquals("tradehero", THIntent.getString(R.string.intent_scheme));
        assertEquals("tradehero://", THIntent.getString(R.string.intent_uri_base, THIntent.getString(R.string.intent_scheme)));

        assertEquals(0, THIntent.getInteger(R.integer.intent_uri_path_index_action));
    }

    @Test public void baseUriPathIsFixed()
    {
        assertEquals("tradehero://", THIntent.getBaseUriPath());
    }

    @Test public void uriPathIsFixed()
    {
        assertEquals("tradehero://", new SimpleTHIntent().getUriPath());
    }

    @Test public void uriIsParsed()
    {
        Uri uri = new SimpleTHIntent().getUri();
        assertEquals("tradehero", uri.getScheme());
    }

    @Test public void hostUriIsWellFormed()
    {
        assertEquals("tradehero://profile", THIntent.getHostUriPath(R.string.intent_host_profile));
    }

    @Test public void actionUriIsWellFormed()
    {
        assertEquals("tradehero://profile/open", THIntent.getActionUriPath(R.string.intent_host_profile, R.string.intent_action_portfolio_open));
    }

    @Test public void actionFragmentIsNull()
    {
        assertNull(new SimpleTHIntent().getActionFragment());
    }

    @Test public void getBundleIsEmpty()
    {
        assertEquals(0, new SimpleTHIntent().getBundle().size());
    }
}
