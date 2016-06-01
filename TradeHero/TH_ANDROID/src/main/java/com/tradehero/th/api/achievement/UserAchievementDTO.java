package com.ayondo.academy.api.achievement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.achievement.key.UserAchievementId;
import com.ayondo.academy.api.users.UserBaseKey;
import java.util.Date;

public class UserAchievementDTO implements DTO
{
    public int id;
    public Date achievedAtUtc;
    public int userId;
    public int xpEarned;
    public int xpTotal;
    public int contiguousCount;
    public boolean isReset;
    @NonNull public AchievementDefDTO achievementDef;

    @NonNull public UserAchievementId getUserAchievementId()
    {
        return new UserAchievementId(id);
    }

    @NonNull public UserBaseKey getUserId()
    {
        return new UserBaseKey(userId);
    }

    public int getBaseExp()
    {
        return xpTotal - xpEarned;
    }

    public boolean shouldShow()
    {
        return  achievementDef.isQuest
                && !isReset
                && contiguousCount == 0;
    }

    /**
     * In principle, no two UserAchievementDTO should be returned with the same
     * AchievementDefDTO.
     * In practice, when this happens, it is the result of a race condition on
     * the server.
     * @param other
     * @return
     */
    public boolean isSameDefId(@Nullable UserAchievementDTO other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        return achievementDef.getAchievementsId().equals(other.achievementDef.getAchievementsId());
    }

    @Override public String toString()
    {
        return "UserAchievementDTO{" +
                "id=" + id +
                ", achievedAtUtc=" + achievedAtUtc +
                ", userId=" + userId +
                ", xpEarned=" + xpEarned +
                ", xpTotal=" + xpTotal +
                ", contiguousCount=" + contiguousCount +
                ", isReset=" + isReset +
                ", achievementDef=" + achievementDef +
                '}';
    }
}
