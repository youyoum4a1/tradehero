package com.tradehero.th.fragments.social.friend;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by wangliang on 14-5-27.
 */
public class FriendsListKey implements DTOKey {

    UserBaseKey userBaseKey;
    SocialNetworkEnum socialNetworkEnum;

    public FriendsListKey() {
    }

    public FriendsListKey(UserBaseKey userBaseKey, SocialNetworkEnum socialNetworkEnum) {
        this.userBaseKey = userBaseKey;
        this.socialNetworkEnum = socialNetworkEnum;
    }
}
