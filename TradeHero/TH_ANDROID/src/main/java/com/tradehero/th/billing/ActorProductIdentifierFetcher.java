package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface ActorProductIdentifierFetcher<
        ProductIdentifierType extends ProductIdentifier,
        OnSKUFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, IABExceptionType>,
        IABExceptionType extends IABException>
{
    OnSKUFetchedListenerType getSkuFetchedListener(int requestCode);
    int registerSkuFetchedListener(OnSKUFetchedListenerType skuFetchedListener);
    void launchSkuFetchSequence(int requestCode);
}
