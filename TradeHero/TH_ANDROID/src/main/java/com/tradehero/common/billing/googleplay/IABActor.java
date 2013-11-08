package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.ProductIdentifier;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActor<ProductIdentifierType extends ProductIdentifier,
                        ProductDetailsType extends ProductDetails<ProductIdentifierType>>
{
    void launchPurchaseSequence(ProductDetailsType productDetails);
    void launchPurchaseSequence(ProductDetailsType productDetails, Object extraData);
}
