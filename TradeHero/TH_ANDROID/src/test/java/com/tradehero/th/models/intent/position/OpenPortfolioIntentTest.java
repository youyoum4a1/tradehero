package com.tradehero.th.models.intent.position;

import android.net.Uri;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.thm.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.models.intent.THIntent;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class OpenPortfolioIntentTest
{
    @Before public void setUp()
    {
        THIntent.context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {
        THIntent.context = null;
    }

    @Test public void portfolioActionUriPathIsWellFormed()
    {
        PortfolioId useless = new PortfolioId(123);
        PortfolioId portfolioId = new PortfolioId(456);
        assertThat(new OpenPortfolioIntent(useless).getPortfolioActionUriPath(portfolioId)).isEqualTo("tradehero://portfolio/open/456");
    }

    @Test public void portfolioActionUriIsWellFormed()
    {
        PortfolioId useless = new PortfolioId(123);
        PortfolioId portfolioId = new PortfolioId(456);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(useless);
        Uri uri = intent.getPortfolioActionUri(portfolioId);
        List<String> pathSegments = uri.getPathSegments();

        assertThat(uri.getScheme()).isEqualTo("tradehero");
        assertThat(uri.getHost()).isEqualTo("portfolio");
        assertThat(pathSegments.size()).isEqualTo(2);
        assertThat(pathSegments.get(0)).isEqualTo("open");
        assertThat(pathSegments.get(1)).isEqualTo("456");
    }

    @Test public void constructorPlacesPath()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Uri uri = intent.getData();

        assertThat(uri + "").isEqualTo("tradehero://portfolio/open/123");

        List<String> pathSegments = uri.getPathSegments();
        assertThat(uri.getScheme()).isEqualTo("tradehero");
        assertThat(uri.getHost()).isEqualTo("portfolio");
        assertThat(pathSegments.size()).isEqualTo(2);
        assertThat(pathSegments.get(THIntent.getInteger(R.integer.intent_uri_path_index_action))).isEqualTo("open");
        assertThat(pathSegments.get(THIntent.getInteger(R.integer.intent_uri_action_portfolio_path_index_id))).isEqualTo("123");
    }

    @Test public void uriParserIsOk()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Uri uri = intent.getData();
        assertThat(portfolioId.equals(OpenPortfolioIntent.getPortfolioId(uri))).isTrue();
    }

    @Test public void getPortfolioIdReturnsCorrect()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);

        assertThat(portfolioId.equals(intent.getPortfolioId())).isTrue();
    }

    @Test public void actionFragmentIsCorrect()
    {
        assertThat(PositionListFragment.class == new OpenPortfolioIntent(new PortfolioId(123)).getActionFragment()).isTrue();
    }

    @Test public void bundleIsCorrect()
    {
        PortfolioId portfolioId = new PortfolioId(123);
        OpenPortfolioIntent intent = new OpenPortfolioIntent(portfolioId);
        Bundle bundle = intent.getBundle();
        assertThat(bundle.size()).isLessThanOrEqualTo(1);
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

        assertThat(bundle.size()).isLessThanOrEqualTo(2);
        // Need to change the Intent
        //assertEquals(123, bundle.getInt(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
    }
}
