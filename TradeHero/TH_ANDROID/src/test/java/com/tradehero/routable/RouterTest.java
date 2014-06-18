package com.tradehero.routable;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricMavenTestRunner.class)
public class RouterTest
{
    private Router router;
    private DashboardActivity activity;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        router = new Router(activity);
    }

    @Test(expected = RouteNotFoundException.class) public void shouldComplainWhenOpeningUrlDoesNotMatchAnyRegisteredUrls()
    {
        ShadowActivity shadowActivity = shadowOf(activity);

        router.map("test/:id", TestActivity.class);
        assertThat(shadowActivity.getNextStartedActivity()).isNull();

        router.open("test");
    }

    @Test public void shouldOpenRouteToCorrectActivity()
    {
        ShadowActivity shadowActivity = shadowOf(activity);

        String route = "test/:id";

        router.map(route, TestActivity.class);
        assertThat(shadowActivity.getNextStartedActivity()).isNull();

        router.open("test/12");
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent).isNotNull();
        assertThat(startedIntent.getComponent()).isNotNull();
        assertThat(startedIntent.getComponent().getClassName()).isSameAs(TestActivity.class.getName());
        assertThat(startedIntent.getExtras().get("id")).isEqualTo("12");
    }

    private class TestActivity extends Activity {}
}
