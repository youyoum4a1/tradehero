package com.androidth.general.models.share;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.HasSocialNetworkEnum;
import com.androidth.general.api.social.SocialNetworkEnum;

public interface ShareDestinationWithEnum
        extends ShareDestination, HasSocialNetworkEnum
{
    @NonNull @Override SocialNetworkEnum getSocialNetworkEnum();
}
