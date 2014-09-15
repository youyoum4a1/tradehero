package com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by huhaiping on 14-8-29.
 */
public class LeaderboardFromIndustryFragment extends AbsLeaderboardFragment
{
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain("根据行业类别");
    }
}
