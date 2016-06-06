package com.androidth.general.common.billing.purchase;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeHolder;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.billing.PurchaseOrder;
import rx.Observable;

abstract public class BaseBillingPurchaserHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseRequestCodeHolder<BillingPurchaserRx<
        ProductIdentifierType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType>>
        implements BillingPurchaserHolderRx<
        ProductIdentifierType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseBillingPurchaserHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseResult<ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> get(int requestCode,
            @NonNull PurchaseOrderType purchaseOrder)
    {
        BillingPurchaserRx<ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType> purchaser = actors.get(requestCode);
        if (purchaser == null)
        {
            purchaser = createPurchaser(requestCode, purchaseOrder);
            actors.put(requestCode, purchaser);
        }
        return purchaser.get();
    }

    @NonNull abstract protected BillingPurchaserRx<ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType> createPurchaser(
            int requestCode,
            @NonNull PurchaseOrderType purchaseOrder);
}
