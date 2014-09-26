package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserAchievementDTO implements DTO
{
    public int id;
    public Date achievedAtUtc;
    public int userId;
    public int xpEarned;
    public int xpTotal;
    public int contiguousCount;
    public boolean isReset;
    @NotNull public AchievementDefDTO achievementDef;

    @NotNull public UserAchievementId getUserAchievementId()
    {
        return new UserAchievementId(id);
    }

    @NotNull public UserBaseKey getUserId()
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
}
