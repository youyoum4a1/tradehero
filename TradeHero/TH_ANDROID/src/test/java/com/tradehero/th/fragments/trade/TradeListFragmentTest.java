package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.TestConstants;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trade.view.TradeListOverlayHeaderView;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricMavenTestRunner.class)
public class TradeListFragmentTest
{
    @Inject Context context;
    private TradeListFragment tradeListFragment;
    private DashboardNavigator dashboardNavigator;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @After
    public void tearDown()
    {
        tradeListFragment = null;
    }

    @Test(expected = NullPointerException.class) public void shouldNPEOnNullArgs()
    {
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentOnInvalidArgsKey()
    {
        if (!TestConstants.IS_INTELLIJ)
        {
            throw new IllegalArgumentException();
        }
        Bundle args = new Bundle();
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, args);
    }

    @Test public void testHeaderIsRecycled()
    {
        Bundle args = new Bundle();
        TradeListFragment.putPositionDTOKey(args, new OwnedPositionId(1, 2, 3));
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, args);

        assertNotNull(tradeListFragment.header);

        dashboardNavigator.popFragment();

        assertNull(tradeListFragment.header);
    }

    @Test public void testHeaderLosesListenerOnDestroyView()
    {
        Bundle args = new Bundle();
        TradeListFragment.putPositionDTOKey(args, new OwnedPositionId(1, 2, 3));
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, args);

        TradeListOverlayHeaderView tradeListFragmentHeader = tradeListFragment.header;

        assertNotNull(tradeListFragmentHeader.getListener());

        dashboardNavigator.popFragment();

        assertNull(tradeListFragmentHeader.getListener());
    }

    @Test public void testButtonListenerIsRecycled()
    {
        Bundle args = new Bundle();
        TradeListFragment.putPositionDTOKey(args, new OwnedPositionId(1, 2, 3));
        tradeListFragment = dashboardNavigator.pushFragment(TradeListFragment.class, args);

        assertNotNull(tradeListFragment.buttonListener);

        dashboardNavigator.popFragment();
        tradeListFragment.onDestroy();

        assertNull(tradeListFragment.buttonListener);
    }
}
