package com.tradehero.th.api.timeline;

import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;

public class TimelineItemShareRequestDTO
{
    @NotNull public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(@NotNull SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
