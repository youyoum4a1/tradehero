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
        getOrFetchAsync(new UserAchievementId(2));
    }

    @NotNull @Override public UserAchievementDTO fetch(@NotNull UserAchievementId key) throws Throwable
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        AchievementsDTO achievementsDTO = new AchievementsDTO();

        userAchievementDTO.id = key.key;

        achievementsDTO.hexColor = "4891E4";
        if (key.key > 1)
        {
            achievementsDTO.header = "Daily Login Bonus!";
            achievementsDTO.thName = "Day 3";
            achievementsDTO.text = "You have earned TH$ 15,000";
            achievementsDTO.subText = "Come back tomorrow to earn another TH$ 20,000";
            achievementsDTO.virtualDollars = 15000;
            achievementsDTO.visual =
                    "http://icons.iconarchive.com/icons/custom-icon-design/pretty-office-11/512/coin-us-dollar-icon.png";
            achievementsDTO.isQuest = true;
            userAchievementDTO.contiguousCount = 3;
        }
        else
        {
            achievementsDTO.header = "Achievement Unlocked!";
            achievementsDTO.thName = "Master Trader I";
            achievementsDTO.text = "You have earned TH$ 10,000 for making 2 trades.";
            achievementsDTO.subText = "Make 3 more trades to earn another TH$ 15,000";
            achievementsDTO.virtualDollars = 10000;
            achievementsDTO.visual =
                    "http://images.clipartpanda.com/corps-clipart-free-vector-us-army-air-corps-shield-clip-art_106495_Us_Army_Air_Corps_Shield_clip_art_hight.png";
        }

        userAchievementDTO.achievementDef = achievementsDTO;
        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        return userAchievementDTO;
    }
}
