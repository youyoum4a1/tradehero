package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import com.tradehero.th.persistence.alert.AlertCompactCacheRx;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static com.tradehero.THRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class BuySellFragmentTest
{
    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject AlertCompactCacheRx alertCompactCache;
    @Inject AlertCacheRx alertCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject SecurityIdCache securityIdCache;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    private BuySellFragment buySellFragment;
    @Inject DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
        currentUserId.set(123);
    }

    @After public void tearDown()
    {
        buySellFragment = null;
        alertCompactListCache.invalidateAll();
        alertCompactCache.invalidateAll();
        alertCache.invalidateAll();
        securityCompactCache.invalidateAll();
        securityIdCache.invalidateAll();
        userWatchlistPositionCache.invalidateAll();
    }

    //<editor-fold desc="Alert Button">
    private Bundle bundleWithGoogleSecurityId()
    {
        SecurityId googleId = new SecurityId("NYSE", "GOOG");
        Bundle args = new Bundle();
        BuySellFragment.putSecurityId(args, googleId);
        return args;
    }

    private void populateAlertCacheWithGoogleSecurityId()
    {
        AlertDTO googleAlert = new AlertDTO();
        googleAlert.id = 32;
        googleAlert.security = new SecurityCompactDTO();
        googleAlert.security.id = 9;
        googleAlert.security.exchange = "NYSE";
        googleAlert.security.symbol = "GOOG";
        AlertId alertId = googleAlert.getAlertId(currentUserId.toUserBaseKey());
        alertCache.onNext(alertId, googleAlert);
        AlertCompactDTOList alertCompactDTOs = new AlertCompactDTOList();
        alertCompactDTOs.add(googleAlert);
        alertCompactListCache.onNext(currentUserId.toUserBaseKey(), alertCompactDTOs);
    }

    @Test public void testWhenHasNoAlertShowsAddAlert()
    {
        alertCompactListCache.onNext(currentUserId.toUserBaseKey(), new AlertCompactDTOList());

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, bundleWithGoogleSecurityId());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddTrigger.getText()).isEqualTo("Add Alert");
    }

    @Test public void testWhenHasAlertShowsEditAlert() throws Throwable
    {
        populateAlertCacheWithGoogleSecurityId();
        assertThat(alertCompactListCache.get(currentUserId.toUserBaseKey()).toBlocking().first()).isNotNull();

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, bundleWithGoogleSecurityId());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddTrigger.getText()).isEqualTo("Edit Alert");
    }
    //</editor-fold>

    //<editor-fold desc="Watchlist Button">
    private void populateUserWatchlistCache()
    {
        WatchlistPositionDTO googleWatch = new WatchlistPositionDTO();
        googleWatch.id = 98;
        googleWatch.userId = 123;
        googleWatch.securityDTO = new SecurityCompactDTO();
        googleWatch.securityDTO.id = 43;
        googleWatch.securityDTO.exchange = "NYSE";
        googleWatch.securityDTO.symbol = "GOOG";

        WatchlistPositionDTOList watchlistPositionDTOs = new WatchlistPositionDTOList();
        watchlistPositionDTOs.add(googleWatch);

        userWatchlistPositionCache.onNext(
                currentUserId.toUserBaseKey(),
                watchlistPositionDTOs);
    }

    @Test public void testWhenNoWatchlistShowAddWatchlist()
    {
        userWatchlistPositionCache.onNext(
                currentUserId.toUserBaseKey(),
                new WatchlistPositionDTOList());

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, bundleWithGoogleSecurityId());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddWatchlist.getText()).isEqualTo("Add to Watchlist");
    }

    @Test public void testWhenHasWatchlistShowEditWatchlist() throws InterruptedException
    {
        populateUserWatchlistCache();
        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, bundleWithGoogleSecurityId());

        runBgUiTasks(3);

        assertThat(buySellFragment.mBtnAddWatchlist.getText()).isEqualTo("Edit in Watchlist");
    }
    //</editor-fold>
}
