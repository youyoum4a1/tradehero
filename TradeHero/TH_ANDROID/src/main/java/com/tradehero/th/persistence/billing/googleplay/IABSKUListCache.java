package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class IABSKUListCache extends ProductIdentifierListCache<IABSKU, IABSKUListKey, IABSKUList>
{
    public static final int MAX_SIZE = 5;

    @Inject public IABSKUListCache()
    {
        super(MAX_SIZE);
    }

    @Override public IABSKUListKey getKeyForAll()
    {
        return IABSKUListKey.getAll();
    }
}
