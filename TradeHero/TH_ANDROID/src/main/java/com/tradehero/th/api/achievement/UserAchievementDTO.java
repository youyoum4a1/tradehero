package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class UserAchievementDTO implements DTO
{
    int id;
    String displayString;
    Date achievedAtUTC;
    int xpEarned;
    int xpTotal;
    int contiguousCount;
    boolean isReset;
    AchievementDefDTO achievementDef;

    public UserAchievementDTOKey getUserAchievementDTOKey()
    {
        return new UserAchievementDTOKey(id);
    }

}
