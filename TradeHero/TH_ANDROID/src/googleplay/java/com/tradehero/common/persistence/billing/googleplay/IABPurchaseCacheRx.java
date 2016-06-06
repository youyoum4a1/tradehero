package com.androidth.general.common.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductPurchaseCacheRx;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.persistence.DTOCacheUtilRx;

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
