package com.tradehero.th.api.share.timeline;

import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.share.SocialShareFormDTOWithEnum;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;

public class TimelineItemShareFormDTO implements SocialShareFormDTOWithEnum
{
    public DiscussionListKey discussionListKey;
    public TimelineItemShareRequestDTO timelineItemShareRequestDTO;

    //<editor-fold desc="Constructors">
    public TimelineItemShareFormDTO()
    {
    }

    public TimelineItemShareFormDTO(
            DiscussionListKey discussionListKey,
            TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        this.discussionListKey = discussionListKey;
        this.timelineItemShareRequestDTO = timelineItemShareRequestDTO;
    }
    //</editor-fold>

    @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        if (timelineItemShareRequestDTO != null)
        {
            return timelineItemShareRequestDTO.socialNetwork;
        }
        return null;
    }
}
