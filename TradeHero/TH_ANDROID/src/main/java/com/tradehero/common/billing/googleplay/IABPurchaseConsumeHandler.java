package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 4:57 PM To change this template use File | Settings | File Templates. */
public interface IABPurchaseConsumeHandler<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABOrderIdType, IABSKUType>,
        IABExceptionType extends IABException>
{
    void handlePurchaseConsumed(int requestCode, IABPurchaseType purchase);
    void handlePurchaseConsumeException(int requestCode, IABExceptionType exception);
}
