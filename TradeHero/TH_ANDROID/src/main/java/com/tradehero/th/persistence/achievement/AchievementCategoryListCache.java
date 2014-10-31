package com.tradehero.th.persistence.achievement;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.key.AchievementCategoryIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache
public class AchievementCategoryListCache extends StraightCutDTOCacheNew<UserBaseKey, AchievementCategoryDTOList, AchievementCategoryIdList>
{
    private static final int DEFAULT_SIZE = 1;
    private final AchievementServiceWrapper achievementServiceWrapper;
    private final AchievementCategoryCache achievementCategoryCache;

    //<editor-fold desc="Constructors">
    @Inject public AchievementCategoryListCache(
            @NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull AchievementCategoryCache achievementCategoryCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_SIZE, dtoCacheUtil);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.achievementCategoryCache = achievementCategoryCache;
    }
    //</editor-fold>

    @NotNull @Override public AchievementCategoryDTOList fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return achievementServiceWrapper.getAchievementCategories(key);
    }

    @NotNull @Override protected AchievementCategoryIdList cutValue(@NotNull UserBaseKey key, @NotNull AchievementCategoryDTOList value)
    {
        achievementCategoryCache.put(key, value);
        return value.createKeys(key);
    }

    @Nullable @Override protected AchievementCategoryDTOList inflateValue(@NotNull UserBaseKey key, @Nullable AchievementCategoryIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        AchievementCategoryDTOList value = achievementCategoryCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
