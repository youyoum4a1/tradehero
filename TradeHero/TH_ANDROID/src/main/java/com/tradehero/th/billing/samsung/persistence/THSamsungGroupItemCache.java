package com.tradehero.th.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.persistence.SamsungGroupItemCache;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 4/1/14.
 */
@Singleton public class THSamsungGroupItemCache
        extends SamsungGroupItemCache<SamsungSKU, SamsungSKUList>
{
    public static int MAX_SIZE = 5;

    @Inject public THSamsungGroupItemCache()
    {
        super(MAX_SIZE);
    }

    @Override protected SamsungSKUList createEmptyValue()
    {
        return new SamsungSKUList();
    }
}
