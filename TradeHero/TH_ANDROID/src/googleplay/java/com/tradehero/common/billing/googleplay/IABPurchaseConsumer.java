package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.RequestCodeActor;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABPurchaseConsumer<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
        extends RequestCodeActor, IABServiceListenerHolder
{
    OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> getConsumptionFinishedListener();
    void setConsumptionFinishedListener(OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> consumptionListener);
    void consume(int requestCode, IABPurchaseType purchase);

    public static interface OnIABConsumptionFinishedListener<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
            IABExceptionType extends IABException>
    {
        void onPurchaseConsumed(int requestCode, IABPurchaseType purchase);
        void onPurchaseConsumeFailed(int requestCode, IABPurchaseType purchase, IABExceptionType exception);
    }
}
