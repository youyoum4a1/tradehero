package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;

/**
 * Created by julien on 4/11/13
 */
public interface ProductDetail<ProductIdentifierType extends ProductIdentifier>
        extends DTO
{
    ProductIdentifierType getProductIdentifier();
}
