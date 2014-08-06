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
                return SocialFriendsFragmentFacebook.class;

            case TW:
                return SocialFriendsFragmentTwitter.class;

            case LN:
                return SocialFriendsFragmentLinkedIn.class;

            case WB:
                return SocialFriendsFragmentWeibo.class;
        }
        throw new IllegalArgumentException("Do not support " + socialNetworkEnum);
    }
}
