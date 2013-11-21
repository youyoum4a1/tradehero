package com.tradehero.th.api.timeline;

import com.tradehero.th.api.social.SocialNetworkEnum;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 10:37 PM To change this template use File | Settings | File Templates. */
public class TimelineItemShareRequestDTO
{
    public static final String TAG = TimelineItemShareRequestDTO.class.getSimpleName();

    public final SocialNetworkEnum socialNetwork;

    public TimelineItemShareRequestDTO(SocialNetworkEnum socialNetwork)
    {
        this.socialNetwork = socialNetwork;
    }
}
