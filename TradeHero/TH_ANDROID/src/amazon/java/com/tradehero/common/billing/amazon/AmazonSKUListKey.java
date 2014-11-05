package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.ProductIdentifierListKey;
import android.support.annotation.NonNull;

public class AmazonSKUListKey 
        implements ProductIdentifierListKey
{
    @NonNull public final ProductType productType;

    //<editor-fold desc="Constructors">
    public AmazonSKUListKey(@NonNull ProductType productType)
    {
        this.productType = productType;
    }
    //</editor-fold>

    @NonNull public static AmazonSKUListKey getConsumable()
    {
        return new AmazonSKUListKey(ProductType.CONSUMABLE);
    }

    @NonNull public static AmazonSKUListKey getEntitled()
    {
        return new AmazonSKUListKey(ProductType.ENTITLED);
    }

    @NonNull public static AmazonSKUListKey getSubscription()
    {
        return new AmazonSKUListKey(ProductType.SUBSCRIPTION);
    }
}
