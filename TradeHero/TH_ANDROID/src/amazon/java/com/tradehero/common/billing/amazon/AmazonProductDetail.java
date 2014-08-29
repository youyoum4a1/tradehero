package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.Product;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import org.jetbrains.annotations.NotNull;

abstract public class AmazonProductDetail<AmazonSKUType extends AmazonSKU>
    implements ProductDetail<AmazonSKUType>
{
    @NotNull protected final Product product;

    //<editor-fold desc="Constructors">
    public AmazonProductDetail(@NotNull Product product)
    {
        super();
        this.product = product;
    }
    //</editor-fold>

    @Override @NotNull abstract public AmazonSKUType getProductIdentifier();
}
