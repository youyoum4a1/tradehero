package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import java.util.List;

public interface THProductDetailDomainInformer<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
{
    List<THProductDetailType> getDetailsOfDomain(ProductIdentifierDomain domain);
}
