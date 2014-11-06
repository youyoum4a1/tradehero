package com.tradehero.common.billing.samsung.persistence;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;

public class SamsungPurchaseCacheRx<
            SamsungSKUType extends SamsungSKU,
            SamsungOrderIdType extends SamsungOrderId,
            SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends ProductPurchaseCacheRx<
                SamsungSKUType,
                SamsungOrderIdType,
                SamsungPurchaseType>
{
    //<editor-fold desc="Constructors">
    public SamsungPurchaseCacheRx(int maxSize, @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>
}
