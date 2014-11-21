package com.tradehero.th.api.share.timeline;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import javax.inject.Inject;

public class TimelineItemShareFormDTOFactory
{
    @Inject public TimelineItemShareFormDTOFactory()
    {
        super();
    }

    @NonNull public TimelineItemShareFormDTO createFrom(
            @NonNull SocialNetworkEnum socialNetwork,
            @NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        TimelineItemShareFormDTO timelineItemShareFormDTO = new TimelineItemShareFormDTO();
        populateWith(timelineItemShareFormDTO, socialNetwork, abstractDiscussionCompactDTO);
        return timelineItemShareFormDTO;
    }

    protected void populateWith(
            @NonNull TimelineItemShareFormDTO timelineItemShareFormDTO,
            @NonNull SocialNetworkEnum socialNetwork,
            @NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        timelineItemShareFormDTO.timelineItemShareRequestDTO = new TimelineItemShareRequestDTO(socialNetwork);
        if (abstractDiscussionCompactDTO instanceof DiscussionDTO)
        {
            timelineItemShareFormDTO.discussionListKey = new DiscussionListKey(
                    //((DiscussionDTO) abstractDiscussionCompactDTO).inReplyToType, //why .inReplyToType is null here ,then can not share success.
                    DiscussionType.COMMENT,
                    abstractDiscussionCompactDTO.id);
        }
        else if (abstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
        {
            timelineItemShareFormDTO.discussionListKey = new DiscussionListKey(
                    DiscussionType.NEWS,
                    abstractDiscussionCompactDTO.id);
        }
        else if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            timelineItemShareFormDTO.discussionListKey = new DiscussionListKey(
                    DiscussionType.TIMELINE_ITEM,
                    abstractDiscussionCompactDTO.id);
        }
    }
}