package com.tradehero.common.billing;


public interface ProductDetailTuner<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
{
    void fineTune(ProductDetailType productDetails);
}
