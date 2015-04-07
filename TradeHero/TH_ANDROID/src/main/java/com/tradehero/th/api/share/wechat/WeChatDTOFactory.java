package com.tradehero.th.api.share.wechat;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.activities.FacebookShareActivity;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.competition.CompetitionPreSeasonDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.social.ReferralCodeDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.models.number.THSignedNumber;

public class WeChatDTOFactory
{
    @NonNull public static WeChatDTO createFrom(@NonNull Resources resources, @NonNull DTO whatToShare)
    {
        if (whatToShare instanceof AbstractDiscussionCompactDTO)
        {
            return createFrom((AbstractDiscussionCompactDTO) whatToShare);
        }
        else if (whatToShare instanceof UserAchievementDTO)
        {
            return createFrom(resources, (UserAchievementDTO) whatToShare);
        }
        else if (whatToShare instanceof ReferralCodeDTO)
        {
            return createFrom(resources, (ReferralCodeDTO) whatToShare);
        }
        throw new IllegalArgumentException("Unknown element to share " + whatToShare);
    }

    @NonNull public static WeChatDTO createFrom(@NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(weChatDTO, abstractDiscussionCompactDTO);
        return weChatDTO;
    }

    static void populateWith(
            @NonNull WeChatDTO weChatDTO,
            @NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        weChatDTO.id = abstractDiscussionCompactDTO.id;
        if (abstractDiscussionCompactDTO instanceof DiscussionDTO)
        {
            weChatDTO.type = WeChatMessageType.CreateDiscussion;
            weChatDTO.title = ((DiscussionDTO) abstractDiscussionCompactDTO).text;
            if (((DiscussionDTO) abstractDiscussionCompactDTO).user != null &&
                    ((DiscussionDTO) abstractDiscussionCompactDTO).user.picture != null)
            {
                weChatDTO.imageURL = ((DiscussionDTO) abstractDiscussionCompactDTO).user.picture;
            }
        }
        else if (abstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
        {
            weChatDTO.type = WeChatMessageType.News;
            weChatDTO.title = ((NewsItemCompactDTO) abstractDiscussionCompactDTO).title;
        }
        else if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            weChatDTO.type = WeChatMessageType.Discussion;
            weChatDTO.title = ((TimelineItemDTO) abstractDiscussionCompactDTO).text;
            SecurityMediaDTO firstMediaWithLogo = ((TimelineItemDTO) abstractDiscussionCompactDTO).getFlavorSecurityForDisplay();
            if (firstMediaWithLogo != null && firstMediaWithLogo.url != null)
            {
                weChatDTO.imageURL = firstMediaWithLogo.url;
            }
        }
    }

    @NonNull public static WeChatDTO createFrom(@NonNull Resources resources, @NonNull UserAchievementDTO userAchievementDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(resources, weChatDTO, userAchievementDTO);
        return weChatDTO;
    }

    static void populateWith(
            @NonNull Resources resources,
            @NonNull WeChatDTO weChatDTO,
            @NonNull UserAchievementDTO userAchievementDTO)
    {
        weChatDTO.id = userAchievementDTO.id;
        if (userAchievementDTO.achievementDef.isQuest)
        {
            weChatDTO.type = WeChatMessageType.QuestBonus;
            weChatDTO.title = resources.getString(R.string.share_to_wechat_quest_bonus_text, userAchievementDTO.achievementDef.thName);
        }
        else
        {
            weChatDTO.type = WeChatMessageType.Achievement;
            weChatDTO.title = resources.getString(R.string.share_to_wechat_achievement_text, userAchievementDTO.achievementDef.thName);
        }
        weChatDTO.imageURL = userAchievementDTO.achievementDef.visual;
    }

    @NonNull public static WeChatDTO createFrom(@NonNull Resources resources, @NonNull ReferralCodeDTO referralCodeDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(resources, weChatDTO, referralCodeDTO);
        return weChatDTO;
    }

    static void populateWith(
            @NonNull Resources resources,
            @NonNull WeChatDTO weChatDTO,
            @NonNull ReferralCodeDTO referralCodeDTO)
    {
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.Referral;
        weChatDTO.title = resources.getString(R.string.share_to_wechat_referral_text, referralCodeDTO.referralCode);
    }

    @NonNull public static WeChatDTO createFrom(
            @NonNull Resources resources,
            @NonNull CompetitionPreSeasonDTO preSeasonDTO,
            @NonNull ProviderDTO providerDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(resources, preSeasonDTO, providerDTO, weChatDTO);
        return weChatDTO;
    }

    private static void populateWith(
            @NonNull Resources resources,
            @NonNull CompetitionPreSeasonDTO preSeasonDTO,
            @NonNull ProviderDTO providerDTO,
            @NonNull WeChatDTO weChatDTO)
    {
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.PreSeason;
        weChatDTO.title = resources.getString(R.string.share_to_wechat_preseason_text, providerDTO.name, preSeasonDTO.title);
    }

    @NonNull public static WeChatDTO createFrom(
            @NonNull Resources resources,
            @NonNull SecurityCompactDTO securityCompactDTO,
            double mTransactionQuantity,
            @NonNull String formattedPrice)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.type = WeChatMessageType.Trade;
        weChatDTO.title = String.format(
                        resources.getString(R.string.traded_facebook_share_message),
                        THSignedNumber.builder(mTransactionQuantity).build().toString(),
                        securityCompactDTO.name,
                        SecurityCompactDTOUtil.getShortSymbol(securityCompactDTO),
                        formattedPrice);
        if (securityCompactDTO.imageBlobUrl == null)
        {
            weChatDTO.imageURL = FacebookShareActivity.BASE_ART_WORK;
        }
        else
        {
            weChatDTO.imageURL = securityCompactDTO.imageBlobUrl;
        }
        return weChatDTO;
    }
}
