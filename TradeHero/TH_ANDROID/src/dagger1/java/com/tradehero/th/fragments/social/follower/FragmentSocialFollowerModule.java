package com.tradehero.th.fragments.social.follower;

import dagger.Module;

@Module(
        injects = {
                FollowerManagerFragment.class,
                FollowerRevenueReportFragment.class,
                AllFollowerFragment.class,
                PremiumFollowerFragment.class,
                FreeFollowerFragment.class,
                FollowerPayoutManagerFragment.class,
                FollowerListItemView.class,
                FollowerRevenueListItemView.class,
                FollowerRoiListItemView.class,
                SendMessageFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialFollowerModule
{
}
