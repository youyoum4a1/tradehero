package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.SocialNetworkEnum;
import javax.inject.Inject;

public class SocialNetworkFactory
{
    //<editor-fold desc="Constructors">
    @Inject public SocialNetworkFactory()
    {
    }
    //</editor-fold>

    public Class<? extends SocialFriendsFragment> findProperTargetFragment(SocialNetworkEnum socialNetworkEnum)
    {
        switch (socialNetworkEnum)
        {
            case FB:
                return FacebookSocialFriendsFragment.class;

            case TW:
                return TwitterSocialFriendsFragment.class;

            case LN:
                return LinkedInSocialFriendsFragment.class;

            case WB:
                return WeiboSocialFriendsFragment.class;
        }
        throw new IllegalArgumentException("Do not support " + socialNetworkEnum);
    }
}
