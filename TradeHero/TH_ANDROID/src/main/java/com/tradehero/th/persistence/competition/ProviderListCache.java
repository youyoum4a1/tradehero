package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class ProviderListCache extends StraightCutDTOCacheNew<ProviderListKey, ProviderDTOList, ProviderIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final ProviderCache providerCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderListCache(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull ProviderCache providerCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.providerServiceWrapper = providerServiceWrapper;
        this.providerCache = providerCache;
    }
    //</editor-fold>

    @Override @NotNull public ProviderDTOList fetch(@NotNull ProviderListKey key) throws Throwable
    {
        if (key.key.equals(ProviderListKey.ALL_PROVIDERS))
        {
            return providerServiceWrapper.getProviders();
        }

        throw new IllegalArgumentException("Unknown ProviderListKey " + key);
    }

    @NotNull @Override protected ProviderIdList cutValue(@NotNull ProviderListKey key, @NotNull ProviderDTOList value)
    {
        providerCache.put(value);
        return value.createKeys();
    }

    @Nullable @Override protected ProviderDTOList inflateValue(@NotNull ProviderListKey key, @Nullable ProviderIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        @NotNull ProviderDTOList value = providerCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
