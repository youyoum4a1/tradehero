package com.tradehero.th.fragments.social.follower;

import dagger.Module;

@Module(
        injects = {
                FollowersFragment.class,
                FollowerListItemView.class,
                SendMessageFragment.class,
                FollowerRecyclerItemAdapter.class
        },
        library = true,
        complete = false
)
public class FragmentSocialFollowerModule
{
}
