package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;

import org.jetbrains.annotations.NotNull;

public interface ProductDetail<ProductIdentifierType extends ProductIdentifier>
        extends DTO
{
    @NotNull ProductIdentifierType getProductIdentifier();
}
