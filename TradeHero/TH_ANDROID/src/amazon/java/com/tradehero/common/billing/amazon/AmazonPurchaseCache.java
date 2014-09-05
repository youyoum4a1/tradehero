package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.ProductPurchaseCache;
import org.jetbrains.annotations.NotNull;

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
    public AmazonPurchaseCache(int maxSize)
    {
        super(maxSize);
    }
    //</editor-fold>

    @Override @NotNull public AmazonPurchaseType fetch(@NotNull AmazonOrderIdType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }
}
