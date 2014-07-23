package com.tradehero.th.fragments.web;

import android.net.Uri;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.THRouter;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class WebViewFragmentTest
{
    private DashboardNavigator dashboardNavigator;
    @Inject THRouter thRouter;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
    }

    @Test public void routeShouldLoadWebViewFragment()
    {
        String url = "http://host.domain.tld/path?query=param";
        String encodedUrl = Uri.encode(url);
        String routePath = "web/url/" + encodedUrl;
        thRouter.open(routePath);

        assertThat(dashboardNavigator.getCurrentFragment().getClass().getCanonicalName())
                .isEqualTo(WebViewFragment.class.getCanonicalName());
    }

    @Test public void routeShouldPutUrlInFragment()
    {
        String url = "http://host.domain.tld/path?query=param";
        String encodedUrl = Uri.encode(url);
        String routePath = "web/url/" + encodedUrl;
        thRouter.open(routePath);

        WebViewFragment currentFragment = (WebViewFragment) dashboardNavigator.getCurrentFragment();
        assertThat(currentFragment.requiredUrlEncoded).isEqualTo(encodedUrl);
        assertThat(currentFragment.getLoadingUrl()).isEqualTo(url);
    }
}
