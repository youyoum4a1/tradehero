package com.tradehero.common.billing.samsung.purchase;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.purchase.BaseBillingPurchaserHolderRx;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungPurchaseOrder;
import com.tradehero.common.billing.samsung.SamsungSKU;

abstract public class BaseSamsungPurchaserHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BaseBillingPurchaserHolderRx<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType>
        implements SamsungPurchaserHolderRx<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaserHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override
    abstract protected SamsungPurchaserRx<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType> createPurchaser(
            int requestCode,
            @NonNull SamsungPurchaseOrderType purchaseOrder);
}
