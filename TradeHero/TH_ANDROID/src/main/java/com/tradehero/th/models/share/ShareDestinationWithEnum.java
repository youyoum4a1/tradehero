package com.ayondo.academy.models.share;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.HasSocialNetworkEnum;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public interface ShareDestinationWithEnum
        extends ShareDestination, HasSocialNetworkEnum
{
    @NonNull @Override SocialNetworkEnum getSocialNetworkEnum();
}
