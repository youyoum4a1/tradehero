package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.listener.OnGetInboxListener;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungPurchaseFetcher<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
        extends BillingPurchaseFetcher<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>,
        OnGetInboxListener
{
}
