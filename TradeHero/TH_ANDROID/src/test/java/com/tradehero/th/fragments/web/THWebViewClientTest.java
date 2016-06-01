package com.ayondo.academy.fragments.web;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowWebView;
import retrofit.Endpoint;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class THWebViewClientTest
{
    private static final String MARKET_URL = "market://details?id=com.ayondo.academy";

    private DashboardActivity activity;
    private WebView webView;
    private THWebViewClient thWebViewClient;

    @Inject Endpoint apiEndpoint;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        webView = new WebView(activity);
        thWebViewClient = new THWebViewClient(activity);
        webView.setWebViewClient(thWebViewClient);

    }

    @Test public void shouldOpenGooglePlayMarketForMarketUrl()
    {
        thWebViewClient.shouldOverrideUrlLoading(webView, MARKET_URL);

        ShadowActivity shadowActivity = shadowOf(activity);

        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity).isNotNull();
        assertThat(nextStartedActivity.getComponent()).isNull();
        assertThat(nextStartedActivity.getData()).isNotNull();
        assertThat(nextStartedActivity.getData()).isEqualTo(Uri.parse(MARKET_URL));
    }

    @Test public void shouldOpenLandingPageSpecifiedInCompetitionUrl()
    {
        ShadowWebView shadowWebView = shadowOf(webView);

        thWebViewClient.shouldOverrideUrlLoading(webView, "tradehero://providers/23/pages/" + Uri.encode("http://google.com"));
        assertThat(shadowWebView.getLastLoadedUrl()).isEqualTo("http://google.com");

        thWebViewClient.shouldOverrideUrlLoading(webView, "tradehero://providers/23/pages/" + Uri.encode("competition/test_page?next=1"));
        assertThat(shadowWebView.getLastLoadedUrl()).isEqualTo(apiEndpoint.getUrl() + "competition/test_page?next=1");
    }
}