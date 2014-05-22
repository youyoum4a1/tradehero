package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;

public interface ProductDetail<ProductIdentifierType extends ProductIdentifier>
        extends DTO
{
    ProductIdentifierType getProductIdentifier();
}
