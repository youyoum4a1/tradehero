package com.tradehero.common.billing.inventory;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeReplayActor;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import java.util.List;
import rx.Observable;

abstract public class BaseBillingInventoryFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetail<ProductIdentifierType>>
    extends BaseRequestCodeReplayActor<ProductInventoryResult<ProductIdentifierType, ProductDetailsType>>
        implements BillingInventoryFetcherRx<
        ProductIdentifierType,
        ProductDetailsType>
{
    @NonNull private final List<ProductIdentifierType> productIdentifiers;

    //<editor-fold desc="Constructors">
    protected BaseBillingInventoryFetcherRx(
            int requestCode,
            @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        super(requestCode);
        this.productIdentifiers = productIdentifiers;
    }
    //</editor-fold>

    @Override @NonNull public List<ProductIdentifierType> getProductIdentifiers()
    {
        return productIdentifiers;
    }

    @NonNull @Override public Observable<ProductInventoryResult<ProductIdentifierType, ProductDetailsType>> get()
    {
        return replayObservable;
    }
}