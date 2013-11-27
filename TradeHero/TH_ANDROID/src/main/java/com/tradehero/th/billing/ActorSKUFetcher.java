package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface ActorSKUFetcher<
        ProductIdentifierType extends ProductIdentifier,
        OnSKUFetchedListenerType extends SKUFetcher.OnSKUFetchedListener<ProductIdentifierType>>
{
    OnSKUFetchedListenerType getSkuFetchedListener(int requestCode);
    int registerSkuFetchedListener(OnSKUFetchedListenerType skuFetchedListener);
    void launchSkuFetchSequence(int requestCode);
}
