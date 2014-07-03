package com.tradehero.th.api.social.key;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;

public class FriendsListKey implements DTOKey
{
    public UserBaseKey userBaseKey;
    public SocialNetworkEnum socialNetworkEnum;

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
