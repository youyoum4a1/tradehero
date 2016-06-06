package com.androidth.general.api.share.timeline;

import android.support.annotation.Nullable;
import com.androidth.general.api.discussion.key.DiscussionListKey;
import com.androidth.general.api.share.SocialShareFormDTOWithEnum;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;

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
