package com.tradehero.th.persistence.games;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.games.ViralMiniGameDefDTOList;
import com.tradehero.th.api.games.ViralMiniGameDefListKey;
import com.tradehero.th.network.service.MiniGameServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ViralMiniGameDefListCache extends BaseFetchDTOCacheRx<ViralMiniGameDefListKey, ViralMiniGameDefDTOList>
{
    private static final int DEFAULT_MAX_VALUE = 1;
    private static final int DEFAULT_MAX_SUBJECT = 1;

    @NonNull protected final MiniGameServiceWrapper miniGameServiceWrapper;
    @NonNull protected final Lazy<ViralMiniGameDefCache> miniGameDefCache;

    //<editor-fold desc="Constructors">
    @Inject public ViralMiniGameDefListCache(
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull MiniGameServiceWrapper miniGameServiceWrapper,
            @NonNull Lazy<ViralMiniGameDefCache> miniGameDefCache)
    {
        super(DEFAULT_MAX_VALUE, DEFAULT_MAX_SUBJECT, DEFAULT_MAX_SUBJECT, dtoCacheUtilRx);
        this.miniGameServiceWrapper = miniGameServiceWrapper;
        this.miniGameDefCache = miniGameDefCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<ViralMiniGameDefDTOList> fetch(@NonNull ViralMiniGameDefListKey key)
    {
        return miniGameServiceWrapper.getAllViralGame();
    }

    @Override public void onNext(@NonNull ViralMiniGameDefListKey key, @NonNull ViralMiniGameDefDTOList value)
    {
        miniGameDefCache.get().onNext(value);
        super.onNext(key, value);
    }
}
