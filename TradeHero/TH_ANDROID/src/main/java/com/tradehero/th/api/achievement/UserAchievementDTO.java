package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class UserAchievementDTO implements DTO
{
    int id;
    Date achievedAtUtc;
    int xpEarned;
    int xpTotal;
    int contiguousCount;
    boolean isReset;
    AchievementDefDTO achievementDef;
    String displayString;

    public UserAchievementDTOKey getUserAchievementDTOKey()
    {
        return new UserAchievementDTOKey(id);
    }

}
