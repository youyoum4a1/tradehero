package com.tradehero.th.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.persistence.SamsungGroupItemCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class THSamsungGroupItemCache
        extends SamsungGroupItemCache<SamsungSKU, SamsungSKUList>
{
    public static int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungGroupItemCache()
    {
        super(MAX_SIZE);
    }
    //</editor-fold>

    @Override protected SamsungSKUList createEmptyValue()
    {
        return new SamsungSKUList();
    }
}
