package com.tradehero.common.billing.purchase;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;

public class PurchaseResult<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseResult
{
    @NonNull public PurchaseOrderType order;
    @NonNull public ProductPurchaseType purchase;

    //<editor-fold desc="Constructors">
    public PurchaseResult(int requestCode,
            @NonNull PurchaseOrderType order,
            @NonNull ProductPurchaseType purchase)
    {
        super(requestCode);
        this.order = order;
        this.purchase = purchase;
    }
    //</editor-fold>
}
