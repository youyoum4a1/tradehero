package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseKey;

public class AchievementCategoryId implements DTOKey
{
    public final int userId;
    public final int categoryId;

    public AchievementCategoryId(UserBaseKey userBaseKey, int categoryId)
    {
        this.userId = userBaseKey.getUserId();
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AchievementCategoryId)) return false;

        AchievementCategoryId that = (AchievementCategoryId) o;

        if (categoryId != that.categoryId) return false;
        return userId == that.userId;
    }

    @Override
    public int hashCode()
    {
        int result = userId;
        result = 31 * result + categoryId;
        return result;
    }
}
