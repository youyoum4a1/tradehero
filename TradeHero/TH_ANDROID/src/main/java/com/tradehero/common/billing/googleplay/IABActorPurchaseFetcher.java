package com.tradehero.common.billing.googleplay;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorPurchaseFetcher<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseFetchedListenerType extends IABPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType>>
{
    void forgetRequestCode(int requestCode);
    boolean isBillingAvailable();

    IABPurchaseFetchedListenerType getPurchaseFetchedListener(int requestCode);
    int registerPurchaseFetchedListener(IABPurchaseFetchedListenerType purchaseFetchedListener);
    void launchFetchPurchaseSequence(int requestCode);
}
