package com.tradehero.th.models.share;

import com.tradehero.th.api.social.SocialNetworkEnum;

public interface ShareDestinationWithEnum extends ShareDestination
{
    SocialNetworkEnum getSocialNetworkEnum();
}
