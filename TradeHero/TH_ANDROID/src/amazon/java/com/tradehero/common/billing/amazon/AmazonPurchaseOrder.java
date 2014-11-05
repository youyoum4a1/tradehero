package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.PurchaseOrder;
import android.support.annotation.NonNull;

public class AmazonPurchaseOrder<AmazonSKUType extends AmazonSKU>
        implements PurchaseOrder<AmazonSKUType>
{
    @NonNull protected final AmazonSKUType sku;
    protected final int quantity;

    //<editor-fold desc="Constructors">
    public AmazonPurchaseOrder(
            @NonNull AmazonSKUType sku,
            int quantity)
    {
        this.sku = sku;
        this.quantity = quantity;
    }
    //</editor-fold>

    @NonNull @Override public AmazonSKUType getProductIdentifier()
    {
        return sku;
    }

    @Override public int getQuantity()
    {
        return quantity;
    }

    @Override public String toString()
    {
        return "AmazonPurchaseOrder{" +
                "sku=" + sku +
                ", quantity=" + quantity +
                '}';
    }
}
