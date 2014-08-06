package com.tradehero.th.utils.achievement;

import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementDTOKey;
import javax.inject.Inject;

public class UserAchievementDTOUtil
{
    @Inject public UserAchievementDTOUtil()
    {
        super();
    }

    public boolean shouldShow(UserAchievementDTOKey userAchievementDTOKey)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementDTOKey);
        if (userAchievementDTO == null || !userAchievementDTO.isReset)
        {
            return false;
        }
        //TODO check for type 'daily' / 'achievement' / 'etc'
        return true;
    }

    public UserAchievementDTO pop(UserAchievementDTOKey userAchievementDTOKey)
    {
        return dummy();
    }

    public UserAchievementDTO get(UserAchievementDTOKey userAchievementDTOKey)
    {
        return dummy();
    }

    private UserAchievementDTO dummy()
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        AchievementDefDTO achievementDefDTO = new AchievementDefDTO();

        userAchievementDTO.id = 1;
        achievementDefDTO.virtualDollars = 1000;
        achievementDefDTO.visual = "http://fc08.deviantart.net/fs70/f/2013/333/0/0/badge_for_gaming_signature__banner__or_avatar_by_sinner_pwa-d6w26w3.png";
        achievementDefDTO.header = "Achievement Unlocked";
        achievementDefDTO.thName = "Kicking Ass With Style";
        achievementDefDTO.text = "Placed #1 in the Leaderboard";
        achievementDefDTO.subText = "";
        achievementDefDTO.hexColor = "4891E4";

        userAchievementDTO.achievementDef = achievementDefDTO;
        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        return userAchievementDTO;
    }
}
