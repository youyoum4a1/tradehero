package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;
import retrofit.Callback;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricMavenTestRunner.class)
public class WatchlistEditFragmentTest
{
    private static final SecurityId GOOGLE_SECURITY_ID = new SecurityId("NASDAQ", "GOOGL");
    private static final String GOOGLE_NAME = "Google Inc";
    private static final Double GOOGLE_STOCK_WATCHING_PRICE = 554.51;
    private static final Integer GOOGLE_STOCK_WATCHING_QUANTITY = 1;

    @Inject protected Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected SecurityIdCache securityIdCache;

    private WatchlistEditFragment watchlistFragment;
    private DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        ActivityController<DashboardActivity> activityController = Robolectric.buildActivity(DashboardActivity.class).create().start();
        DashboardActivity activity = activityController.get();
        dashboardNavigator = spy(activity.getDashboardNavigator());

        // TODO should we do like following, or should we get the object by deserialize json data from test resource?
        SecurityCompactDTO googleSecurityCompactDTO = new SecurityCompactDTO();
        googleSecurityCompactDTO.id = 162075;
        googleSecurityCompactDTO.name = GOOGLE_NAME;
        googleSecurityCompactDTO.lastPrice = GOOGLE_STOCK_WATCHING_PRICE;

        SecurityCompactCache securityCompactCache = spy(new SecurityCompactCache(securityServiceWrapper, securityPositionDetailCache, securityIdCache));
        when(securityCompactCache.get(any(SecurityId.class))).thenReturn(googleSecurityCompactDTO);

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
        ButterKnife.inject(holder, watchlistFragment.getView());

        assertThat(holder.securityName.getText()).isEqualTo(SecurityUtils.getDisplayableSecurityName(GOOGLE_SECURITY_ID));
        assertThat(holder.securityDesc.getText()).isEqualTo(GOOGLE_NAME);
        assertThat(holder.securityPrice.getText().toString()).isEqualTo(GOOGLE_STOCK_WATCHING_PRICE.toString());
        assertThat(holder.securityQuantity.getText().toString()).isEqualTo(GOOGLE_STOCK_WATCHING_QUANTITY.toString());
    }

    @Test public void clickOnDoneButtonShouldGoBackToBuySellScreen()
    {
        WatchlistViewHolder holder = new WatchlistViewHolder();
        ButterKnife.inject(holder, watchlistFragment.getView());

        Robolectric.getBackgroundScheduler().pause();

        watchlistFragment.watchlistServiceWrapper = mock(WatchlistServiceWrapper.class);

        // TODO conditionally return different answers, mimic for different results from server side, following one is in ideal condition
        when(watchlistFragment.watchlistServiceWrapper.createWatchlistEntry(any(WatchlistPositionFormDTO.class), any(Callback.class)))
                .thenAnswer(new Answer<Void>()
                {
                    @Override public Void answer(InvocationOnMock invocation) throws Throwable
                    {
                        dashboardNavigator.popFragment();
                        return null;
                    }
                });
        holder.btnDone.performClick();

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PreviousScreenFragment.class);
    }

    public static class WatchlistViewHolder
    {
        @InjectView(R.id.edit_watchlist_item_security_name) TextView securityName;
        @InjectView(R.id.edit_watchlist_item_security_desc) TextView securityDesc;
        @InjectView(R.id.edit_watchlist_item_security_price) EditText securityPrice;
        @InjectView(R.id.edit_watchlist_item_security_quantity) EditText securityQuantity;

        @InjectView(R.id.edit_watchlist_item_done) TextView btnDone;
        @InjectView(R.id.edit_watchlist_item_delete) TextView btnDelete;
    }

    public static class PreviousScreenFragment extends Fragment
    {
    }
}
