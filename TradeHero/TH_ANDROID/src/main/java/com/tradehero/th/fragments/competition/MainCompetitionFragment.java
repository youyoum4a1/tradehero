package com.tradehero.th.fragments.competition;

import android.widget.AbsListView;

/**
 * Created by xavier on 1/17/14.
 */
public class MainCompetitionFragment extends CompetitionFragment
{
    public static final String TAG = MainCompetitionFragment.class.getSimpleName();

    private AbsListView listView;



    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
