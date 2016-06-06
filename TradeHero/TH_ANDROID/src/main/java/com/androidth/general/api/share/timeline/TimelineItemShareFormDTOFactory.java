package com.androidth.general.api.share.timeline;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.DiscussionListKey;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.timeline.TimelineItemDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;

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