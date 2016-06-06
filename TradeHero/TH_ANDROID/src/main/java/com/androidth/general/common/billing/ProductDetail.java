package com.androidth.general.common.billing;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;

public interface ProductDetail<ProductIdentifierType extends ProductIdentifier>
        extends DTO
{
    @NonNull ProductIdentifierType getProductIdentifier();
}
