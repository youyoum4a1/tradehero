package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class HelpVideoListCacheRx extends BaseFetchDTOCacheRx<HelpVideoListKey, HelpVideoDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final HelpVideoCacheRx helpVideoCache;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoListCacheRx(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull HelpVideoCacheRx helpVideoCache)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.providerServiceWrapper = providerServiceWrapper;
        this.helpVideoCache = helpVideoCache;
    }
    //</editor-fold>

    @Override @NotNull public Observable<HelpVideoDTOList> fetch(@NotNull HelpVideoListKey key)
    {
        return providerServiceWrapper.getHelpVideosRx(key);
    }

    @Override public void onNext(@NotNull HelpVideoListKey key, @NotNull HelpVideoDTOList value)
    {
        super.onNext(key, value);
        helpVideoCache.onNext(value);
    }
}
