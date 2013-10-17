package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.fragments.base.ItemListFragment;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 7:21 PM Copyright (c) TradeHero */
public class LeaderboardDefListViewFragment extends ItemListFragment<LeaderboardDefDTO>
{
    @Override public Loader<List<LeaderboardDefDTO>> onCreateLoader(int i, Bundle bundle)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
