package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.SocialNetworkEnum;

import javax.inject.Inject;

public class SocialNetworkFactory
{
    @Inject public SocialNetworkFactory()
    {
    }

    public Class<? extends SocialFriendsFragment> findProperTargetFragment(SocialNetworkEnum socialNetworkEnum)
    {
        switch (socialNetworkEnum)
        {
            case WB:
                return SocialFriendsFragmentWeibo.class;
        }
        throw new IllegalArgumentException("Do not support " + socialNetworkEnum);
    }
}
