package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class AmazonSKUListRetrievedAsyncMilestone
        extends ProductIdentifierListRetrievedAsyncMilestone<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList,
        AmazonSKUListCache>
{
    @Inject Lazy<AmazonSKUListCache> AmazonskuListCache;

    public AmazonSKUListRetrievedAsyncMilestone(AmazonSKUListKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected AmazonSKUListCache getCache()
    {
        return AmazonskuListCache.get();
    }
}
