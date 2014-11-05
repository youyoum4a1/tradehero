package com.tradehero.th.api.timeline;

import com.tradehero.th.api.social.SocialNetworkEnum;
import android.support.annotation.NonNull;

public class TimelineItemShareRequestDTO
{
    @NonNull public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(@NonNull SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
