package com.ayondo.academy.billing;

import com.android.internal.util.Predicate;
import com.tradehero.common.billing.ProductIdentifier;

public class THProductDetailDomainPredicate<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        implements Predicate<THProductDetailType>
{
    private final ProductIdentifierDomain domain;

    public THProductDetailDomainPredicate(ProductIdentifierDomain domain)
    {
        super();
        this.domain = domain;
    }

    @Override public boolean apply(THProductDetailType thProductDetail)
    {
        return thProductDetail != null && (thProductDetail.getDomain() == null ? domain == null : thProductDetail.getDomain().equals(domain));
    }
}
