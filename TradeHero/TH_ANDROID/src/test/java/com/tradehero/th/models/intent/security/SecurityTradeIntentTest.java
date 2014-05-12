package com.tradehero.th.models.intent.security;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.TestConstants;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.intent.OpenCurrentActivityHolder;
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


@RunWith(RobolectricTestRunner.class)
@Config(manifest = TestConstants.TRADEHERO_MANIFEST_PATH)
public class SecurityTradeIntentTest
{
    public static final String TAG = SecurityTradeIntentTest.class.getSimpleName();

    @Before public void setUp()
    {
        THIntent.currentActivityHolder = new OpenCurrentActivityHolder(Robolectric.getShadowApplication().getApplicationContext());
    }

    @After public void tearDown()
    {
        THIntent.currentActivityHolder = null;
    }

    @Test public void securityActionUriPathIsWellFormed()
    {
        SecurityId useless = new SecurityId("ABB", "CDD");
        SecurityId securityId = new SecurityId("EFF", "GHH");
        assertEquals("tradehero://trending/open/EFF/GHH", new SimpleSecurityTradeIntent(useless).getSecurityActionUriPath(securityId));
    }

    @Test public void securityActionUriIsWellFormed()
    {
        SecurityId useless = new SecurityId("ABB", "CDD");
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityTradeIntent intent = new SimpleSecurityTradeIntent(useless);
        Uri uri = intent.getSecurityActionUri(securityId);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("trending", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("open", pathSegments.get(0));
        assertEquals("EFF", pathSegments.get(1));
        assertEquals("GHH", pathSegments.get(2));
    }

    @Test public void constructorPlacesPath()
    {
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityTradeIntent intent = new SimpleSecurityTradeIntent(securityId);
        Uri uri = intent.getData();

        assertEquals("tradehero://trending/open/EFF/GHH", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("trending", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("open", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_path_index_action)));
        assertEquals("EFF", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_trade_security_path_index_exchange)));
        assertEquals("GHH", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_trade_security_path_index_security)));
    }

    @Test public void uriParserIsOk()
    {
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityTradeIntent intent = new SimpleSecurityTradeIntent(securityId);
        Uri uri = intent.getData();
        assertTrue(securityId.equals(SecurityTradeIntent.getSecurityId(uri)));
    }

    @Test public void getSecurityIdReturnsCorrect()
    {
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityTradeIntent intent = new SimpleSecurityTradeIntent(securityId);

        assertTrue(securityId.equals(intent.getSecurityId()));
    }

    @Test public void actionFragmentIsCorrect()
    {
        assertEquals(BuySellFragment.class, new SimpleSecurityTradeIntent(new SecurityId("A", "B")).getActionFragment());
    }

    @Test public void bundleIsCorrect()
    {
        SecurityId securityId = new SecurityId("A", "B");
        SecurityTradeIntent intent = new SimpleSecurityTradeIntent(securityId);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertEquals(2, bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE).size());
        assertTrue(securityId.equals(new SecurityId(bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE))));
    }

    @Test public void populateBundleKeepsExisting()
    {
        SecurityId securityId = new SecurityId("A", "B");
        SecurityTradeIntent intent = new SimpleSecurityTradeIntent(securityId);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertEquals(2, bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE).size());
        assertTrue(securityId.equals(new SecurityId(bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE))));
    }
}
