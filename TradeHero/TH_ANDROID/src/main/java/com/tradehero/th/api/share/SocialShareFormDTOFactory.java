package com.tradehero.th.api.share;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationWithEnum;
import com.tradehero.th.models.share.WeChatShareDestination;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class SocialShareFormDTOFactory
{
    @NonNull private final WeChatDTOFactory weChatDTOFactory;
    @NonNull private final TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareFormDTOFactory(
            @NonNull WeChatDTOFactory weChatDTOFactory,
            @NonNull TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory)
    {
        this.weChatDTOFactory = weChatDTOFactory;
        this.timelineItemShareFormDTOFactory = timelineItemShareFormDTOFactory;
    }
    //</editor-fold>

    @NonNull public SocialShareFormDTO createForm(
            @NonNull ShareDestination shareDestination,
            @NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
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
