package com.tradehero.th.fragments.web;

import android.webkit.WebView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowWebView;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricMavenTestRunner.class)
public class THWebViewClientTest
{
    private DashboardActivity activity;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);

    }

    @Test public void shouldOpenGooglePlayMarketForMarketUrl()
    {
        WebView webView = new WebView(activity);
        THWebViewClient thWebViewClient = new THWebViewClient(activity);
        webView.setWebViewClient(thWebViewClient);
        webView.loadUrl("market://details?id=com.tradehero.thm");

        ShadowWebView shadowWebview = shadowOf(webView);
        ShadowActivity shadowActivity = shadowOf(activity);

        assertThat(activity.isFinishing()).isTrue();
        assertThat(shadowActivity.getNextStartedActivityForResult()).isNotNull();
    }
}