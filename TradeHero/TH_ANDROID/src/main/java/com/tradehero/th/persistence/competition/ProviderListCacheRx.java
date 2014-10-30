package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class ProviderListCacheRx extends BaseFetchDTOCacheRx<ProviderListKey, ProviderDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final ProviderCacheRx providerCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderListCacheRx(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull ProviderCacheRx providerCache)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.providerServiceWrapper = providerServiceWrapper;
        this.providerCache = providerCache;
    }
    //</editor-fold>

    @Override @NotNull public Observable<ProviderDTOList> fetch(@NotNull ProviderListKey key)
    {
        if (key.key.equals(ProviderListKey.ALL_PROVIDERS))
        {
            return providerServiceWrapper.getProvidersRx();
        }

        throw new IllegalArgumentException("Unknown ProviderListKey " + key);
    }

    @Override public void onNext(@NotNull ProviderListKey key, @NotNull ProviderDTOList value)
    {
        super.onNext(key, value);
        providerCache.onNext(value);
    }
}
