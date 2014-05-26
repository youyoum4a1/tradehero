package com.tradehero.th.api.share.timeline;

import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;

public class TimelineItemShareFormDTO implements SocialShareFormDTO
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
}
