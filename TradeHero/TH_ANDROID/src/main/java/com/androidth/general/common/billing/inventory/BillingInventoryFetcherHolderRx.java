package com.androidth.general.common.billing.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductDetail;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.RequestCodeHolder;
import java.util.List;
import rx.Observable;

public interface BillingInventoryFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
        extends RequestCodeHolder
{
    @NonNull Observable<ProductInventoryResult<
            ProductIdentifierType,
            ProductDetailType>> get(
            int requestCode,
            @NonNull List<ProductIdentifierType> productIdentifiers);
}
