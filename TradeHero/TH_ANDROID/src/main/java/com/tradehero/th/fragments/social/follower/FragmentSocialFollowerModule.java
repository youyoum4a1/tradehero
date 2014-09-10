package com.tradehero.th.fragments.social.follower;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                FollowerManagerFragment.class,
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
