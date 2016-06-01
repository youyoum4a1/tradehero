package com.ayondo.academy.api.share.timeline;

import android.support.annotation.Nullable;
import com.ayondo.academy.api.discussion.key.DiscussionListKey;
import com.ayondo.academy.api.share.SocialShareFormDTOWithEnum;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.timeline.TimelineItemShareRequestDTO;

public class TimelineItemShareFormDTO implements SocialShareFormDTOWithEnum
{
    public DiscussionListKey discussionListKey;
    public TimelineItemShareRequestDTO timelineItemShareRequestDTO;

    //<editor-fold desc="Constructors">
    public TimelineItemShareFormDTO()
    {
    }
    //</editor-fold>

    @Override @Nullable public SocialNetworkEnum getSocialNetworkEnum()
    {
        if (timelineItemShareRequestDTO != null)
        {
            return timelineItemShareRequestDTO.socialNetwork;
        }
        return null;
    }
}
