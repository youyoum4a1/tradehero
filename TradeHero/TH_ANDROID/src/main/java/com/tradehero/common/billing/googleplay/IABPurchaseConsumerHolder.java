package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABPurchaseConsumerHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType> getConsumptionFinishedListener(int requestCode);
    void registerConsumptionFinishedListener(int requestCode, IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType> purchaseConsumeHandler);
    void launchConsumeSequence(int requestCode, IABPurchaseType purchase);
    void onDestroy();
}
