package com.tradehero.th.utils.achievement;

import com.tradehero.th.api.achievement.AchievementsDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserAchievementDTOUtil
{
    @NotNull private final UserAchievementCache userAchievementCache;

    @Inject public UserAchievementDTOUtil(UserAchievementCache userAchievementCache)
    {
        super();
        this.userAchievementCache = userAchievementCache;
    }

    public boolean shouldShow(UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        if (userAchievementDTO == null)
        {
            return false;
        }
        if (userAchievementDTO.achievementDef.isQuest && !userAchievementDTO.isReset && userAchievementDTO.contiguousCount == 0)
        {
            return false;
        }
        return true;
    }

    @Nullable public UserAchievementDTO pop(UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = userAchievementCache.get(userAchievementId);
        if(userAchievementDTO != null)
        {
            userAchievementCache.invalidate(userAchievementId);
        }
        return userAchievementDTO;
    }

    @Nullable public UserAchievementDTO get(UserAchievementId userAchievementId)
    {
        return userAchievementCache.get(userAchievementId);
    }

    public void remove(UserAchievementId userAchievementId)
    {
        userAchievementCache.invalidate(userAchievementId);
    }

    private UserAchievementDTO dummy()
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        AchievementsDTO achievementsDTO = new AchievementsDTO();

        userAchievementDTO.id = 1;
        achievementsDTO.virtualDollars = 50;
        achievementsDTO.visual =
                "http://fc08.deviantart.net/fs70/f/2013/333/0/0/badge_for_gaming_signature__banner__or_avatar_by_sinner_pwa-d6w26w3.png";
        achievementsDTO.header = "Achievement Unlocked";
        achievementsDTO.thName = "Master Trader I";
        achievementsDTO.text = "You have earned $50.00 TH$";
        achievementsDTO.subText = "Come back tomorrow to earn $50.00 TH$";
        achievementsDTO.hexColor = "4891E4";

        userAchievementDTO.achievementDef = achievementsDTO;
        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        return userAchievementDTO;
    }
}
