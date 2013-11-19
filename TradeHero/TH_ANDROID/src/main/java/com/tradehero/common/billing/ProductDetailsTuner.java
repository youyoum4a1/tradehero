package com.tradehero.common.billing;

import com.tradehero.common.billing.ProductDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:20 PM To change this template use File | Settings | File Templates. */
public interface ProductDetailsTuner<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetails<ProductIdentifierType>>
{
    void fineTune(ProductDetailsType productDetails);
}
