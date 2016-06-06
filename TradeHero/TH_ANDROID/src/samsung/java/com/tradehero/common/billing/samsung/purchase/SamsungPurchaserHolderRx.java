package com.androidth.general.common.billing.samsung.purchase;

import com.androidth.general.common.billing.purchase.BillingPurchaserHolderRx;
import com.androidth.general.common.billing.samsung.SamsungOrderId;
import com.androidth.general.common.billing.samsung.SamsungPurchase;
import com.androidth.general.common.billing.samsung.SamsungPurchaseOrder;
import com.androidth.general.common.billing.samsung.SamsungSKU;

public interface SamsungPurchaserHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BillingPurchaserHolderRx<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
}
