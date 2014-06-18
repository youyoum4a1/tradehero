package com.tradehero.th.models.intent.competition;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.competition.CompetitionFragment;
import com.tradehero.th.models.intent.THIntent;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class OneProviderIntentTest
{
    @Before public void setUp()
    {
        THIntent.context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {
        THIntent.context = null;
    }

    @Test public void providerActionUriPathIsWellFormed()
    {
        ProviderId useless = new ProviderId(234);
        ProviderId providerId = new ProviderId(567);
        assertEquals("tradehero://providers/567/pages", new SimpleOneProviderIntent(useless).getProviderActionUriPath(providerId));
    }

    @Test public void providerActionUriIsWellFormed()
    {
        ProviderId useless = new ProviderId(234);
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(useless);
        Uri uri = intent.getProviderActionUri(providerId);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(2, pathSegments.size());
        assertEquals(567, Integer.parseInt(pathSegments.get(0)));
        assertEquals("pages", pathSegments.get(1));
    }

    @Test public void constructorPlacesPath()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(providerId);
        Uri uri = intent.getData();

        assertEquals("tradehero://providers/567/pages", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(2, pathSegments.size());
        assertEquals(567, Integer.parseInt(pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_id))));
        assertEquals("pages", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_action)));
    }

    @Test public void uriParserIsOk()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(providerId);
        Uri uri = intent.getData();
        assertTrue(providerId.equals(OneProviderIntent.getProviderId(uri)));
    }

    @Test public void getProviderIdReturnsCorrect()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(providerId);

        assertTrue(providerId.equals(intent.getProviderId()));
    }

    @Test public void bundleIsCorrect()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(providerId);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertEquals(567, (int) CompetitionFragment.getProviderId(bundle).key);
    }

    @Test public void populateBundleKeepsExisting()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(providerId);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertEquals(567, (int) CompetitionFragment.getProviderId(bundle).key);
    }
}
