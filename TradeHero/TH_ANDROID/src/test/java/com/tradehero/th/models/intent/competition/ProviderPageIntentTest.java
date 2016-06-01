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
public class ProviderPageIntentTest
{
    private Resources resources;

    @Before public void setUp()
    {
        resources = RuntimeEnvironment.application.getApplicationContext().getResources();
    }

    @After public void tearDown()
    {
    }

    @Test public void providerActionUriPathIsWellFormed1()
    {
        ProviderId uselessId = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String uri = "def";
        assertEquals("tradehero://providers/456/pages/def", new ProviderPageIntent(resources, uselessId, uselessUri).getProviderActionUriPath(providerId, uri));
    }

    @Test public void providerActionUriPathIsWellFormed2()
    {
        ProviderId uselessId = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String uri = "/competitionpages/rules?providerId=789&userId=234";
        assertEquals("tradehero://providers/456/pages/%252Fcompetitionpages%252Frules%253FproviderId%253D789%2526userId%253D234",
                new ProviderPageIntent(resources, uselessId, uselessUri).getProviderActionUriPath(providerId, uri));
    }

    // disable for now
    //@Test public void providerActionCompleteUriPathIsWellFormed2()
    //{
    //    ProviderId providerId = new ProviderId(456);
    //    String uri = "/competitionpages/rules?providerId=789&userId=234";
    //    assertEquals(Constants.BASE_API_URL + uri,
    //            new ProviderPageIntent(providerId, uri).getCompleteForwardUriPath());
    //}

    @Test public void providerActionUriIsWellFormed1()
    {
        ProviderId useless = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(resources, useless, uselessUri);
        Uri uri = intent.getProviderActionUri(providerId, usedUri);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("456", pathSegments.get(0));
        assertEquals("pages", pathSegments.get(1));
        assertEquals("def", pathSegments.get(2));
    }

    @Test public void providerActionUriIsWellFormed2()
    {
        ProviderId useless = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(resources, useless, uselessUri);
        Uri uri = intent.getProviderActionUri(providerId, usedUri);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("456", pathSegments.get(0));
        assertEquals("pages", pathSegments.get(1));
        assertEquals("%2Fcompetitionpages%2Frules%3FproviderId%3D789%26userId%3D234", pathSegments.get(2));
    }

    @Test public void constructorPlacesPath1()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);
        Uri uri = intent.getData();

        assertEquals("tradehero://providers/123/pages/def", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("123", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_id)));
        assertEquals("pages", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_action)));
        assertEquals("def", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_encoded_page)));
    }

    @Test public void constructorPlacesPath2()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);
        Uri uri = intent.getData();

        assertEquals("tradehero://providers/123/pages/%252Fcompetitionpages%252Frules%253FproviderId%253D789%2526userId%253D234", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("123", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_id)));
        assertEquals("pages", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_action)));
        assertEquals("%2Fcompetitionpages%2Frules%3FproviderId%3D789%26userId%3D234", pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_encoded_page)));
    }

    @Test public void uriParserIsOk1()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);
        Uri uri = intent.getData();
        assertTrue(providerId.equals(ProviderPageIntent.getProviderId(resources, uri)));
        assertTrue(usedUri.equals(ProviderPageIntent.getForwardUriPath(resources, uri)));
    }

    @Test public void uriParserIsOk2()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);
        Uri uri = intent.getData();
        assertTrue(providerId.equals(ProviderPageIntent.getProviderId(resources, uri)));
        assertTrue(usedUri.equals(ProviderPageIntent.getForwardUriPath(resources, uri)));
    }

    @Test public void getProviderIdForwardUriReturnsCorrect1()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);

        assertTrue(providerId.equals(intent.getProviderId()));
        assertTrue(usedUri.equals(intent.getForwardUriPath()));
    }

    @Test public void getProviderIdForwardUriReturnsCorrect2()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);

        assertTrue(providerId.equals(intent.getProviderId()));
        assertTrue(usedUri.equals(intent.getForwardUriPath()));
    }

    @Test public void actionFragmentIsCorrect()
    {
        try
        {
            new ProviderPageIntent(resources, new ProviderId(123), "abc").getActionFragment();
            assertTrue("We should not have reached here", false);
        }
        catch (RuntimeException e)
        {

        }
        catch (Throwable e)
        {
            assertTrue("Wrong exception thrown", false);
        }
    }

    @Test public void bundleIsCorrect()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "abc";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertEquals(123, (int) ProviderVideoListFragment.getProviderId(bundle).key);
    }

    @Test public void populateBundleKeepsExisting()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "abc";
        ProviderPageIntent intent = new ProviderPageIntent(resources, providerId, usedUri);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertEquals(123, (int) ProviderVideoListFragment.getProviderId(bundle).key);
    }
}
