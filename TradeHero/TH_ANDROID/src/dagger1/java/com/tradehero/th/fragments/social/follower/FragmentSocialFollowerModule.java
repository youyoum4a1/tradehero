package com.ayondo.academy.fragments.social.follower;

import dagger.Module;

@Module(
        injects = {
                FollowersFragment.class,
                SendMessageFragment.class,
                FollowerRecyclerItemAdapter.class
        },
        library = true,
        complete = false
)
public class FragmentSocialFollowerModule
{
}
