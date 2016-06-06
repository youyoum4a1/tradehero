package com.androidth.general.common.billing.purchasefetch;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.billing.RequestCodeHolder;
import rx.Observable;

public interface BillingPurchaseFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
    extends RequestCodeHolder
{
    @NonNull Observable<PurchaseFetchResult<ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> get(int requestCode);
}
