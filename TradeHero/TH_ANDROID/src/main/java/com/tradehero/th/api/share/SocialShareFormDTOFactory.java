package com.ayondo.academy.api.share;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.ayondo.academy.api.share.wechat.WeChatDTOFactory;
import com.ayondo.academy.api.social.ReferralCodeDTO;
import com.ayondo.academy.api.social.ReferralCodeShareFormDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.models.share.ShareDestination;
import com.ayondo.academy.models.share.ShareDestinationWithEnum;
import com.ayondo.academy.models.share.WeChatShareDestination;

public class SocialShareFormDTOFactory
{
    @NonNull public static SocialShareFormDTO createForm(
            @NonNull Resources resources,
            @NonNull ShareDestination shareDestination,
            @NonNull DTO whatToShare)
    {
        if (shareDestination instanceof WeChatShareDestination)
        {
            return WeChatDTOFactory.createFrom(resources, whatToShare);
        }
        else if (shareDestination instanceof ShareDestinationWithEnum)
        {
            SocialNetworkEnum socialNetwork = ((ShareDestinationWithEnum) shareDestination).getSocialNetworkEnum();
            if (whatToShare instanceof AbstractDiscussionCompactDTO)
            {
                return TimelineItemShareFormDTOFactory.createFrom(
                        socialNetwork,
                        (AbstractDiscussionCompactDTO) whatToShare);
            }
            if (whatToShare instanceof ReferralCodeDTO)
            {
                return new ReferralCodeShareFormDTO(socialNetwork);
            }
        }
        throw new IllegalArgumentException("Unhandled ShareDestination " + shareDestination.getClass().getName() +
                ", and whatToShare " + whatToShare.getClass().getName());
    }
}
