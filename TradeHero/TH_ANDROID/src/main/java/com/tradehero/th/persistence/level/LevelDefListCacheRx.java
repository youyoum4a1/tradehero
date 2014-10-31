package com.tradehero.th.persistence.level;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class LevelDefListCacheRx extends BaseFetchDTOCacheRx<LevelDefListId, LevelDefDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NotNull private final AchievementServiceWrapper userServiceWrapper;
    @NotNull private final LevelDefCacheRx levelDefCache;

    //<editor-fold desc="Constructors">
    @Inject public LevelDefListCacheRx(
            @NotNull AchievementServiceWrapper userServiceWrapper,
            @NotNull LevelDefCacheRx levelDefCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, DEFAULT_MAX_SIZE, DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.levelDefCache = levelDefCache;
        get(new LevelDefListId());
    }
    //</editor-fold>

    @NotNull @Override public Observable<LevelDefDTOList> fetch(@NotNull LevelDefListId key)
    {
        return userServiceWrapper.getLevelDefsRx();
    }

    @Override public void onNext(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
    {
        value.sort();
        levelDefCache.onNext(value);
        super.onNext(key, value);
    }
}
