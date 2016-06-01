package com.ayondo.academy.api.achievement;

import com.tradehero.common.api.BaseArrayList;

public class UserAchievementDTOList extends BaseArrayList<UserAchievementDTO>
{
    public int findBiggestXPTotal()
    {
        int xpTotal = 0;
        for (UserAchievementDTO userAchievementDTO : this)
        {
            if (xpTotal < userAchievementDTO.xpTotal)
            {
                xpTotal = userAchievementDTO.xpTotal;
            }
        }
        return xpTotal;
    }
}
