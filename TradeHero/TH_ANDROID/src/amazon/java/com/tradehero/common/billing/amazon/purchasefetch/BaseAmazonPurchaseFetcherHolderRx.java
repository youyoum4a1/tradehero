package com.tradehero.common.billing.amazon.purchasefetch;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.purchasefetch.BaseBillingPurchaseFetcherHolderRx;

abstract public class BaseAmazonPurchaseFetcherHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BaseBillingPurchaseFetcherHolderRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
        implements AmazonPurchaseFetcherHolderRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected AmazonPurchaseFetcherRx<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType> createFetcher(
            int requestCode);
}
