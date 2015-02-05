package com.tradehero.th.models.share;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.HasSocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkEnum;

public interface ShareDestinationWithEnum
        extends ShareDestination, HasSocialNetworkEnum
{
    @NonNull @Override SocialNetworkEnum getSocialNetworkEnum();
}
