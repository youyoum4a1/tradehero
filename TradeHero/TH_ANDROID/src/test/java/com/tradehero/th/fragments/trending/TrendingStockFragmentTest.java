package com.ayondo.academy.fragments.trending;

import android.os.Bundle;
import com.ayondo.academyRobolectric;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.billing.THBillingInteractorRx;
import com.ayondo.academy.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TrendingStockFragmentTest
{
    @Inject CurrentUserId currentUserId;
    @Inject DashboardNavigator dashboardNavigator;
    private OpenTrendingFragment trendingFragment;
    private OwnedPortfolioId applicablePortfolioId;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
        currentUserId.set(23);
        applicablePortfolioId = new OwnedPortfolioId(23, 7);
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
        trendingFragment = null;
    }

    @Test public void uiRequestDollarPassedWhenCallExtraCash() throws InterruptedException
    {
        Bundle args = new Bundle();
        TrendingStockFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        trendingFragment = dashboardNavigator.pushFragment(
                OpenTrendingFragment.class,
                args);
        THRobolectric.runBgUiTasks(3);
        THBillingInteractorRx dummyInteractor = mock(THBillingInteractorRx.class);
        trendingFragment.set(dummyInteractor);

        trendingFragment.handleExtraCashItemOnClick();
        Thread.sleep(50);
    }
}
