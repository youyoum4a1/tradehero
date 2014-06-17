package com.tradehero.th.network.share;

import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import javax.inject.Inject;

public class SocialShareVerifier
{
    enum CanShareType
    {
        YES,
        NO,
        NEED_AUTH,
        TRY_AND_SEE,
    }

    @Inject public SocialShareVerifier()
    {
        super();
    }

    public CanShareType canShare(UserProfileCompactDTO currentUserProfile, SocialShareFormDTO toShare)
    {
        if (currentUserProfile == null || toShare == null)
        {
            return null;
        }

        if (toShare instanceof WeChatDTO)
        {
            return CanShareType.TRY_AND_SEE;
        }
        if (toShare instanceof TimelineItemShareFormDTO)
        {
            TimelineItemShareFormDTO tiShareDTO = (TimelineItemShareFormDTO) toShare;
            if (tiShareDTO.timelineItemShareRequestDTO == null)
            {
                return CanShareType.NO;
            }
            if (tiShareDTO.timelineItemShareRequestDTO.socialNetwork == null)
            {
                return CanShareType.NO;
            }
            switch (tiShareDTO.timelineItemShareRequestDTO.socialNetwork)
            {
                case FB:
                    return currentUserProfile.fbLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

                case LN:
                    return currentUserProfile.liLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

                case TW:
                    return currentUserProfile.twLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

                case WECHAT:
                    throw new IllegalStateException("WeChat is not shared like this");

                case WB:
                    return currentUserProfile.wbLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

                case TH:
                    throw new IllegalStateException("There is no sharing to TH");
            }
            throw new IllegalStateException("Unknown use case of TimelineItemShareFormDTO");
        }
        throw new IllegalStateException("Unhandled type " + toShare.getClass().getName());
    }
}
