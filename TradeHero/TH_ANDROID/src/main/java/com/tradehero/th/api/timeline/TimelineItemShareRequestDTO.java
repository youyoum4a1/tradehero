package com.tradehero.th.api.timeline;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class TimelineItemShareRequestDTO
{
    @NonNull public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(@NonNull SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
