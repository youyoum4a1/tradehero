package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.AbstractTestBase;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class BuySellFragmentTest extends AbstractTestBase
{
    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject AlertCompactListCache alertCompactListCache;
    @Inject AlertCompactCache alertCompactCache;
    @Inject AlertCache alertCache;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject SecurityIdCache securityIdCache;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    private BuySellFragment buySellFragment;
    private DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
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
        alertCache.put(alertId, googleAlert);
        AlertCompactDTOList alertCompactDTOs = new AlertCompactDTOList();
        alertCompactDTOs.add(googleAlert);
        alertCompactListCache.put(currentUserId.toUserBaseKey(), alertCompactDTOs);
    }

    @Test public void testWhenHasNoAlertShowsAddAlert()
    {
        alertCompactListCache.put(currentUserId.toUserBaseKey(), new AlertCompactDTOList());

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, bundleWithGoogleSecurityId());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddTrigger.getText()).isEqualTo("Add Alert");
    }

    @Test public void testWhenHasAlertShowsEditAlert() throws Throwable
    {
        populateAlertCacheWithGoogleSecurityId();
        assertThat(alertCompactListCache.getOrFetchSync(currentUserId.toUserBaseKey())).isNotNull();

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, bundleWithGoogleSecurityId());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddTrigger.getText()).isEqualTo("Edit Alert");
    }
    //</editor-fold>

    //<editor-fold desc="Watchlist Button">
    private void populateUserWatchlistCache()
    {
        WatchlistPositionDTOList watchlistPositionDTOs = new WatchlistPositionDTOList();
        WatchlistPositionDTO google = new WatchlistPositionDTO();
        google.securityDTO = new SecurityCompactDTO();
        google.securityDTO.exchange = "NYSE";
        google.securityDTO.symbol = "GOOD";
        watchlistPositionDTOs.add(google);
        userWatchlistPositionCache.put(
                currentUserId.toUserBaseKey(),
                watchlistPositionDTOs);
    }

    @Test public void testWhenNoWatchlistShowAddWatchlist()
    {
        userWatchlistPositionCache.put(
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

        runBgUiTasks(10);
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        // TODO any better way than sleeping?
        Thread.sleep(200); // This feels like a HACK but otherwise the test fails intermittently
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddWatchlist.getText()).isEqualTo("Edit in Watchlist");
    }
    //</editor-fold>
}
