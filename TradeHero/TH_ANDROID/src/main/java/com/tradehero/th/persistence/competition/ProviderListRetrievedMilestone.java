package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:13 PM To change this template use File | Settings | File Templates. */
public class ProviderListRetrievedMilestone extends DTORetrievedAsyncMilestone<ProviderListKey, ProviderIdList, ProviderListCache>
{
    public static final String TAG = ProviderListRetrievedMilestone.class.getSimpleName();

    @Inject ProviderListCache providerListCache;

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
}
