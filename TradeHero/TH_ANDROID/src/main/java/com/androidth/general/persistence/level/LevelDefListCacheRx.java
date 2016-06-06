package com.androidth.general.persistence.level;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.level.LevelDefDTOList;
import com.androidth.general.api.level.key.LevelDefListId;
import com.androidth.general.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LevelDefListCacheRx extends BaseFetchDTOCacheRx<LevelDefListId, LevelDefDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NonNull private final AchievementServiceWrapper userServiceWrapper;
    @NonNull private final LevelDefCacheRx levelDefCache;

    //<editor-fold desc="Constructors">
    @Inject public LevelDefListCacheRx(
            @NonNull AchievementServiceWrapper userServiceWrapper,
            @NonNull LevelDefCacheRx levelDefCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.levelDefCache = levelDefCache;
        getOne(new LevelDefListId());
    }
    //</editor-fold>

    @NonNull @Override public Observable<LevelDefDTOList> fetch(@NonNull LevelDefListId key)
    {
        return userServiceWrapper.getLevelDefsRx();
    }

    @Override public void onNext(@NonNull LevelDefListId key, @NonNull LevelDefDTOList value)
    {
        value.sort();
        levelDefCache.onNext(value);
        super.onNext(key, value);
    }
}
