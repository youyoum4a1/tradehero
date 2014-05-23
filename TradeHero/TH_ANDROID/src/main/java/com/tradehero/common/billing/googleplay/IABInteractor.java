package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.request.UIIABBillingRequest;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;

public interface IABInteractor<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABActorType extends BillingLogicHolder<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                BillingRequestType,
                IABExceptionType>,
        BillingRequestType extends BillingRequest<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        UIBillingRequestType extends UIBillingRequest<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> & UIIABBillingRequest,
        IABExceptionType extends IABException>
    extends BillingInteractor<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType,
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
