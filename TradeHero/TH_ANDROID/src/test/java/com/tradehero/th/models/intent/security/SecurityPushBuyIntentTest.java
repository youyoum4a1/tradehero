package com.tradehero.th.models.intent.security;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.intent.OpenCurrentActivityHolder;
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
public class SecurityPushBuyIntentTest
{
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
        SecurityIntegerId unused = new SecurityIntegerId(123);
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId useless = new SecurityId("ABB", "CDD");
        SecurityId securityId = new SecurityId("EFF", "GHH");
        assertEquals("tradehero://security/456_EFF_GHH", new SecurityPushBuyIntent(unused, useless).getSecurityActionUriPath(used, securityId));
    }

    @Test public void securityActionUriIsWellFormed()
    {
        SecurityIntegerId unused = new SecurityIntegerId(123);
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId useless = new SecurityId("ABB", "CDD");
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityPushBuyIntent intent = new SecurityPushBuyIntent(unused, useless);
        Uri uri = intent.getSecurityActionUri(used, securityId);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("security", uri.getHost());
        assertEquals(1, pathSegments.size());
        assertEquals("456_EFF_GHH", pathSegments.get(0));
    }

    @Test public void constructorPlacesPath()
    {
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityPushBuyIntent intent = new SecurityPushBuyIntent(used, securityId);
        Uri uri = intent.getData();

        assertEquals("tradehero://security/456_EFF_GHH", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("security", uri.getHost());
        assertEquals(1, pathSegments.size());
        assertEquals("456_EFF_GHH", pathSegments.get(THIntent.getInteger(R.integer.intent_security_push_buy_index_elements)));
    }

    @Test public void uriParserIsOk()
    {
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityPushBuyIntent intent = new SecurityPushBuyIntent(used, securityId);
        Uri uri = intent.getData();
        assertTrue(used.equals(SecurityPushBuyIntent.getSecurityIntegerId(uri)));
        assertTrue(securityId.equals(SecurityPushBuyIntent.getSecurityId(uri)));
    }

    @Test public void getSecurityIdReturnsCorrect()
    {
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId securityId = new SecurityId("EFF", "GHH");
        SecurityPushBuyIntent intent = new SecurityPushBuyIntent(used, securityId);

        assertTrue(used.equals(intent.getSecurityIntegerId()));
        assertTrue(securityId.equals(intent.getSecurityId()));
    }

    @Test public void actionFragmentIsCorrect()
    {
        assertEquals(BuySellFragment.class, new SecurityPushBuyIntent(new SecurityIntegerId(3), new SecurityId("A", "B")).getActionFragment());
    }

    @Test public void bundleIsCorrect()
    {
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId securityId = new SecurityId("A", "B");
        SecurityPushBuyIntent intent = new SecurityPushBuyIntent(used, securityId);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertEquals(2, bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE).size());
        assertTrue(securityId.equals(new SecurityId(bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE))));
    }

    @Test public void populateBundleKeepsExisting()
    {
        SecurityIntegerId used = new SecurityIntegerId(456);
        SecurityId securityId = new SecurityId("A", "B");
        SecurityPushBuyIntent intent = new SecurityPushBuyIntent(used, securityId);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertEquals(2, bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE).size());
        assertTrue(securityId.equals(new SecurityId(bundle.getBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE))));
    }
}
