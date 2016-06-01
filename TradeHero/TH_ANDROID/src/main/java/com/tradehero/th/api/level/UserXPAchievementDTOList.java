package com.ayondo.academy.api.level;

import com.tradehero.common.api.BaseArrayList;

public class UserXPAchievementDTOList extends BaseArrayList<UserXPAchievementDTO>
{
    public int findBiggestXPTotal()
    {
        int xpTotal = 0;
        for (UserXPAchievementDTO userXPAchievementDTO : this)
        {
            if (xpTotal < userXPAchievementDTO.xpTotal)
            {
                xpTotal = userXPAchievementDTO.xpTotal;
            }
        }
        return xpTotal;
    }
}
