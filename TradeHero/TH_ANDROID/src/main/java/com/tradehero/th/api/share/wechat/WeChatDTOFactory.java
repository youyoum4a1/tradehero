package com.tradehero.th.api.share.wechat;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class WeChatDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public WeChatDTOFactory()
    {
    }
    //</editor-fold>

    @NotNull public WeChatDTO createFrom(@NotNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(weChatDTO, abstractDiscussionCompactDTO);
        return weChatDTO;
    }

    @NotNull public WeChatDTO createFrom(@NotNull Context context, @NotNull UserAchievementDTO userAchievementDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = userAchievementDTO.id;
        String dollars = THSignedNumber.builder(userAchievementDTO.achievementDef.virtualDollars).relevantDigitCount(1).withOutSign().toString();
        String xp = THSignedNumber.builder(userAchievementDTO.xpEarned).relevantDigitCount(1).withOutSign().toString();
        if(userAchievementDTO.achievementDef.isQuest)
        {
            weChatDTO.type = WeChatMessageType.QuestBonus;
            weChatDTO.title = context.getString(R.string.share_to_wechat_quest_bonus_text, userAchievementDTO.achievementDef.thName, dollars, xp);
        }
        else
        {
            weChatDTO.type = WeChatMessageType.Achievement;
            weChatDTO.title = context.getString(R.string.share_to_wechat_achievement_text, userAchievementDTO.achievementDef.thName, dollars, xp);
        }
        weChatDTO.imageURL = userAchievementDTO.achievementDef.visual;
        return weChatDTO;
    }

    protected void populateWith(
            @NotNull WeChatDTO weChatDTO,
            @NotNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
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
}
