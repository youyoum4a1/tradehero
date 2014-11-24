package com.tradehero.common.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

public interface ProductDetail<ProductIdentifierType extends ProductIdentifier>
        extends DTO
{
    @NonNull ProductIdentifierType getProductIdentifier();
}
