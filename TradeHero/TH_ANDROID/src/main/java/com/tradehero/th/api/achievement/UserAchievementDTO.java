package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class UserAchievementDTO implements DTO
{
    public int id;
    public Date achievedAtUtc;
    public int xpEarned;
    public int xpTotal;
    public int contiguousCount;
    public boolean isReset;
    public AchievementDefDTO achievementDef;

    public UserAchievementId getUserAchievementId()
    {
        return new UserAchievementId(id);
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
}
