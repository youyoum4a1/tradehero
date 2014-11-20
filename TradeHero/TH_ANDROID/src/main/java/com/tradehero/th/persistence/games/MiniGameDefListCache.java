package com.tradehero.th.persistence.games;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.games.MiniGameDefDTOList;
import com.tradehero.th.api.games.MiniGameDefListKey;
import com.tradehero.th.network.service.MiniGameServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class MiniGameDefListCache extends BaseFetchDTOCacheRx<MiniGameDefListKey, MiniGameDefDTOList>
{
    private static final int DEFAULT_MAX_VALUE = 1;
    private static final int DEFAULT_MAX_SUBJECT = 1;

    @NonNull protected final MiniGameServiceWrapper miniGameServiceWrapper;
    @NonNull protected final Lazy<MiniGameDefCache> miniGameDefCache;

    //<editor-fold desc="Constructors">
    @Inject public MiniGameDefListCache(
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull MiniGameServiceWrapper miniGameServiceWrapper,
            @NonNull Lazy<MiniGameDefCache> miniGameDefCache)
    {
        super(DEFAULT_MAX_VALUE, DEFAULT_MAX_SUBJECT, DEFAULT_MAX_SUBJECT, dtoCacheUtilRx);
        this.miniGameServiceWrapper = miniGameServiceWrapper;
        this.miniGameDefCache = miniGameDefCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<MiniGameDefDTOList> fetch(@NonNull MiniGameDefListKey key)
    {
        return miniGameServiceWrapper.getAllGames();
    }

    @Override public void onNext(@NonNull MiniGameDefListKey key, @NonNull MiniGameDefDTOList value)
    {
        miniGameDefCache.get().onNext(value);
        super.onNext(key, value);
    }
}
