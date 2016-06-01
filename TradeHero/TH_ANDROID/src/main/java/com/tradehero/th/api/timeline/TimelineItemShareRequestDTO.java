package com.ayondo.academy.api.timeline;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class TimelineItemShareRequestDTO
{
    @NonNull public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(@NonNull SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
