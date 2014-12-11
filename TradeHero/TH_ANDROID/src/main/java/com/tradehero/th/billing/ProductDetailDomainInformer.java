package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import java.util.List;

public interface ProductDetailDomainInformer<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
{
    List<ProductDetailType> getDetailsOfDomain(ProductIdentifierDomain domain);
}