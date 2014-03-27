package com.tradehero.th.persistence.billing.samsung;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:27 PM To change this template use File | Settings | File Templates.
 * */
@Singleton public class SamsungSKUListCache extends ProductIdentifierListCache<SamsungSKU, SamsungSKUListKey, SamsungSKUList>
{
    public static final int MAX_SIZE = 15;

    @Inject public SamsungSKUListCache()
    {
        super(MAX_SIZE);
    }

    @Override public SamsungSKUListKey getKeyForAll()
    {
        return SamsungSKUListKey.getAllKey();
    }
}
