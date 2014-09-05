package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.PurchaseOrder;
import org.jetbrains.annotations.NotNull;

public class AmazonPurchaseOrder<AmazonSKUType extends AmazonSKU>
        implements PurchaseOrder<AmazonSKUType>
{
    @NotNull protected final AmazonSKUType sku;
    protected final int quantity;

    //<editor-fold desc="Constructors">
    public AmazonPurchaseOrder(
            @NotNull AmazonSKUType sku,
            int quantity)
    {
        this.sku = sku;
        this.quantity = quantity;
    }
    //</editor-fold>

    @NotNull @Override public AmazonSKUType getProductIdentifier()
    {
        return sku;
    }

    @Override public int getQuantity()
    {
        return quantity;
    }
}
