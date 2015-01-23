package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.utils.metrics.Analytics;
import dagger.Lazy;

import javax.inject.Inject;


public class MeTimelineFragment extends TimelineFragment
    implements WithTutorial
{
    @Inject protected CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject Lazy<ResideMenu> resideMenuLazy;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //thRouter.save(getArguments(), currentUserId.toUserBaseKey());
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }
}
