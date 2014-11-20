package com.tradehero.th.api.share;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.ReferralCodeDTO;
import com.tradehero.th.api.social.ReferralCodeShareFormDTO;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationWithEnum;
import com.tradehero.th.models.share.WeChatShareDestination;
import javax.inject.Inject;

public class SocialShareFormDTOFactory
{
    @NonNull private final Context context;
    @NonNull private final WeChatDTOFactory weChatDTOFactory;
    @NonNull private final TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareFormDTOFactory(
            @NonNull Context context,
            @NonNull WeChatDTOFactory weChatDTOFactory,
            @NonNull TimelineItemShareFormDTOFactory timelineItemShareFormDTOFactory)
    {
        this.context = context;
        this.weChatDTOFactory = weChatDTOFactory;
        this.timelineItemShareFormDTOFactory = timelineItemShareFormDTOFactory;
    }
    //</editor-fold>

    @NonNull public SocialShareFormDTO createForm(
            @NonNull ShareDestination shareDestination,
            @NonNull DTO whatToShare)
    {
        if (shareDestination instanceof WeChatShareDestination)
        {
            return weChatDTOFactory.createFrom(context, whatToShare);
        }
        else if (shareDestination instanceof ShareDestinationWithEnum)
        {
            if (whatToShare instanceof AbstractDiscussionCompactDTO)
            {
                return timelineItemShareFormDTOFactory.createFrom(
                        ((ShareDestinationWithEnum) shareDestination).getSocialNetworkEnum(),
                        (AbstractDiscussionCompactDTO) whatToShare);
            }
            if (whatToShare instanceof ReferralCodeDTO)
            {
                return new ReferralCodeShareFormDTO(((ShareDestinationWithEnum) shareDestination).getSocialNetworkEnum());
            }
        }
        throw new IllegalArgumentException("Unhandled ShareDestination " + shareDestination.getClass().getName() +
                ", and whatToShare " + whatToShare.getClass().getName());
    }
}
