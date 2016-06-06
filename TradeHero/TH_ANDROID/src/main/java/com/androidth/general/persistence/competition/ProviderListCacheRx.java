package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ProviderListCacheRx extends BaseFetchDTOCacheRx<ProviderListKey, ProviderDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final ProviderServiceWrapper providerServiceWrapper;
    @NonNull private final ProviderCacheRx providerCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderListCacheRx(
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull ProviderCacheRx providerCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
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

    @Nullable @Override protected ProviderDTOList putValue(@NonNull ProviderListKey key, @NonNull ProviderDTOList value)
    {
        providerCache.onNext(value);
        return super.putValue(key, value);
    }
}
