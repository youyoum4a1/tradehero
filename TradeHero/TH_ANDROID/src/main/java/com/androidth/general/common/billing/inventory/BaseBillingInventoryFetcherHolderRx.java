package com.androidth.general.common.billing.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeHolder;
import com.androidth.general.common.billing.ProductDetail;
import com.androidth.general.common.billing.ProductIdentifier;
import java.util.List;
import rx.Observable;

abstract public class BaseBillingInventoryFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
        extends BaseRequestCodeHolder<BillingInventoryFetcherRx<
        ProductIdentifierType,
        ProductDetailType>>
        implements BillingInventoryFetcherHolderRx<
        ProductIdentifierType,
        ProductDetailType>
{
    //<editor-fold desc="Constructors">
    public BaseBillingInventoryFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductInventoryResult<ProductIdentifierType,
            ProductDetailType>> get(int requestCode, @NonNull List<ProductIdentifierType> productIdentifiers)
    {
        BillingInventoryFetcherRx<
                ProductIdentifierType,
                ProductDetailType> fetcher = actors.get(requestCode);
        if (fetcher == null)
        {
            fetcher = createFetcher(requestCode, productIdentifiers);
            actors.put(requestCode, fetcher);
        }
        return fetcher.get();
    }

    @NonNull abstract protected BillingInventoryFetcherRx<
            ProductIdentifierType,
            ProductDetailType> createFetcher(int requestCode,
            @NonNull List<ProductIdentifierType> productIdentifiers);
}
