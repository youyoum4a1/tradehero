package com.tradehero.th.fragments.watchlist;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.FragmentTestUtil;

@RunWith(RobolectricMavenTestRunner.class)
public class WatchlistPositionFragmentTest
{
    @Test public void testStartFragment()
    {
        WatchlistPositionFragment fragment = new WatchlistPositionFragment();
        FragmentTestUtil.startFragment(fragment);
    }
}
