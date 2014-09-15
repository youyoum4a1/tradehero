package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-9.
 * 所有比赛
 */
public class CompetitionAllFragment extends CompetitionBaseFragment
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

    @Override public void onResume()
    {
        super.onResume();
        Timber.d("OnRusme: CompetitionAllFragment  ");
    }

    public int getCompetitionPageType()
    {
        return CompetitionUtils.COMPETITION_PAGE_ALL;
    }
}
