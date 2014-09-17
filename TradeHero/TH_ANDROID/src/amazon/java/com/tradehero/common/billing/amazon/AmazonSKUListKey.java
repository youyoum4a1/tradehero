package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.ProductIdentifierListKey;
import org.jetbrains.annotations.NotNull;

public class AmazonSKUListKey 
        implements ProductIdentifierListKey
{
    @NotNull public final ProductType productType;

    //<editor-fold desc="Constructors">
    public AmazonSKUListKey(@NotNull ProductType productType)
    {
        this.productType = productType;
    }
    //</editor-fold>

    @NotNull public static AmazonSKUListKey getConsumable()
    {
        return new AmazonSKUListKey(ProductType.CONSUMABLE);
    }

    @NotNull public static AmazonSKUListKey getEntitled()
    {
        return new AmazonSKUListKey(ProductType.ENTITLED);
    }

    @NotNull public static AmazonSKUListKey getSubscription()
    {
        return new AmazonSKUListKey(ProductType.SUBSCRIPTION);
    }
}
