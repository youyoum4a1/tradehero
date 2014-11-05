package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import android.support.annotation.NonNull;

public class AmazonPurchaseCache<
            AmazonSKUType extends AmazonSKU,
            AmazonOrderIdType extends AmazonOrderId,
            AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends ProductPurchaseCache<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public AmazonPurchaseCache(int maxSize,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    @Override @NonNull public AmazonPurchaseType fetch(@NonNull AmazonOrderIdType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }
}
