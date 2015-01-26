package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.ProductPurchase;
import android.support.annotation.NonNull;

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
