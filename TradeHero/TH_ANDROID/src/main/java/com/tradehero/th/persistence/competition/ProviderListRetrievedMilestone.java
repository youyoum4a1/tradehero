package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class ProviderListRetrievedMilestone extends DTORetrievedAsyncMilestone<ProviderListKey, ProviderIdList, ProviderListCache>
{
    @Inject ProviderListCache providerListCache;
    @Inject ProviderCache providerCache;

    @Inject public ProviderListRetrievedMilestone()
    {
        this(new ProviderListKey());
    }

    public ProviderListRetrievedMilestone(ProviderListKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected ProviderListCache getCache()
    {
        return providerListCache;
    }

    @Override public void launch()
    {
        launchOwn();
    }

    @Override public boolean isComplete()
    {
        return super.isComplete() && hasDTOs(providerListCache.get(key));
    }

    public boolean hasDTOs(ProviderIdList keyList)
    {
        if (keyList == null)
        {
            return false;
        }
        for (ProviderId id : keyList)
        {
            if (providerCache.get(id) == null)
            {
                return false;
            }
        }
        return true;
    }
}
