package com.tradehero.th.fragments.social.follower;

import dagger.Module;

@Module(
        injects = {
                FollowerManagerFragment.class,
                FollowerRevenueReportFragment.class,
                UserFollowerDTOSetAdapter.class,
                AllFollowerFragment.class,
                PremiumFollowerFragment.class,
                FreeFollowerFragment.class,
                FollowerManagerInfoFetcher.class,
                FollowerPayoutManagerFragment.class,
                FollowerListItemView.class,
                SendMessageFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialFollowerModule
{
}
