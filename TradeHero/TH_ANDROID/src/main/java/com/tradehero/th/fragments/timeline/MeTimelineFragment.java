package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 3:35 PM Copyright (c) TradeHero */
public class MeTimelineFragment extends TimelineFragment
    implements WithTutorial
{
    @Inject protected CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Me);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (currentUserId != null)
        {
            getArguments().putInt(BUNDLE_KEY_SHOW_USER_ID, currentUserId.get());
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override protected void createUserProfileRetrievedMilestone()
    {
        userProfileRetrievedMilestone = new UserProfileRetrievedMilestone(currentUserId.toUserBaseKey());
    }

    @Override protected void createPortfolioCompactListRetrievedMilestone()
    {
        portfolioCompactListRetrievedMilestone = new PortfolioCompactListRetrievedMilestone(currentUserId.toUserBaseKey());
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }


    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }
}
