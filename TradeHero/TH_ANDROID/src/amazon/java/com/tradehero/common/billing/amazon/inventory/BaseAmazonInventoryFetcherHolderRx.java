package com.tradehero.common.billing.amazon.inventory;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.inventory.BaseBillingInventoryFetcherHolderRx;
import java.util.List;

abstract public class BaseAmazonInventoryFetcherHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>>
        extends BaseBillingInventoryFetcherHolderRx<
        AmazonSKUType,
        AmazonProductDetailType>
        implements AmazonInventoryFetcherHolderRx<
        AmazonSKUType,
        AmazonProductDetailType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonInventoryFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected AmazonInventoryFetcherRx<AmazonSKUType, AmazonProductDetailType> createFetcher(
            int requestCode,
            @NonNull List<AmazonSKUType> productIdentifiers);
}
