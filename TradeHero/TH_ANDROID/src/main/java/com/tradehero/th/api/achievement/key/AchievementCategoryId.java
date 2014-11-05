package com.tradehero.th.api.achievement.key;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import android.support.annotation.NonNull;

public class AchievementCategoryId implements DTOKey
{
    @NonNull public final Integer userId;
    @NonNull public final Integer categoryId;

    //<editor-fold desc="Constructors">
    public AchievementCategoryId(@NonNull UserBaseKey userBaseKey, int categoryId)
    {
        this.userId = userBaseKey.getUserId();
        this.categoryId = categoryId;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof AchievementCategoryId))
        {
            return false;
        }

        AchievementCategoryId that = (AchievementCategoryId) o;

        return categoryId.equals(that.categoryId)
            && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode() ^ categoryId.hashCode();
    }
}
