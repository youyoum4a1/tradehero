package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorPurchaseConsumer<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABConsumeFinishedListenerType extends IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABExceptionType extends IABException>
{
    void forgetRequestCode(int requestCode);
    boolean isBillingAvailable();

    IABConsumeFinishedListenerType getConsumeFinishedListener(int requestCode);
    int registerConsumeFinishedListener(IABConsumeFinishedListenerType purchaseConsumeHandler);
    void launchConsumeSequence(int requestCode, IABPurchaseType purchase);
}
