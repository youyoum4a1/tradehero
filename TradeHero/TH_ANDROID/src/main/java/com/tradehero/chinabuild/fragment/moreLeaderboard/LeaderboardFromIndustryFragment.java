package com.tradehero.chinabuild.fragment.moreLeaderboard;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

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
