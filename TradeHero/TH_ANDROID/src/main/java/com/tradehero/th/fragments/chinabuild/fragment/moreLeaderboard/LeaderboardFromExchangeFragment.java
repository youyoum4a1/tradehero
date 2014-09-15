package com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard;

import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-8-29.
 */
public class LeaderboardFromExchangeFragment extends AbsLeaderboardFragment
{
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);


        setHeadViewMiddleMain("根据交易所");
    }
}
