package com.tradehero.th.network.share;

import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.achievement.AchievementShareFormDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialShareVerifier
{
    enum CanShareType
    {
        YES,
        NO,
        NEED_AUTH,
        TRY_AND_SEE,
    }

    //<editor-fold desc="Constructors">
    @Inject public SocialShareVerifier()
    {
        super();
    }
    //</editor-fold>

    @NotNull public CanShareType canShare(
            @NotNull UserProfileCompactDTO currentUserProfile,
            @NotNull SocialShareFormDTO toShare)
    {
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
            return canShare(currentUserProfile, tiShareDTO.timelineItemShareRequestDTO.socialNetwork);
        }
        if (toShare instanceof AchievementShareFormDTO)
        {
            AchievementShareFormDTO aShareDTO = (AchievementShareFormDTO) toShare;
            CanShareType canShare;
            for (SocialNetworkEnum socialNetworkEnum : aShareDTO.achievementShareReqFormDTO.networks)
            {
                canShare = canShare(currentUserProfile, socialNetworkEnum);
                if (canShare != CanShareType.YES)
                {
                    return canShare;
                }
            }
            return CanShareType.YES;
        }
        throw new IllegalStateException("Unhandled type " + toShare.getClass().getName());
    }

    @NotNull protected CanShareType canShare(
            @NotNull UserProfileCompactDTO currentUserProfile,
            @NotNull SocialNetworkEnum socialNetworkEnum)
    {
        switch (socialNetworkEnum)
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

            default:
                throw new IllegalArgumentException("Unhandled SocialNetworkEnum." + socialNetworkEnum)    ;
        }
    }
}
