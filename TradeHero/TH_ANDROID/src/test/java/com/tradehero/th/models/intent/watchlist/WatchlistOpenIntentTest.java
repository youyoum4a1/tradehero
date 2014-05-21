package com.tradehero.th.models.intent.watchlist;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.TestConstants;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
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
public class WatchlistOpenIntentTest
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
        PortfolioId useless = new PortfolioId(234);
        PortfolioId portfolioId = new PortfolioId(567);
        assertEquals("tradehero://portfolio/watchlist/open/567", new WatchlistOpenIntent(useless).getWatchlistActionUriPath(portfolioId));
    }

    @Test public void portfolioActionUriIsWellFormed()
    {
        PortfolioId useless = new PortfolioId(234);
        PortfolioId portfolioId = new PortfolioId(567);
        WatchlistOpenIntent intent = new WatchlistOpenIntent(useless);
        Uri uri = intent.getWatchlistActionUri(portfolioId);
        List<String> pathSegments = uri.getPathSegments();

        assertEquals("tradehero", uri.getScheme());
        assertEquals("portfolio", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("watchlist", pathSegments.get(0));
        assertEquals("open", pathSegments.get(1));
        assertEquals(567, Integer.parseInt(pathSegments.get(2)));
    }

    @Test public void constructorPlacesPath()
    {
        PortfolioId portfolioId = new PortfolioId(567);
        WatchlistOpenIntent intent = new WatchlistOpenIntent(portfolioId);
        Uri uri = intent.getData();

        assertEquals("tradehero://portfolio/watchlist/open/567", uri + "");

        List<String> pathSegments = uri.getPathSegments();
        assertEquals("tradehero", uri.getScheme());
        assertEquals("portfolio", uri.getHost());
        assertEquals(3, pathSegments.size());
        assertEquals("watchlist", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_path_index_action)));
        assertEquals("open", pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_watchlist_path_index_action)));
        assertEquals(567, Integer.parseInt(pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_watchlist_path_index_id))));
    }

    @Test public void uriParserIsOk()
    {
        PortfolioId portfolioId = new PortfolioId(567);
        WatchlistOpenIntent intent = new WatchlistOpenIntent(portfolioId);
        Uri uri = intent.getData();
        assertTrue(portfolioId.equals(WatchlistOpenIntent.getPortfolioId(uri)));
    }

    @Test public void getPortfolioIdReturnsCorrect()
    {
        PortfolioId portfolioId = new PortfolioId(567);
        WatchlistOpenIntent intent = new WatchlistOpenIntent(portfolioId);

        assertTrue(portfolioId.equals(intent.getPortfolioId()));
    }

    @Test public void actionFragmentIsCorrect()
    {
        assertEquals(WatchlistPositionFragment.class, new WatchlistOpenIntent(new PortfolioId(567)).getActionFragment());
    }

    @Test public void bundleIsCorrect()
    {
        PortfolioId portfolioId = new PortfolioId(567);
        WatchlistOpenIntent intent = new WatchlistOpenIntent(portfolioId);
        Bundle bundle = intent.getBundle();
        // TODO have implementation
        //assertEquals(1, bundle.size());
        //assertEquals(567, bundle.getInt(PositionWatchlistFragment.BUNDLE));
    }

    @Test public void populateBundleKeepsExisting()
    {
        PortfolioId portfolioId = new PortfolioId(567);
        WatchlistOpenIntent intent = new WatchlistOpenIntent(portfolioId);
        Bundle bundle = new Bundle();
        bundle.putString("Whoo", "bah");
        intent.populate(bundle);

        // TODO have implementation
        //assertEquals(2, bundle.size());
        //assertEquals(567, bundle.getInt(PositionWatchlistFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
    }
}
