package com.tradehero.th.fragments.watchlist;

import com.tradehero.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;


@RunWith(RobolectricTestRunner.class)
//@Config(manifest = TestConstants.TRADEHERO_MANIFEST_PATH)
@Config(manifest = Config.NONE)
public class WatchlistPositionFragmentTest
{
    public static final String TAG = WatchlistPositionFragmentTest.class.getSimpleName();

    @Test public void testStartFragment()
    {
        WatchlistPositionFragment fragment = new WatchlistPositionFragment();
        FragmentTestUtil.startFragment(fragment);
    }
}
