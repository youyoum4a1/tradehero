package com.androidth.general.common.billing.restore;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseResult;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;

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
