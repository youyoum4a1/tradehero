package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:27 PM To change this template use File | Settings | File Templates.
 * */
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
