package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface SamsungLogicHolder<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        BillingRequestType extends BillingRequest<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends
        BillingLogicHolder<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                BillingRequestType,
                SamsungExceptionType>
{
}
