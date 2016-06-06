package com.androidth.general.common.billing.inventory;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeActor;
import com.androidth.general.common.billing.ProductDetail;
import com.androidth.general.common.billing.ProductIdentifier;
import java.util.List;

abstract public class BaseBillingInventoryFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetail<ProductIdentifierType>>
        extends BaseRequestCodeActor
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
}
