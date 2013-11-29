package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:20 PM To change this template use File | Settings | File Templates. */
public interface ProductDetailTuner<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
{
    void fineTune(ProductDetailType productDetails);
}
