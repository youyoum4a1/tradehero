package com.tradehero.th.api.share;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.models.share.FacebookShareDestination;
import com.tradehero.th.models.share.LinkedInShareDestination;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationWithEnum;
import com.tradehero.th.models.share.TwitterShareDestination;
import com.tradehero.th.models.share.WeChatShareDestination;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialShareFormDTOFactory
{
    @NotNull private final WeChatDTOFactory weChatDTOFactory;
    @NotNull private final TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareFormDTOFactory(
            @NotNull WeChatDTOFactory weChatDTOFactory,
            @NotNull TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory)
    {
        this.weChatDTOFactory = weChatDTOFactory;
        this.timelineItemShareFormDTOFactory = timelineItemShareFormDTOFactory;
    }
    //</editor-fold>

    @NotNull public SocialShareFormDTO createForm(
            @NotNull ShareDestination shareDestination,
            @NotNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        if (shareDestination instanceof WeChatShareDestination)
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
