package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.request.BillingRequest;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABLogicHolder<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        BillingRequestType extends BillingRequest<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends
        BillingLogicHolder<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                BillingRequestType,
                IABExceptionType>,
        IABPurchaseConsumerHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>
{
    IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> getConsumptionFinishedListener(int requestCode);
    void registerConsumptionFinishedListener(int requestCode,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> consumptionFinishedListener);
    void launchConsumeSequence(int requestCode, IABPurchaseType purchase);
}
