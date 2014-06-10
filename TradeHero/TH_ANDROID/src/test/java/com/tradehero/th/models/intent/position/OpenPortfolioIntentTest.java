package com.tradehero.th.models.intent.position;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.fragments.position.PositionListFragment;
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
public class OpenPortfolioIntentTest
{
    @Before public void setUp()
    {
        THIntent.currentActivityHolder = new OpenCurrentActivityHolder(Robolectric.getShadowApplication().getApplicationContext());
    }

    @After public void tearDown()
    {
        THIntent.currentActivityHolder = null;
    }

    @Test public void portfolioActionUriPathIsWellFormed()
    {
        PortfolioId useless = new PortfolioId(123);
        PortfolioId portfolioId = new PortfolioId(456);
        assertEquals("tradehero://portfolio/open/456", new OpenPortfolioIntent(useless).getPortfolioActionUriPath(portfolioId));
    }

    @Test public void portfolioActionUriIsWellFormed()
    {
        PortfolioId useless = new PortfolioId(123);
        PortfolioId portfolioId = new PortfolioId(456);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(useless);
        Uri uri = intent.getPortfolioActionUri(portfolioId);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("portfolio", uri.getHost());
        assertEquals(2, pathSegments.size());
        assertEquals("open", pathSegments.get(0));
        assertEquals("456", pathSegments.get(1));
    }

    @Test public void constructorPlacesPath()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Uri uri = intent.getData();

        assertEquals("tradehero://portfolio/open/123", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("portfolio", uri.getHost());
        assertEquals(2, pathSegments.size());
        assertEquals("open", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_path_index_action)));
        assertEquals("123", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_portfolio_path_index_id)));
    }

    @Test public void uriParserIsOk()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Uri uri = intent.getData();
        assertTrue(portfolioId.equals(OpenPortfolioIntent.getPortfolioId(uri)));
    }

    @Test public void getPortfolioIdReturnsCorrect()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);

        assertTrue(portfolioId.equals(intent.getPortfolioId()));
    }

    @Test public void actionFragmentIsCorrect()
    {
        assertEquals(PositionListFragment.class, new OpenPortfolioIntent(new PortfolioId(123)).getActionFragment());
    }

    @Test public void bundleIsCorrect()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Bundle bundle = intent.getBundle();
        assertEquals(1, bundle.size());
        assertTrue(false);
        // Need to change the Intent
        //assertEquals(123, bundle.getInt(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
    }

    @Test public void populateBundleKeepsExisting()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        assertEquals(2, bundle.size());
        assertTrue(false);
        // Need to change the Intent
        //assertEquals(123, bundle.getInt(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
    }
}
