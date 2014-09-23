package com.tradehero.th.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(THRobolectricTestRunner.class)
public class SplashActivityTest
{
    @Inject @FirstLaunch public BooleanPreference firstLaunchPreference;

    @Test public void shouldHaveCopyrightShowing()
    {
        SplashActivity activity = Robolectric.setupActivity(SplashActivityExtended.class);
        HierarchyInjector.inject(activity, this);

        ViewGroup copyrightView = (ViewGroup) activity.findViewById(R.id.copyright);
        assertThat(copyrightView).isNotNull();
    }

    @Test public void shouldDisplayAppVersionCorrectly()
    {
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);
        HierarchyInjector.inject(activity, this);

        TextView appVersionView = (TextView) activity.findViewById(R.id.app_version);

        assertThat(appVersionView).isNotNull();
        assertThat(appVersionView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(appVersionView.getText()).isEqualTo(VersionUtils.getAppVersion(activity));
    }

    @Test public void shouldOpenGuideScreenOnFirstLaunch()
    {
        firstLaunchPreference.set(true);
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);
        HierarchyInjector.inject(activity, this);

        ShadowActivity shadowSplashActivity = shadowOf(activity);
        assertThat(activity.isFinishing()).isTrue();
        assertThat(shadowSplashActivity.getNextStartedActivity().getComponent().getClassName()).isEqualTo(GuideActivity.class.getName());
        assertThat(firstLaunchPreference.get()).isFalse();
    }

    @Test public void shouldNotOpenGuideScreenAfterFirstLaunch()
    {
        firstLaunchPreference.set(false);
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);
        HierarchyInjector.inject(activity, this);

        assertThat(activity.isFinishing()).isFalse();
        assertThat(firstLaunchPreference.get()).isFalse();
    }
}
