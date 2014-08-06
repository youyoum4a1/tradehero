package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/**
 * Created by xavier on 3/27/14.
 */
public interface SamsungPurchaseFetcherHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
    extends BillingPurchaseFetcherHolder<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
}
