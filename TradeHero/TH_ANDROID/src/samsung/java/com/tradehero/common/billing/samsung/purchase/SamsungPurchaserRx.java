package com.androidth.general.common.billing.samsung.purchase;

import com.androidth.general.common.billing.purchase.BillingPurchaserRx;
import com.androidth.general.common.billing.samsung.SamsungOrderId;
import com.androidth.general.common.billing.samsung.SamsungPurchase;
import com.androidth.general.common.billing.samsung.SamsungPurchaseOrder;
import com.androidth.general.common.billing.samsung.SamsungSKU;

public interface SamsungPurchaserRx<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BillingPurchaserRx<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
}
