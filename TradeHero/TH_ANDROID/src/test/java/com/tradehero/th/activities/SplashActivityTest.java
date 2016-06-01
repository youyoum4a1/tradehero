package com.ayondo.academy.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.base.TestTHApp;
import com.ayondo.academy.persistence.prefs.FirstLaunch;
import com.ayondo.academy.utils.VersionUtils;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SplashActivityTest
{
    @Inject @FirstLaunch public BooleanPreference firstLaunchPreference;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test public void shouldHaveCopyrightShowing()
    {
        SplashActivity activity = Robolectric.setupActivity(SplashActivityExtended.class);

        ViewGroup copyrightView = (ViewGroup) activity.findViewById(R.id.copyright);
        assertThat(copyrightView).isNotNull();
    }

    @Test public void shouldDisplayAppVersionCorrectly()
    {
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);

        TextView appVersionView = (TextView) activity.findViewById(R.id.app_version);

        assertThat(appVersionView).isNotNull();
        assertThat(appVersionView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(appVersionView.getText()).isEqualTo(VersionUtils.getAppVersion(activity));
    }

    @Test public void shouldOpenAuthenticationScreenOnFirstLaunch()
    {
        firstLaunchPreference.set(true);
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);

        ShadowActivity shadowSplashActivity = shadowOf(activity);
        assertThat(activity.isFinishing()).isTrue();
        assertThat(shadowSplashActivity.getNextStartedActivity().getComponent().getClassName()).isEqualTo(AuthenticationActivity.class.getName());
        assertThat(firstLaunchPreference.get()).isFalse();
    }

    @Test public void shouldNotOpenAuthenticationScreenAfterFirstLaunch()
    {
        firstLaunchPreference.set(false);
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);

        assertThat(activity.isFinishing()).isFalse();
        assertThat(firstLaunchPreference.get()).isFalse();
    }
}
