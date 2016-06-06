package com.androidth.general.api.level;

import com.androidth.general.common.api.BaseArrayList;

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
