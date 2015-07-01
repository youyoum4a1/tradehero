package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductPurchase;

public interface AmazonPurchase<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        extends ProductPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NonNull String getAmazonUserId();
    boolean isCancelled();
}
