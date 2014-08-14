package com.tradehero.th.fragments.translation;

import android.content.Context;
import com.tradehero.AbstractTestBase;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class TranslatableLanguageListFragmentTest extends AbstractTestBase
{
    @Inject Context context;
    @Inject TranslationTokenCache translationTokenCache;
    private DashboardNavigator dashboardNavigator;
    private TranslatableLanguageListFragment listFragment;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
        listFragment = null;
    }

    @Test public void shouldToastErrorOnStartUpBecauseNoTokenService()
    {
        ShadowToast.reset();
        listFragment = dashboardNavigator.pushFragment(TranslatableLanguageListFragment.class);
        assertThat(listFragment).isNotNull();

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        String latestToastText = ShadowToast.getTextOfLatestToast();
        assertThat(latestToastText).isEqualTo(context.getString(R.string.error_incomplete_info_message));
    }

    @Test public void shouldPopulateAdapterOnStartupWhenHasValidToken()
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        listFragment = dashboardNavigator.pushFragment(TranslatableLanguageListFragment.class);

        runBgUiTasks(10);

        assertThat(listFragment.listView.getAdapter().getCount()).isGreaterThan(10);
    }
}
