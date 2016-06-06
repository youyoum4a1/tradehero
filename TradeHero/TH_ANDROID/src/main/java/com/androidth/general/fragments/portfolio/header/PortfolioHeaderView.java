package com.androidth.general.fragments.portfolio.header;

import android.support.annotation.NonNull;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.users.UserProfileDTO;
import rx.Observable;

/**
 * Interface for the header displayed on a PositionListFragment
 */
public interface PortfolioHeaderView
{
    void linkWith(UserProfileDTO userProfileDTO);
    void linkWith(PortfolioCompactDTO portfolioCompactDTO);

    @NonNull Observable<UserAction> getUserActionObservable();

    class UserAction
    {
        @NonNull public final UserProfileDTO requested;

        public UserAction(@NonNull UserProfileDTO requested)
        {
            this.requested = requested;
        }
    }

    class FollowUserAction extends UserAction
    {
        public FollowUserAction(@NonNull UserProfileDTO requested)
        {
            super(requested);
        }
    }

    class TimelineUserAction extends UserAction
    {
        public TimelineUserAction(@NonNull UserProfileDTO requested)
        {
            super(requested);
        }
    }

    class UnFollowUserAction extends UserAction
    {
        public UnFollowUserAction(@NonNull UserProfileDTO requested)
        {
            super(requested);
        }
    }
}
