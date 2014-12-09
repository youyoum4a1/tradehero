package com.tradehero.common.billing.restore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;

public class PurchaseRestoreResultWithError<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends PurchaseRestoreResult<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType>
{
    @Nullable public final Throwable throwable;

    //<editor-fold desc="Constructors">
    public PurchaseRestoreResultWithError(
            int requestCode,
            @NonNull ProductPurchaseType purchase,
            @Nullable Throwable throwable)
    {
        super(requestCode, purchase);
        this.throwable = throwable;
    }
    //</editor-fold>
}
