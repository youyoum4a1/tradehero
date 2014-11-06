package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;

public class AmazonPurchaseCacheRx<
            AmazonSKUType extends AmazonSKU,
            AmazonOrderIdType extends AmazonOrderId,
            AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends ProductPurchaseCacheRx<
                    AmazonSKUType,
                    AmazonOrderIdType,
                    AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public AmazonPurchaseCacheRx(int maxSize,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>
}
