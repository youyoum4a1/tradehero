package com.tradehero.th.fragments.social.friend;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;

// TODO move to API package
public class FriendsListKey implements DTOKey
{
    UserBaseKey userBaseKey;
    SocialNetworkEnum socialNetworkEnum;

    //<editor-fold desc="Constructors">
    public FriendsListKey()
    {
    }

    public FriendsListKey(UserBaseKey userBaseKey, SocialNetworkEnum socialNetworkEnum)
    {
        this.userBaseKey = userBaseKey;
        this.socialNetworkEnum = socialNetworkEnum;
    }
    //</editor-fold>
}
