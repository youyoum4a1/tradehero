package com.androidth.general.api.achievement;

import com.androidth.general.common.api.BaseArrayList;

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
