package com.tradehero.th.fragments.portfolio.header;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by julien on 21/10/13
 * Interface for the header displayed on a PositionListFragment
 */
public interface PortfolioHeaderView
{
    public void bindOwnedPortfolioId(OwnedPortfolioId id);
    void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener);
    void setTimelineRequestedListener(OnTimelineRequestedListener timelineRequestedListener);

    public static interface OnFollowRequestedListener
    {
        void onFollowRequested(UserBaseKey userBaseKey);
    }

    public static interface OnTimelineRequestedListener
    {
        void onTimelineRequested(UserBaseKey userBaseKey);
    }
}
