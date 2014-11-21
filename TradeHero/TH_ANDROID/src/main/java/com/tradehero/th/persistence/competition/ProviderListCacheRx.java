package com.tradehero.th.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ProviderListCacheRx extends BaseFetchDTOCacheRx<ProviderListKey, ProviderDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NonNull private final ProviderServiceWrapper providerServiceWrapper;
    @NonNull private final ProviderCacheRx providerCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderListCacheRx(
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull ProviderCacheRx providerCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
        this.providerCache = providerCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<ProviderDTOList> fetch(@NonNull ProviderListKey key)
    {
        if (key.key.equals(ProviderListKey.ALL_PROVIDERS))
        {
            return providerServiceWrapper.getProvidersRx();
        }

        throw new IllegalArgumentException("Unknown ProviderListKey " + key);
    }

    @Override public void onNext(@NonNull ProviderListKey key, @NonNull ProviderDTOList value)
    {
        super.onNext(key, value);
        providerCache.onNext(value);
    }
}
