package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Singleton public class ProviderListCache extends StraightDTOCache<ProviderListKey, ProviderIdList>
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

    @Override protected ProviderIdList fetch(@NotNull ProviderListKey key) throws Throwable
    {
        if (key.key.equals(ProviderListKey.ALL_PROVIDERS))
        {
            return putInternal(key, providerServiceWrapper.getProviders());
        }

        throw new IllegalArgumentException("Unknown ProviderListKey " + key);
    }

    @Contract("_, null -> null; _, !null -> !null") @Nullable
    protected ProviderIdList putInternal(@NotNull ProviderListKey key, @Nullable List<ProviderDTO> fleshedValues)
    {
        ProviderIdList providerIds = null;
        if (fleshedValues != null)
        {
            providerIds = new ProviderIdList();
            @NotNull ProviderId providerId;
            for (@NotNull ProviderDTO providerDTO: fleshedValues)
            {
                providerId = providerDTO.getProviderId();
                providerIds.add(providerId);
                providerCache.put(providerId, providerDTO);
            }
            put(key, providerIds);
        }
        return providerIds;
    }
}
