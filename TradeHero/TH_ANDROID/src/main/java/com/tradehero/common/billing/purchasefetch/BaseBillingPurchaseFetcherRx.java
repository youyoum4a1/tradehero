package com.tradehero.common.billing.purchasefetch;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeReplayActor;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import rx.Observable;

abstract public class BaseBillingPurchaseFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseRequestCodeReplayActor<PurchaseFetchResult<ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType>>
        implements BillingPurchaseFetcherRx<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType>
{
    //<editor-fold desc="Constructors">
    protected BaseBillingPurchaseFetcherRx(int requestCode)
    {
        super(requestCode);
    }
    //</editor-fold>

    @Override @NonNull public Observable<PurchaseFetchResult<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> get()
    {
        return replayObservable;
    }
}
