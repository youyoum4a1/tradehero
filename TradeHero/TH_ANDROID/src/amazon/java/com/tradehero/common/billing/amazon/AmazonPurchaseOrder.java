package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.RequestId;
import com.tradehero.common.billing.PurchaseOrder;
import org.jetbrains.annotations.NotNull;

public class AmazonPurchaseOrder<AmazonSKUType extends AmazonSKU>
        implements PurchaseOrder<AmazonSKUType>
{
    @NotNull protected final AmazonSKUType sku;
    protected final int quantity;
    @NotNull protected final RequestId requestId;

    //<editor-fold desc="Constructors">
    public AmazonPurchaseOrder(
            @NotNull AmazonSKUType sku,
            int quantity,
            @NotNull RequestId requestId)
    {
        this.sku = sku;
        this.quantity = quantity;
        this.requestId = requestId;
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
