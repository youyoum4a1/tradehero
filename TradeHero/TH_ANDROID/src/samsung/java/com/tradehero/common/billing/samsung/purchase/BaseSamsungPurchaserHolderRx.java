package com.androidth.general.common.billing.samsung.purchase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.purchase.BaseBillingPurchaserHolderRx;
import com.androidth.general.common.billing.samsung.SamsungOrderId;
import com.androidth.general.common.billing.samsung.SamsungPurchase;
import com.androidth.general.common.billing.samsung.SamsungPurchaseOrder;
import com.androidth.general.common.billing.samsung.SamsungSKU;

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

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
