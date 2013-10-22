package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.ProviderListKey;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.ProviderService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class ProviderListCache extends StraightDTOCache<ProviderListKey, ProviderIdList>
{
    public static final String TAG = ProviderListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected Lazy<ProviderService> providerService;
    @Inject protected Lazy<ProviderCache> providerCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ProviderIdList fetch(ProviderListKey key)
    {
        THLog.d(TAG, "fetch " + key);
        try
        {

            if (key.key == ProviderListKey.ALL_PROVIDERS)
            {
                return putInternal(key, providerService.get().getProviders());
            }

            throw new IllegalArgumentException("Unknown ProviderListKey " + key);
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return null;
    }

    protected ProviderIdList putInternal(ProviderListKey key, List<ProviderDTO> fleshedValues)
    {
        ProviderIdList providerIds = null;
        if (fleshedValues != null)
        {
            providerIds = new ProviderIdList();
            ProviderId providerId;
            for(ProviderDTO providerDTO: fleshedValues)
            {
                providerId = providerDTO.getProviderId();
                providerIds.add(providerId);
                providerCache.get().put(providerId, providerDTO);
            }
            put(key, providerIds);
        }
        return providerIds;
    }
}
