package com.tradehero.common.billing.inventory;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;

public class ProductInventoryResult<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
        extends BaseResult
{
    @NonNull public final ProductIdentifierType id;
    @NonNull public final ProductDetailType detail;

    //<editor-fold desc="Constructors">
    public ProductInventoryResult(int requestCode,
            @NonNull ProductIdentifierType id,
            @NonNull ProductDetailType detail)
    {
        super(requestCode);
        this.id = id;
        this.detail = detail;
    }
    //</editor-fold>
}
