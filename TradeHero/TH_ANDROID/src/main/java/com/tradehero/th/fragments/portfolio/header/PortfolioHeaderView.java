package com.tradehero.th.fragments.portfolio.header;

import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Interface for the header displayed on a PositionListFragment
 */
public interface PortfolioHeaderView
{
    public void linkWith(UserProfileDTO userProfileDTO);
    public void linkWith(PortfolioDTO portfolioDTO);
    void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener);
    void setTimelineRequestedListener(OnTimelineRequestedListener timelineRequestedListener);

    public static interface OnFollowRequestedListener
    {
        void onFollowRequested(UserBaseKey userBaseKey);

        /**
         * when the user follow the hero success
         * @param hero
         */
        void onUserFollowed(UserBaseKey hero);
    }

    public static interface OnTimelineRequestedListener
    {
        void onTimelineRequested(UserBaseKey userBaseKey);
    }
}
