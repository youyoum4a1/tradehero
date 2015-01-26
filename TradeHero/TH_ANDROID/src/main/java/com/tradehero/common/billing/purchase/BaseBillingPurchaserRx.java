package com.tradehero.common.billing.purchase;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeActor;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;

abstract public class BaseBillingPurchaserRx<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseRequestCodeActor
        implements BillingPurchaserRx<
        ProductIdentifierType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType>
{
    @NonNull private final PurchaseOrderType purchaseOrder;

    //<editor-fold desc="Constructors">
    protected BaseBillingPurchaserRx(int requestCode, @NonNull PurchaseOrderType purchaseOrder)
    {
        super(requestCode);
        this.purchaseOrder = purchaseOrder;
    }
    //</editor-fold>

    @NonNull @Override public PurchaseOrderType getPurchaseOrder()
    {
        return purchaseOrder;
    }
}
