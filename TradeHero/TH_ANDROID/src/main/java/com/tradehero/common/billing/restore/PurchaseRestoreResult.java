package com.tradehero.common.billing.restore;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;

public class PurchaseRestoreResult<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseResult
{
    @NonNull public final ProductPurchaseType purchase;

    //<editor-fold desc="Constructors">
    public PurchaseRestoreResult(int requestCode,
            @NonNull ProductPurchaseType purchase)
    {
        super(requestCode);
        this.purchase = purchase;
    }
    //</editor-fold>
}
