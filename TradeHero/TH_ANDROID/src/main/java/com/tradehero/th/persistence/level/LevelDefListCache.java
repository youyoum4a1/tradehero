package com.tradehero.th.persistence.level;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.LevelDefIdList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class LevelDefListCache extends StraightCutDTOCacheNew<LevelDefListId, LevelDefDTOList, LevelDefIdList>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NotNull private final AchievementServiceWrapper userServiceWrapper;
    @NotNull private final LevelDefCache levelDefCache;

    @Inject public LevelDefListCache(@NotNull AchievementServiceWrapper userServiceWrapper, @NotNull LevelDefCache levelDefCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.levelDefCache = levelDefCache;
    }

    @NotNull @Override protected LevelDefIdList cutValue(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
    {
        levelDefCache.put(value);
        return new LevelDefIdList(value);
    }

    @Nullable @Override protected LevelDefDTOList inflateValue(@NotNull LevelDefListId key, @Nullable LevelDefIdList cutValue)
    {
        if(cutValue == null)
        {
            return null;
        }
        LevelDefDTOList values = levelDefCache.get(cutValue);
        if(values.hasNullItem())
        {
            return null;
        }
        return values;
    }

    @NotNull @Override public LevelDefDTOList fetch(@NotNull LevelDefListId key) throws Throwable
    {
        return userServiceWrapper.getLevelDefs();
    }
}
