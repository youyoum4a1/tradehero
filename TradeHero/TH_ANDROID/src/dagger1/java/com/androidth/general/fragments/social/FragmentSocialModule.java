package com.androidth.general.fragments.social;

import com.androidth.general.fragments.social.follower.FragmentSocialFollowerModule;
import com.androidth.general.fragments.social.friend.FragmentSocialFriendModule;
import com.androidth.general.fragments.social.hero.FragmentSocialHeroModule;
import com.androidth.general.fragments.social.message.FragmentSocialMessageModule;
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
