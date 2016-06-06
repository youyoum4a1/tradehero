package com.androidth.general.common.billing.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductDetail;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.RequestCodeActor;
import java.util.List;
import rx.Observable;

public interface BillingInventoryFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
    extends RequestCodeActor
{
    @NonNull List<ProductIdentifierType> getProductIdentifiers();
    @NonNull Observable<ProductInventoryResult<ProductIdentifierType, ProductDetailType>> get();
}
