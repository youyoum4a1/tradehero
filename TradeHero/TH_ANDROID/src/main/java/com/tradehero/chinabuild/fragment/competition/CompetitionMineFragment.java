package com.tradehero.chinabuild.fragment.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by huhaiping on 14-9-9.
 * 我参加的比赛
 */
public class CompetitionMineFragment extends CompetitionBaseFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public int getCompetitionPageType()
    {
        return CompetitionUtils.COMPETITION_PAGE_MINE;
    }
}
