package com.tradehero.th.api.share;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationWithEnum;
import com.tradehero.th.models.share.WeChatShareDestination;
import javax.inject.Inject;

public class SocialShareFormDTOFactory
{
    private final WeChatDTOFactory weChatDTOFactory;
    private final TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory;

    @Inject public SocialShareFormDTOFactory(
            WeChatDTOFactory weChatDTOFactory,
            TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory)
    {
        this.weChatDTOFactory = weChatDTOFactory;
        this.timelineItemShareFormDTOFactory = timelineItemShareFormDTOFactory;
    }

    public SocialShareFormDTO createForm(ShareDestination shareDestination,
            AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        if (shareDestination == null)
        {
            return null;
        }
        else if (shareDestination instanceof WeChatShareDestination)
        {
            return weChatDTOFactory.createFrom(abstractDiscussionCompactDTO);
        }
        else if (shareDestination instanceof ShareDestinationWithEnum)
        {
            return timelineItemShareFormDTOFactory.createFrom(
                    ((ShareDestinationWithEnum) shareDestination).getSocialNetworkEnum(),
                    abstractDiscussionCompactDTO);
        }
        throw new IllegalArgumentException("Unhandled ShareDestination " + shareDestination.getClass().getName());
    }
}
