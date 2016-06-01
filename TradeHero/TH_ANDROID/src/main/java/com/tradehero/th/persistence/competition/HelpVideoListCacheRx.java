package com.ayondo.academy.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.competition.HelpVideoDTOList;
import com.ayondo.academy.api.competition.key.HelpVideoListKey;
import com.ayondo.academy.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class HelpVideoListCacheRx extends BaseFetchDTOCacheRx<HelpVideoListKey, HelpVideoDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final ProviderServiceWrapper providerServiceWrapper;
    @NonNull private final HelpVideoCacheRx helpVideoCache;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoListCacheRx(
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull HelpVideoCacheRx helpVideoCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
        this.helpVideoCache = helpVideoCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<HelpVideoDTOList> fetch(@NonNull HelpVideoListKey key)
    {
        return providerServiceWrapper.getHelpVideosRx(key);
    }

    @Override public void onNext(@NonNull HelpVideoListKey key, @NonNull HelpVideoDTOList value)
    {
        super.onNext(key, value);
        helpVideoCache.onNext(value);
    }
}
