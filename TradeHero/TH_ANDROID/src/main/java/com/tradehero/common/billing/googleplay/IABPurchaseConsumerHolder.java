package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.RequestCodeHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABPurchaseConsumerHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
    extends RequestCodeHolder
{
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
