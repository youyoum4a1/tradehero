package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AmazonSKUListCache extends ProductIdentifierListCache<AmazonSKU, AmazonSKUListKey, AmazonSKUList>
{
    public static final int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public AmazonSKUListCache()
    {
        super(MAX_SIZE);
    }
    //</editor-fold>

    @Override public AmazonSKUListKey getKeyForAll()
    {
        // TODO any better?
        return null;
    }
}
