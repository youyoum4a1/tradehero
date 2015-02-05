package com.tradehero.common.billing.purchasefetch;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.RequestCodeHolder;
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
