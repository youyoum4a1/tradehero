package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface ProductIdentifierFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        OnProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    OnProductIdentifierFetchedListenerType getProductIdentifierFetchedListener(int requestCode);
    boolean isUnusedRequestCode(int requestCode);
    void registerProductIdentifierFetchedListener(int requestCode, OnProductIdentifierFetchedListenerType productIdentifierFetchedListener);
    void unregisterProductIdentifierFetchedListener(int requestCode);
    void launchProductIdentifierFetchSequence(int requestCode);
    void onDestroy();
}
