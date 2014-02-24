package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABPurchaseFetcherHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABExceptionType extends IABException>
{
    IABPurchaseFetchedListenerType getPurchaseFetchedListener(int requestCode);
    int registerPurchaseFetchedListener(IABPurchaseFetchedListenerType purchaseFetchedListener);
    void unregisterPurchaseFetchedListener(int requestCode);
    void launchFetchPurchaseSequence(int requestCode);
}
