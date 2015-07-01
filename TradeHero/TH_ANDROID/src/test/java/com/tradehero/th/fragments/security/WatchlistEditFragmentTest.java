package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class WatchlistEditFragmentTest
{
    private static final SecurityId GOOGLE_SECURITY_ID = new SecurityId("NASDAQ", "GOOGL");
    private static final String GOOGLE_NAME = "Google Inc";
    private static final Double GOOGLE_STOCK_WATCHING_PRICE = 554.51;

    @Inject protected Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @Inject protected SecurityCompactCacheRx securityCompactCache;

    private WatchlistEditFragment watchlistFragment;
    @Inject DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        ActivityController<DashboardActivityExtended> activityController = Robolectric.buildActivity(DashboardActivityExtended.class).create().start();
        DashboardActivity activity = activityController.get();
        activity.inject(this);

        SecurityCompactDTO googleSecurityCompactDTO = new SecurityCompactDTO();
        googleSecurityCompactDTO.id = 162075;
        googleSecurityCompactDTO.name = GOOGLE_NAME;
        googleSecurityCompactDTO.lastPrice = GOOGLE_STOCK_WATCHING_PRICE;

        securityCompactCache.onNext(GOOGLE_SECURITY_ID, googleSecurityCompactDTO);

        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, GOOGLE_SECURITY_ID);
        dashboardNavigator.pushFragment(PreviousScreenFragment.class);
        watchlistFragment = dashboardNavigator.pushFragment(WatchlistEditFragment.class, args);
        watchlistFragment.securityCompactCache = securityCompactCache;

        activityController.resume();
    }

    @Test public void shouldNotBeNull()
    {
        assertThat(watchlistFragment).isNotNull();
        assertThat(watchlistFragment.getView()).isNotNull();
    }

    @Test public void shouldDisplaySecurityInfoCorrectly()
    {
        WatchlistViewHolder holder = new WatchlistViewHolder();
        ButterKnife.bind(holder, watchlistFragment.getView());

        assertThat(holder.securityName.getText()).isEqualTo(SecurityUtils.getDisplayableSecurityName(GOOGLE_SECURITY_ID));
        assertThat(holder.securityDesc.getText()).isEqualTo(GOOGLE_NAME);
        assertThat(holder.securityPrice.getText().toString()).isEqualTo(GOOGLE_STOCK_WATCHING_PRICE.toString());
    }

    @Test public void clickOnDoneButtonShouldGoBackToBuySellScreen()
    {
        WatchlistViewHolder holder = new WatchlistViewHolder();
        ButterKnife.bind(holder, watchlistFragment.getView());

        Robolectric.getBackgroundThreadScheduler().pause();

        watchlistFragment.watchlistServiceWrapper = mock(WatchlistServiceWrapper.class);

        // TODO conditionally return different answers, mimic for different results from server side, following one is in ideal condition
        when(watchlistFragment.watchlistServiceWrapper.createWatchlistEntryRx(any(WatchlistPositionFormDTO.class)))
                .thenAnswer(new Answer<Void>()
                {
                    @Override public Void answer(InvocationOnMock invocation) throws Throwable
                    {
                        dashboardNavigator.popFragment();
                        return null;
                    }
                });
        holder.btnDone.performClick();

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PreviousScreenFragment.class);
    }

    public static class WatchlistViewHolder
    {
        @Bind(R.id.edit_watchlist_item_security_name) TextView securityName;
        @Bind(R.id.edit_watchlist_item_security_desc) TextView securityDesc;
        @Bind(R.id.edit_watchlist_item_security_price) EditText securityPrice;

        @Bind(R.id.edit_watchlist_item_done) TextView btnDone;
        @Bind(R.id.edit_watchlist_item_delete) TextView btnDelete;
    }

    public static class PreviousScreenFragment extends Fragment
    {
    }
}
