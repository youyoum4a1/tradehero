package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.ProductPurchase;
import org.jetbrains.annotations.NotNull;

public interface AmazonPurchase<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId>
        extends ProductPurchase<
        AmazonSKUType,
        AmazonOrderIdType>
{
    @NotNull String getAmazonUserId();
}
