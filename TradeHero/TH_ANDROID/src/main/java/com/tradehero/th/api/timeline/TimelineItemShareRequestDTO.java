package com.tradehero.th.api.timeline;

import com.tradehero.th.api.social.SocialNetworkEnum;

public class TimelineItemShareRequestDTO
{
    public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
