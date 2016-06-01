package com.ayondo.academy.fragments.social.friend;

import android.content.Context;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import javax.inject.Inject;

public class SocialFriendsFragmentLinkedIn extends SocialFriendsFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override
    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.LN;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.invite_social_friend, getString(R.string.linkedin));
    }
}
