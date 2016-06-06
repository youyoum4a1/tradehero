package com.androidth.general.common.billing.purchase;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.billing.PurchaseOrder;
import com.androidth.general.common.billing.RequestCodeHolder;
import rx.Observable;

public interface BillingPurchaserHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
    extends RequestCodeHolder
{
    @NonNull Observable<PurchaseResult<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType>> get(int requestCode,
            @NonNull PurchaseOrderType purchaseOrder);
}
