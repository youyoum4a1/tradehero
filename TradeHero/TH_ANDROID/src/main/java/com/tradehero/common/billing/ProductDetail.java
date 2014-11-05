package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;

import android.support.annotation.NonNull;

public interface ProductDetail<ProductIdentifierType extends ProductIdentifier>
        extends DTO
{
    @NonNull ProductIdentifierType getProductIdentifier();
}
