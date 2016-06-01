package com.ayondo.academy.api.share.timeline;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.key.DiscussionListKey;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.timeline.TimelineItemDTO;
import com.ayondo.academy.api.timeline.TimelineItemShareRequestDTO;

public class TimelineItemShareFormDTOFactory
{
    @NonNull public static TimelineItemShareFormDTO createFrom(
            @NonNull SocialNetworkEnum socialNetwork,
            @NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        TimelineItemShareFormDTO timelineItemShareFormDTO = new TimelineItemShareFormDTO();
        populateWith(timelineItemShareFormDTO, socialNetwork, abstractDiscussionCompactDTO);
        return timelineItemShareFormDTO;
    }

    static void populateWith(
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