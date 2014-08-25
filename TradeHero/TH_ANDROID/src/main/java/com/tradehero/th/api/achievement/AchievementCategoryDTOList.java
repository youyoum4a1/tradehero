package com.tradehero.th.api.achievement;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public class AchievementCategoryDTOList extends BaseArrayList<AchievementCategoryDTO> implements DTO
{
    public AchievementCategoryIdList createKeys(@NotNull UserBaseKey userBaseKey)
    {
        AchievementCategoryIdList list = new AchievementCategoryIdList();
        for(AchievementCategoryDTO achievementCategoryDTO : this)
        {
            list.add(achievementCategoryDTO.getCategoryId(userBaseKey));
        }
        return list;
    }
}
