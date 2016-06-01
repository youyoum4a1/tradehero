package com.ayondo.academy.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.alert.AlertCompactDTOList;
import com.ayondo.academy.api.alert.AlertDTO;
import com.ayondo.academy.api.alert.AlertId;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTOList;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.persistence.alert.AlertCacheRx;
import com.ayondo.academy.persistence.alert.AlertCompactCacheRx;
import com.ayondo.academy.persistence.alert.AlertCompactListCacheRx;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import com.ayondo.academy.persistence.security.SecurityIdCache;
import com.ayondo.academy.persistence.watchlist.UserWatchlistPositionCacheRx;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static com.ayondo.academyRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BuySellStockFragmentTest
{
    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject AlertCompactCacheRx alertCompactCache;
    @Inject AlertCacheRx alertCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject SecurityIdCache securityIdCache;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    private BuySellStockFragment buySellStockFragment;
    @Inject DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
        currentUserId.set(123);
    }

    @After public void tearDown()
    {
        buySellStockFragment = null;
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
        BuySellStockFragment.putRequisite(
                args,
                new AbstractBuySellFragment.Requisite(googleId));
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

        buySellStockFragment = dashboardNavigator.pushFragment(BuySellStockFragment.class, bundleWithGoogleSecurityId());
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        //assertThat(buySellStockFragment.mBtnAddTrigger.getText()).isEqualTo("Add Alert");
    }

    @Test public void testWhenHasAlertShowsEditAlert() throws Throwable
    {
        populateAlertCacheWithGoogleSecurityId();
        assertThat(alertCompactListCache.get(currentUserId.toUserBaseKey()).toBlocking().first()).isNotNull();

        buySellStockFragment = dashboardNavigator.pushFragment(BuySellStockFragment.class, bundleWithGoogleSecurityId());
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        //assertThat(buySellStockFragment.mBtnAddTrigger.getText()).isEqualTo("Edit Alert");
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

        buySellStockFragment = dashboardNavigator.pushFragment(BuySellStockFragment.class, bundleWithGoogleSecurityId());
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        //assertThat(buySellStockFragment.mBtnAddWatchlist.getText()).isEqualTo("Add to Watchlist");
    }

    @Test public void testWhenHasWatchlistShowEditWatchlist() throws InterruptedException
    {
        populateUserWatchlistCache();
        buySellStockFragment = dashboardNavigator.pushFragment(BuySellStockFragment.class, bundleWithGoogleSecurityId());

        runBgUiTasks(3);

        //assertThat(buySellStockFragment.mBtnAddWatchlist.getText()).isEqualTo("Edit in Watchlist");
    }
    //</editor-fold>
}
