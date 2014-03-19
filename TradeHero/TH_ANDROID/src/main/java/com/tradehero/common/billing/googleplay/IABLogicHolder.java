package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.BillingRequest;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABLogicHolder<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        BillingRequestType extends BillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends
        BillingLogicHolder<
                        IABSKUType,
                        IABProductDetailType,
                        IABPurchaseOrderType,
                        IABOrderIdType,
                        IABPurchaseType,
                        BillingRequestType,
                        IABExceptionType>
{
}
