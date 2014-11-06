package com.tradehero.common.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;

public class IABPurchaseCacheRx<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends ProductPurchaseCacheRx<
                    IABSKUType,
                    IABOrderIdType,
                    IABPurchaseType>
{
    //<editor-fold desc="Constructors">
    public IABPurchaseCacheRx(int maxSize,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>
}
