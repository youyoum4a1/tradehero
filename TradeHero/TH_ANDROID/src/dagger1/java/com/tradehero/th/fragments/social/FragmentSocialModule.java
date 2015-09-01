package com.tradehero.th.fragments.social;

import com.tradehero.th.fragments.social.follower.FragmentSocialFollowerModule;
import com.tradehero.th.fragments.social.friend.FragmentSocialFriendModule;
import com.tradehero.th.fragments.social.hero.FragmentSocialHeroModule;
import com.tradehero.th.fragments.social.message.FragmentSocialMessageModule;
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
                AllRelationsFragment.class,
                RelationsListItemView.class,
                ShareDelegateFragment.class,
                FollowDialogView.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialModule
{
}
