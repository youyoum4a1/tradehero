package com.ayondo.academy.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.TestConstants;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assume.assumeTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TradeListFragmentTest
{
    @Inject Context context;
    private TradeListFragment tradeListFragment;
    @Inject DashboardNavigator dashboardNavigator;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
    }

    @After
    public void tearDown()
    {
        tradeListFragment = null;
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJShouldNPEOnNullArgs()
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJShouldIllegalOnNullArgs()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJShouldThrowIllegalArgumentOnInvalidArgsKey()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        Bundle args = new Bundle();
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, args);
    }
}
