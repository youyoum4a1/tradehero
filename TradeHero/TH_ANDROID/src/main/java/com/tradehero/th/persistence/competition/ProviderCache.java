package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderKey;
import com.tradehero.th.api.competition.ProviderListKey;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class ProviderCache extends StraightDTOCache<Integer, ProviderKey, ProviderDTO>
{
    public static final String TAG = ProviderCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<ProviderListCache> providerListCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ProviderDTO fetch(ProviderKey key)
    {
        // Just have the list cache download them all
        providerListCache.get().fetch(new ProviderListKey(ProviderListKey.ALL_PROVIDERS));
        // By then, the list cache has updated this cache
        return get(key);
    }

    public List<ProviderDTO> getOrFetch(List<ProviderKey> providerKeys)
    {
        if (providerKeys == null)
        {
            return null;
        }

        List<ProviderDTO> providerDTOList = new ArrayList<>();
        for (ProviderKey providerKey: providerKeys)
        {
            providerDTOList.add(getOrFetch(providerKey, false));
        }
        return providerDTOList;
    }
}
