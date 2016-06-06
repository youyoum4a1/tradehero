package com.androidth.general.common.billing.purchase;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseResult;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.billing.PurchaseOrder;

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
