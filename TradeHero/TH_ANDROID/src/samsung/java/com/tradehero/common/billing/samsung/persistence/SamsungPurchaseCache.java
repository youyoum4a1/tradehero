package com.tradehero.common.billing.samsung.persistence;

import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import android.support.annotation.NonNull;

public class SamsungPurchaseCache<
            SamsungSKUType extends SamsungSKU,
            SamsungOrderIdType extends SamsungOrderId,
            SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends ProductPurchaseCache<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
    //<editor-fold desc="Constructors">
    public SamsungPurchaseCache(int maxSize, @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    @Override @NonNull public SamsungPurchaseType fetch(@NonNull SamsungOrderIdType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }
}
