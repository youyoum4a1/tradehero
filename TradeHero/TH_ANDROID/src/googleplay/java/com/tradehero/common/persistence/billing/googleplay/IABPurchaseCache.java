package com.tradehero.common.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;

import com.tradehero.common.persistence.DTOCacheUtilNew;
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
    public IABPurchaseCache(int maxSize,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    @Override @NotNull public IABPurchaseType fetch(@NotNull IABOrderIdType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }
}
