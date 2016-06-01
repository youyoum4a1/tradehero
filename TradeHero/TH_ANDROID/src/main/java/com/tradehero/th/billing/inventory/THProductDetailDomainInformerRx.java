package com.ayondo.academy.billing.inventory;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.RequestCodeHolder;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.ayondo.academy.billing.ProductIdentifierDomain;
import com.ayondo.academy.billing.THProductDetail;

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
