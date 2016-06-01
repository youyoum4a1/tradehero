package com.ayondo.academy.fragments.social;

import com.ayondo.academy.fragments.social.follower.FragmentSocialFollowerModule;
import com.ayondo.academy.fragments.social.friend.FragmentSocialFriendModule;
import com.ayondo.academy.fragments.social.hero.FragmentSocialHeroModule;
import com.ayondo.academy.fragments.social.message.FragmentSocialMessageModule;
import dagger.Module;

@Module(
        includes = {
                FragmentSocialFollowerModule.class,
                FragmentSocialFriendModule.class,
                FragmentSocialHeroModule.class,
                FragmentSocialMessageModule.class
        },
        injects = {
                PeopleSearchFragment.class,
                AllRelationsRecyclerFragment.class,
                ShareDelegateFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialModule
{
}
