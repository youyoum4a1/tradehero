package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialNetworkFactory
{
    @NonNull public static Class<? extends SocialFriendsFragment> findProperTargetFragment(@NonNull SocialNetworkEnum socialNetworkEnum)
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
