package com.tradehero.th.models.intent.competition;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.TestConstants;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 1/14/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = TestConstants.TRADEHERO_MANIFEST_PATH)
public class ProviderPageIntentTest
{
    public static final String TAG = ProviderPageIntentTest.class.getSimpleName();

    @Before public void setUp()
    {
        THIntent.context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {
        THIntent.context = null;
    }

    @Test public void providerActionUriPathIsWellFormed1()
    {
        ProviderId uselessId = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String uri = "def";
        assertEquals("tradehero://providers/456/pages/def", new ProviderPageIntent(uselessId, uselessUri).getProviderActionUriPath(providerId, uri));
    }

    @Test public void providerActionUriPathIsWellFormed2()
    {
        ProviderId uselessId = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String uri = "/competitionpages/rules?providerId=789&userId=234";
        assertEquals("tradehero://providers/456/pages/%252Fcompetitionpages%252Frules%253FproviderId%253D789%2526userId%253D234",
                new ProviderPageIntent(uselessId, uselessUri).getProviderActionUriPath(providerId, uri));
    }

    @Test public void providerActionUriIsWellFormed1()
    {
        ProviderId useless = new ProviderId(123);
        String uselessUri = "abc";
        ProviderId providerId = new ProviderId(456);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(useless, uselessUri);
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
        ProviderPageIntent intent = new ProviderPageIntent(useless, uselessUri);
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
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);
        Uri uri = intent.getData();

        assertEquals("tradehero://providers/123/pages/def", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("123", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_id)));
        assertEquals("pages", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_action)));
        assertEquals("def", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_encoded_page)));
    }

    @Test public void constructorPlacesPath2()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);
        Uri uri = intent.getData();

        assertEquals("tradehero://providers/123/pages/%252Fcompetitionpages%252Frules%253FproviderId%253D789%2526userId%253D234", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("providers", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("123", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_id)));
        assertEquals("pages", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_action)));
        assertEquals("%2Fcompetitionpages%2Frules%3FproviderId%3D789%26userId%3D234", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_provider_path_index_encoded_page)));
    }

    @Test public void uriParserIsOk1()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);
        Uri uri = intent.getData();
        assertTrue(providerId.equals(ProviderPageIntent.getProviderId(uri)));
        assertTrue(usedUri.equals(ProviderPageIntent.getForwardUriPath(uri)));
    }

    @Test public void uriParserIsOk2()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);
        Uri uri = intent.getData();
        assertTrue(providerId.equals(ProviderPageIntent.getProviderId(uri)));
        assertTrue(usedUri.equals(ProviderPageIntent.getForwardUriPath(uri)));
    }

    @Test public void getProviderIdForwardUriReturnsCorrect1()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "def";
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);

        assertTrue(providerId.equals(intent.getProviderId()));
        assertTrue(usedUri.equals(intent.getForwardUriPath()));
    }

    @Test public void getProviderIdForwardUriReturnsCorrect2()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "/competitionpages/rules?providerId=789&userId=234";
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);

        assertTrue(providerId.equals(intent.getProviderId()));
        assertTrue(usedUri.equals(intent.getForwardUriPath()));
    }

    @Test public void actionFragmentIsCorrect()
    {
        try
        {
            new ProviderPageIntent(new ProviderId(123), "abc").getActionFragment();
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
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertEquals(123, bundle.getInt(CompetitionFragment.BUNDLE_KEY_PROVIDER_ID));
    }

    @Test public void populateBundleKeepsExisting()
    {
        ProviderId providerId = new ProviderId(123);
        String usedUri = "abc";
        ProviderPageIntent intent = new ProviderPageIntent(providerId, usedUri);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertEquals(123, bundle.getInt(CompetitionFragment.BUNDLE_KEY_PROVIDER_ID));
    }
}
