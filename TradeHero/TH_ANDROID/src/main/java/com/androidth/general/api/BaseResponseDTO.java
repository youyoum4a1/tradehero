package com.androidth.general.api;

import android.support.annotation.Nullable;

import com.androidth.general.api.achievement.UserAchievementDTO;
import com.androidth.general.api.level.UserXPAchievementDTO;

import java.util.List;

/**
 * Here only to account for the fact that sometimes achievements are passed
 */
public class BaseResponseDTO
{
    @Nullable public List<UserXPAchievementDTO> xpEarned;
    @Nullable public List<UserAchievementDTO> achievements;
    @Nullable public Integer originalXP;

    @Nullable
    public Integer getOriginalXP() {
        return originalXP;
    }

    public void setOriginalXP(@Nullable Integer originalXP) {
        this.originalXP = originalXP;
    }

    public List<UserAchievementDTO> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<UserAchievementDTO> achievements) {
        this.achievements = achievements;
    }

    public List<UserXPAchievementDTO> getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(List<UserXPAchievementDTO> xpEarned) {
        this.xpEarned = xpEarned;
    }
}
