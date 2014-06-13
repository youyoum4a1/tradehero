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
import timber.log.Timber;

@Singleton public class ProviderListCache extends StraightDTOCache<ProviderListKey, ProviderIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected ProviderServiceWrapper providerServiceWrapper;
    @Inject protected ProviderCache providerCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ProviderIdList fetch(ProviderListKey key) throws Throwable
    {
        Timber.d("fetch %s", key);
        if (key.key.equals(ProviderListKey.ALL_PROVIDERS))
        {
            return putInternal(key, providerServiceWrapper.getProviders());
        }

        throw new IllegalArgumentException("Unknown ProviderListKey " + key);
    }

    protected ProviderIdList putInternal(ProviderListKey key, List<ProviderDTO> fleshedValues)
    {
        ProviderIdList providerIds = null;
        if (fleshedValues != null)
        {
            providerIds = new ProviderIdList();
            ProviderId providerId;
            for (ProviderDTO providerDTO: fleshedValues)
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
