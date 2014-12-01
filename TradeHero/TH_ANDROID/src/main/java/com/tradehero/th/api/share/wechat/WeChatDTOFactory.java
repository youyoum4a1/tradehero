package com.tradehero.th.api.share.wechat;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.competition.CompetitionPreSeasonDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.social.ReferralCodeDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import javax.inject.Inject;

public class WeChatDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public WeChatDTOFactory()
    {
    }
    //</editor-fold>

    @NonNull public WeChatDTO createFrom(@NonNull Context context, @NonNull DTO whatToShare)
    {
        if (whatToShare instanceof AbstractDiscussionCompactDTO)
        {
            return createFrom((AbstractDiscussionCompactDTO) whatToShare);
        }
        else if (whatToShare instanceof UserAchievementDTO)
        {
            return createFrom(context, (UserAchievementDTO) whatToShare);
        }
        else if (whatToShare instanceof ReferralCodeDTO)
        {
            return createFrom(context, (ReferralCodeDTO) whatToShare);
        }
        else if (whatToShare instanceof CompetitionPreSeasonDTO)
        {
            return createFrom((CompetitionPreSeasonDTO) whatToShare);
        }
        throw new IllegalArgumentException("Unknown element to share " + whatToShare);
    }

    @NonNull public WeChatDTO createFrom(@NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(weChatDTO, abstractDiscussionCompactDTO);
        return weChatDTO;
    }

    protected void populateWith(
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

    @NonNull public WeChatDTO createFrom(@NonNull Context context, @NonNull UserAchievementDTO userAchievementDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(context, weChatDTO, userAchievementDTO);
        return weChatDTO;
    }

    protected void populateWith(@NonNull Context context, @NonNull WeChatDTO weChatDTO, @NonNull UserAchievementDTO userAchievementDTO)
    {
        weChatDTO.id = userAchievementDTO.id;
        if (userAchievementDTO.achievementDef.isQuest)
        {
            weChatDTO.type = WeChatMessageType.QuestBonus;
            weChatDTO.title = context.getString(R.string.share_to_wechat_quest_bonus_text, userAchievementDTO.achievementDef.thName);
        }
        else
        {
            weChatDTO.type = WeChatMessageType.Achievement;
            weChatDTO.title = context.getString(R.string.share_to_wechat_achievement_text, userAchievementDTO.achievementDef.thName);
        }
        weChatDTO.imageURL = userAchievementDTO.achievementDef.visual;
    }

    @NonNull public WeChatDTO createFrom(@NonNull Context context, @NonNull ReferralCodeDTO referralCodeDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(context, weChatDTO, referralCodeDTO);
        return weChatDTO;
    }

    protected void populateWith(@NonNull Context context, @NonNull WeChatDTO weChatDTO, @NonNull ReferralCodeDTO referralCodeDTO)
    {
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.Referral;
        weChatDTO.title = context.getString(R.string.share_to_wechat_referral_text, referralCodeDTO.referralCode);
    }

    @NonNull public WeChatDTO createFrom(@NonNull CompetitionPreSeasonDTO preSeasonDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(weChatDTO, preSeasonDTO);
        return weChatDTO;
    }

    protected void populateWith(@NonNull WeChatDTO weChatDTO, @NonNull CompetitionPreSeasonDTO preSeasonDTO)
    {
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.PreSeason;
        weChatDTO.title = preSeasonDTO.title;
    }
}
