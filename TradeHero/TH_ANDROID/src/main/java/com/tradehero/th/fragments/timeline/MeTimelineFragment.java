package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 9/20/13 Time: 3:35 PM Copyright (c) TradeHero */
public class MeTimelineFragment extends TimelineFragment
{
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject @Named("Singleton") protected Lazy<UserProfileRetrievedMilestone>
            currentUserProfileRetrievedMilestone;
    @Inject @Named("Singleton") protected Lazy<PortfolioCompactListRetrievedMilestone>
            currentUserPortfolioCompactListRetrievedMilestone;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (currentUserBaseKeyHolder != null)
        {
            getArguments().putInt(BUNDLE_KEY_SHOW_USER_ID,
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override protected Milestone getUserProfileRetrievedMilestone()
    {
        return currentUserProfileRetrievedMilestone.get();
    }

    @Override protected Milestone getPortfolioCompactListRetrievedMilestone()
    {
        return currentUserPortfolioCompactListRetrievedMilestone.get();
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }
}
