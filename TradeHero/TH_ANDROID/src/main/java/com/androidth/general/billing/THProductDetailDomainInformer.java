package com.androidth.general.billing;

import com.androidth.general.common.billing.ProductIdentifier;
import java.util.List;

public interface THProductDetailDomainInformer<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
{
    List<THProductDetailType> getDetailsOfDomain(ProductIdentifierDomain domain);
}
