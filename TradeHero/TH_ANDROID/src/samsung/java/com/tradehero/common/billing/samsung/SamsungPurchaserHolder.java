package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BillingPurchaserHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungPurchaserHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
    extends BillingPurchaserHolder<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
}
