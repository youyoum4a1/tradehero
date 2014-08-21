package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.request.UISamsungRequest;

public interface SamsungInteractor<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<
                SamsungSKUType,
                SamsungOrderIdType>,
        SamsungActorType extends BillingLogicHolder<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                BillingRequestType,
                SamsungExceptionType>,
        BillingRequestType extends BillingRequest<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungExceptionType>,
        UIBillingRequestType extends UIBillingRequest<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungExceptionType> & UISamsungRequest,
        SamsungExceptionType extends SamsungException>
        extends BillingInteractor<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungProductDetailType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungActorType,
        BillingRequestType,
        UIBillingRequestType,
        SamsungExceptionType>
{
}
