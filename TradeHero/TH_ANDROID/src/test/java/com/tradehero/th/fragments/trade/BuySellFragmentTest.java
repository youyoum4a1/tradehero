package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class BuySellFragmentTest
{
    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject AlertCompactListCache alertCompactListCache;
    @Inject AlertCompactCache alertCompactCache;
    @Inject AlertCache alertCache;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject SecurityIdCache securityIdCache;
    private BuySellFragment buySellFragment;
    private DashboardNavigator dashboardNavigator;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
        currentUserId.set(123);
    }

    @After
    public void tearDown()
    {
        buySellFragment = null;
        alertCompactListCache.invalidateAll();
        alertCompactCache.invalidateAll();
        alertCache.invalidateAll();
        securityCompactCache.invalidateAll();
        securityIdCache.invalidateAll();
    }

    //<editor-fold desc="Alert Button">
    private Bundle argsGoogle()
    {
        SecurityId googleId = new SecurityId("NYSE", "GOOG");
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, googleId.getArgs());
        return args;
    }

    private void populateAlertCacheWithGoogle()
    {
        AlertDTO googleAlert = new AlertDTO();
        googleAlert.id = 32;
        googleAlert.security = new SecurityCompactDTO();
        googleAlert.security.id = 9;
        googleAlert.security.exchange = "NYSE";
        googleAlert.security.symbol = "GOOG";
        AlertId alertId = googleAlert.getAlertId(currentUserId.toUserBaseKey());
        alertCache.put(alertId, googleAlert);
        AlertIdList alertIds = new AlertIdList();
        alertIds.add(alertId);
        alertCompactListCache.put(currentUserId.toUserBaseKey(), alertIds);
    }

    @Test public void testWhenHasNoAlertShowsAddAlert()
    {
        alertCompactListCache.put(currentUserId.toUserBaseKey(), new AlertIdList());

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, argsGoogle());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddTrigger.getText()).isEqualTo("Add Alert");
    }

    @Test public void testWhenHasAlertShowsEditAlert()
    {
        populateAlertCacheWithGoogle();

        buySellFragment = dashboardNavigator.pushFragment(BuySellFragment.class, argsGoogle());
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(buySellFragment.mBtnAddTrigger.getText()).isEqualTo("Edit Alert");
    }
    //</editor-fold>
}
