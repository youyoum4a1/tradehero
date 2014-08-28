package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductPurchaseCache;
import org.jetbrains.annotations.NotNull;

public class IABPurchaseCache<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends ProductPurchaseCache<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType>
{
    //<editor-fold desc="Constructors">
    public IABPurchaseCache(int maxSize)
    {
        super(maxSize);
    }
    //</editor-fold>

    @Override @NotNull public IABPurchaseType fetch(@NotNull IABOrderIdType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }
}
