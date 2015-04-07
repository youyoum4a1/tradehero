package com.tradehero.th.api.share;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.ReferralCodeDTO;
import com.tradehero.th.api.social.ReferralCodeShareFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationWithEnum;
import com.tradehero.th.models.share.WeChatShareDestination;

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
