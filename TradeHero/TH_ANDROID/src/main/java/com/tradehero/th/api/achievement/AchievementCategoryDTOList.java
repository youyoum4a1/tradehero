package com.tradehero.th.api.achievement;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.achievement.key.AchievementCategoryIdList;
import com.tradehero.th.api.users.UserBaseKey;
import android.support.annotation.NonNull;

public class AchievementCategoryDTOList extends BaseArrayList<AchievementCategoryDTO> implements DTO
{
    @NonNull public AchievementCategoryIdList createKeys(@NonNull UserBaseKey userBaseKey)
    {
        AchievementCategoryIdList list = new AchievementCategoryIdList();
        for(AchievementCategoryDTO achievementCategoryDTO : this)
        {
            list.add(achievementCategoryDTO.getCategoryId(userBaseKey));
        }
        return list;
    }
}
