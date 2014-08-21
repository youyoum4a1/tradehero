package com.tradehero.th.persistence.achievement;

import com.tradehero.th.api.achievement.AchievementsDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserAchievementCacheDummy extends UserAchievementCache
{
    @Inject public UserAchievementCacheDummy(@NotNull AchievementServiceWrapper achievementServiceWrapper)
    {
        super(achievementServiceWrapper);
        getOrFetchAsync(new UserAchievementId(1));
    }

    @NotNull @Override public UserAchievementDTO fetch(@NotNull UserAchievementId key) throws Throwable
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        AchievementsDTO achievementsDTO = new AchievementsDTO();

        userAchievementDTO.id = key.key;
        achievementsDTO.virtualDollars = 50;
        achievementsDTO.visual =
                "http://uefaclubs.com/images/Sampdoria@2.-other-logo.png";
        achievementsDTO.header = "Achievement Unlocked";
        achievementsDTO.thName = "Master Trader " + key.key;
        achievementsDTO.text = "You have earned $50.00 TH$";
        achievementsDTO.subText = "Come back tomorrow to earn $50.00 TH$";
        achievementsDTO.hexColor = "4891E4";
        achievementsDTO.isQuest = true;

        userAchievementDTO.achievementDef = achievementsDTO;
        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        return userAchievementDTO;
    }
}
