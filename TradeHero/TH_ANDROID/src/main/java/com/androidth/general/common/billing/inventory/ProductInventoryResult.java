package com.androidth.general.common.billing.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseResult;
import com.androidth.general.common.billing.ProductDetail;
import com.androidth.general.common.billing.ProductIdentifier;
import java.util.Collections;
import java.util.Map;

public class ProductInventoryResult<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
        extends BaseResult
{
    @NonNull public final Map<ProductIdentifierType, ProductDetailType> mapped;

    //<editor-fold desc="Constructors">
    public ProductInventoryResult(int requestCode,
            @NonNull Map<ProductIdentifierType, ProductDetailType> mapped)
    {
        super(requestCode);
        this.mapped = Collections.unmodifiableMap(mapped);
    }
    //</editor-fold>
}
