package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.th.persistence.billing.ProductIdentifierListRetrievedAsyncMilestone;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class IABSKUListRetrievedAsyncMilestone
        extends ProductIdentifierListRetrievedAsyncMilestone<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        IABSKUListCache>
{
    @Inject Lazy<IABSKUListCache> iabskuListCache;

    public IABSKUListRetrievedAsyncMilestone(IABSKUListKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected IABSKUListCache getCache()
    {
        return iabskuListCache.get();
    }
}
