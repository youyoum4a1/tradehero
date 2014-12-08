package com.tradehero.th.fragments.social;

import com.tradehero.th.fragments.social.follower.FragmentSocialFollowerComponent;
import com.tradehero.th.fragments.social.friend.FragmentSocialFriendComponent;
import com.tradehero.th.fragments.social.hero.FragmentSocialHeroComponent;
import com.tradehero.th.fragments.social.message.FragmentSocialMessageComponent;
import dagger.Component;

@Component
public interface FragmentSocialComponent extends
        FragmentSocialFollowerComponent,
        FragmentSocialFriendComponent,
        FragmentSocialHeroComponent,
        FragmentSocialMessageComponent
{
    void injectPeopleSearchFragment(PeopleSearchFragment target);
    void injectAllRelationsFragment(AllRelationsFragment target);
    void injectRelationsListItemView(RelationsListItemView target);

    void injectFollowDialogView(FollowDialogView target);
}
