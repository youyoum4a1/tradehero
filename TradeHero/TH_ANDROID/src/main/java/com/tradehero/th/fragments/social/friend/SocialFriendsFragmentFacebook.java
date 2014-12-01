package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import javax.inject.Inject;
import javax.inject.Provider;

public class SocialFriendsFragmentFacebook extends SocialFriendsFragment
{
    @Inject Provider<SocialFriendHandlerFacebook> facebookSocialFriendHandlerProvider;

    @Override
    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.FB;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.invite_social_friend, getString(R.string.facebook));
    }

    @Override @NonNull
    protected SocialFriendHandlerFacebook createFriendHandler()
    {
        return facebookSocialFriendHandlerProvider.get();
    }
}
