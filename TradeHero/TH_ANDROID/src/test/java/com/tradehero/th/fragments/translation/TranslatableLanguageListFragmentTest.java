package com.ayondo.academy.fragments.translation;

import android.content.Context;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.translation.bing.BingTranslationToken;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.persistence.translation.TranslationTokenCacheRx;
import com.ayondo.academy.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static com.ayondo.academyRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TranslatableLanguageListFragmentTest
{
    @Inject Context context;
    @Inject TranslationTokenCacheRx translationTokenCache;
    @Inject DashboardNavigator dashboardNavigator;
    private TranslatableLanguageListFragment listFragment;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
        listFragment = null;
    }

    @Test public void shouldToastErrorOnStartUpBecauseNoTokenService() throws InterruptedException
    {
        ShadowToast.reset();
        listFragment = dashboardNavigator.pushFragment(TranslatableLanguageListFragment.class);
        assertThat(listFragment).isNotNull();

        runBgUiTasks(3);

        String latestToastText = ShadowToast.getTextOfLatestToast();
        assertThat(latestToastText).isEqualTo(context.getString(R.string.error_incomplete_info_message));
    }

    @Test public void shouldPopulateAdapterOnStartupWhenHasValidToken() throws InterruptedException
    {
        translationTokenCache.onNext(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        listFragment = dashboardNavigator.pushFragment(TranslatableLanguageListFragment.class);

        runBgUiTasks(3);

        assertThat(listFragment.listView.getAdapter().getCount()).isGreaterThan(10);
    }
}
