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
import android.support.annotation.NonNull;

public class WeChatDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public WeChatDTOFactory()
    {
    }
    //</editor-fold>

    @NonNull public WeChatDTO createFrom(@NonNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(weChatDTO, abstractDiscussionCompactDTO);
        return weChatDTO;
    }

    @NonNull public WeChatDTO createFrom(@NonNull Context context, @NonNull UserAchievementDTO userAchievementDTO)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = userAchievementDTO.id;
        if(userAchievementDTO.achievementDef.isQuest)
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
}
