package com.androidth.general.common.billing.purchasefetch;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.billing.RequestCodeActor;
import rx.Observable;

public interface BillingPurchaseFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends RequestCodeActor
{
    @NonNull Observable<PurchaseFetchResult<ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType>> get();
}
