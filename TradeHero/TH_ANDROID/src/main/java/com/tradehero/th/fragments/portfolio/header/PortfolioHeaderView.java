package com.tradehero.th.fragments.portfolio.header;

import android.support.annotation.NonNull;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import rx.Observable;

/**
 * Interface for the header displayed on a PositionListFragment
 */
public interface PortfolioHeaderView
{
    public void linkWith(UserProfileDTO userProfileDTO);
    public void linkWith(PortfolioCompactDTO portfolioCompactDTO);

    @NonNull Observable<UserAction> getUserActionObservable();

    public static class UserAction
    {
        @NonNull public final UserProfileDTO requested;

        public UserAction(@NonNull UserProfileDTO requested)
        {
            this.requested = requested;
        }
    }

    public static class FollowUserAction extends UserAction
    {
        public FollowUserAction(@NonNull UserProfileDTO requested)
        {
            super(requested);
        }
    }

    public static class TimelineUserAction extends UserAction
    {
        public TimelineUserAction(@NonNull UserProfileDTO requested)
        {
            super(requested);
        }
    }
}
