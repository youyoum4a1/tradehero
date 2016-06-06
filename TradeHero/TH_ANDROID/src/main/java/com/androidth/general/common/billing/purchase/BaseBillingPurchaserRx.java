package com.androidth.general.common.billing.purchase;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeActor;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.billing.PurchaseOrder;

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
