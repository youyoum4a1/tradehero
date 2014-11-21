package com.tradehero.common.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends DTO
{
    @NonNull OrderIdType getOrderId();
    @NonNull ProductIdentifierType getProductIdentifier();
}
