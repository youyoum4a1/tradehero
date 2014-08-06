package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.listener.OnPaymentListener;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungPurchaser<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
        extends BillingPurchaser<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>,
        OnPaymentListener
{
}
