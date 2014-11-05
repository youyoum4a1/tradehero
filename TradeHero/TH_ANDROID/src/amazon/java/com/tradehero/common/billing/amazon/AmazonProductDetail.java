package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.Product;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import android.support.annotation.NonNull;

abstract public class AmazonProductDetail<AmazonSKUType extends AmazonSKU>
    implements ProductDetail<AmazonSKUType>
{
    @NonNull protected final Product product;

    //<editor-fold desc="Constructors">
    public AmazonProductDetail(@NonNull Product product)
    {
        super();
        this.product = product;
    }
    //</editor-fold>

    @Override @NonNull abstract public AmazonSKUType getProductIdentifier();
}
