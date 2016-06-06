package com.androidth.general.api.share;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.share.timeline.TimelineItemShareFormDTOFactory;
import com.androidth.general.api.share.wechat.WeChatDTOFactory;
import com.androidth.general.api.social.ReferralCodeDTO;
import com.androidth.general.api.social.ReferralCodeShareFormDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.models.share.ShareDestination;
import com.androidth.general.models.share.ShareDestinationWithEnum;
import com.androidth.general.models.share.WeChatShareDestination;

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
