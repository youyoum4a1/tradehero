package com.androidth.general.billing.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.RequestCodeHolder;
import com.androidth.general.common.billing.inventory.ProductInventoryResult;
import com.androidth.general.billing.ProductIdentifierDomain;
import com.androidth.general.billing.THProductDetail;

public interface THProductDetailDomainInformerRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends RequestCodeHolder
{
    @NonNull rx.Observable<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getDetailsOfDomain(
            int requestCode,
            @NonNull ProductIdentifierDomain domain);
}
