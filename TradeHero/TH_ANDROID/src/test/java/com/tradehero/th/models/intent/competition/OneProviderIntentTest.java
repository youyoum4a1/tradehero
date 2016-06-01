package com.ayondo.academy.models.intent.competition;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.fragments.competition.ProviderVideoListFragment;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OneProviderIntentTest
{
    private Resources resources;

    @Before public void setUp()
    {
        resources = RuntimeEnvironment.application.getApplicationContext().getResources();
    }

    @After public void tearDown()
    {
    }

    @Test public void providerActionUriPathIsWellFormed()
    {
        ProviderId useless = new ProviderId(234);
        ProviderId providerId = new ProviderId(567);
        assertEquals("tradehero://providers/567/pages", new SimpleOneProviderIntent(resources, useless).getProviderActionUriPath(providerId));
    }

    @Test public void providerActionUriIsWellFormed()
    {
        ProviderId useless = new ProviderId(234);
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(resources, useless);
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
        OneProviderIntent intent = new SimpleOneProviderIntent(resources, providerId);
        Uri uri = intent.getData();

        assertEquals("tradehero://providers/567/pages", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(2, pathSegments.size());
        assertEquals(567, Integer.parseInt(pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_id))));
        assertEquals("pages", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_action)));
    }

    @Test public void uriParserIsOk()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(resources, providerId);
        Uri uri = intent.getData();
        assertTrue(providerId.equals(OneProviderIntent.getProviderId(resources, uri)));
    }

    @Test public void getProviderIdReturnsCorrect()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(resources, providerId);

        assertTrue(providerId.equals(intent.getProviderId()));
    }

    @Test public void bundleIsCorrect()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(resources, providerId);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertEquals(567, (int) ProviderVideoListFragment.getProviderId(bundle).key);
    }

    @Test public void populateBundleKeepsExisting()
    {
        ProviderId providerId = new ProviderId(567);
        OneProviderIntent intent = new SimpleOneProviderIntent(resources, providerId);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertEquals(567, (int) ProviderVideoListFragment.getProviderId(bundle).key);
    }
}
