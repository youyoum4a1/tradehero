package com.androidth.general.api.timeline;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.SocialNetworkEnum;

public class TimelineItemShareRequestDTO
{
    @NonNull public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(@NonNull SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
