package com.tradehero.th.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.thm.R;
import com.tradehero.th.utils.VersionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class SplashActivityTest
{
    private SplashActivity activity;
    private ViewGroup copyrightView;
    private TextView appVersionView;

    @Before public void setUp()
    {
        activity = Robolectric.buildActivity(SplashActivity.class).create().visible().get();

        copyrightView = (ViewGroup) activity.findViewById(R.id.copyright);
        appVersionView = (TextView) activity.findViewById(R.id.app_version);
    }

    @Test public void shouldHaveCopyrightShowing()
    {
        assertThat(copyrightView).isNotNull();
    }

    @Test public void shouldDisplayAppVersionCorrectly()
    {
        assertThat(appVersionView).isNotNull();
        assertThat(appVersionView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(appVersionView.getText()).isEqualTo(VersionUtils.getAppVersion(activity));
    }
}
