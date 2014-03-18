package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.request.UIIABBillingRequest;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABInteractor<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABActorType extends BillingLogicHolder<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                BillingRequestType,
                IABExceptionType>,
        BillingRequestType extends BillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        UIBillingRequestType extends UIBillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> & UIIABBillingRequest,
        IABExceptionType extends IABException>
    extends BillingInteractor<
            IABSKUType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            IABActorType,
            BillingRequestType,
            UIBillingRequestType,
            IABExceptionType>
{
}
