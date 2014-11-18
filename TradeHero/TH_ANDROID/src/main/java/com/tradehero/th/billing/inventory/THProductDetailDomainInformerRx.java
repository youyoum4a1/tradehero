package com.tradehero.th.billing.inventory;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;
import rx.Observer;

public interface THProductDetailDomainInformerRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
{
    @NonNull Observer<ProductInventoryResult<
            ProductIdentifierType,
            THProductDetailType>> getDetailsOfDomain(
            int requetCode,
            @NonNull ProductIdentifierDomain domain);
}
