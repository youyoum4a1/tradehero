package com.tradehero.common.billing;

import com.tradehero.th.billing.googleplay.THSKUDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActor<ProductIdentifierType extends ProductIdentifier,
                        ProductDetailsType extends ProductDetails<ProductIdentifierType>>
{
    void launchPurchaseSequence(ProductDetailsType productDetails);
    void launchPurchaseSequence(ProductDetailsType productDetails, Object extraData);
}
