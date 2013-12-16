package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:40 PM To change this template use File | Settings | File Templates. */
public class IABSKUListRetrievedAsyncMilestone extends DTORetrievedAsyncMilestone<IABSKUListType, IABSKUList, IABSKUListCache>
{
    public static final String TAG = IABSKUListRetrievedAsyncMilestone.class.getSimpleName();

    @Inject Lazy<IABSKUListCache> iabskuListCache;

    public IABSKUListRetrievedAsyncMilestone(IABSKUListType key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected IABSKUListCache getCache()
    {
        return iabskuListCache.get();
    }

    @Override public void launch()
    {
        launchOwn();
    }
}
