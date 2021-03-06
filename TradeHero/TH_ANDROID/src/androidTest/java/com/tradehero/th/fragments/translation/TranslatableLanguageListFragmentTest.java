package com.tradehero.th.fragments.translation;

import android.content.Context;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;

import static com.tradehero.THRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
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
