package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderListKey;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class ProviderCache extends StraightDTOCache<ProviderId, ProviderDTO>
{
    public static final String TAG = ProviderCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<ProviderListCache> providerListCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ProviderDTO fetch(ProviderId key) throws Throwable
    {
        // Just have the list cache download them all
        providerListCache.get().fetch(new ProviderListKey(ProviderListKey.ALL_PROVIDERS));
        // By then, the list cache has updated this cache
        return get(key);
    }

    @Override public ProviderDTO put(ProviderId key, ProviderDTO value)
    {
        if (value != null)
        {
            warrantSpecificKnowledgeFactory.add(key, value.getAssociatedOwnedPortfolioId(currentUserBaseKeyHolder.getCurrentUserBaseKey()));
        }
        return super.put(key, value);
    }

    public List<ProviderDTO> getOrFetch(List<ProviderId> providerIds) throws Throwable
    {
        if (providerIds == null)
        {
            return null;
        }

        List<ProviderDTO> providerDTOList = new ArrayList<>();
        for (ProviderId providerId : providerIds)
        {
            providerDTOList.add(getOrFetch(providerId, false));
        }
        return providerDTOList;
    }

    public List<ProviderDTO> get(List<ProviderId> providerIds)
    {
        if (providerIds == null)
        {
            return null;
        }

        List<ProviderDTO> fleshedValues = new ArrayList<>();

        for (ProviderId providerId: providerIds)
        {
            fleshedValues.add(get(providerId));
        }

        return fleshedValues;
    }

    public List<ProviderDTO> put(List<ProviderDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<ProviderDTO> previousValues = new ArrayList<>();

        for (ProviderDTO providerDTO: values)
        {
            previousValues.add(put(providerDTO.getProviderId(), providerDTO));
        }

        return previousValues;
    }
}
