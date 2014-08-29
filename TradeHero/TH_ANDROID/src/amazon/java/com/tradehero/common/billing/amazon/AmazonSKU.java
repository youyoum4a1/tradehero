package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.ProductIdentifier;
import org.jetbrains.annotations.NotNull;

public class AmazonSKU implements ProductIdentifier
{
    @NotNull public final String skuId;

    //<editor-fold desc="Constructors">
    public AmazonSKU(@NotNull String skuId)
    {
        this.skuId = skuId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return skuId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof AmazonSKU
                && equalsFields((AmazonSKU) other);
    }

    protected boolean equalsFields(@NotNull AmazonSKU other)
    {
        return skuId.equals(other.skuId);
    }
}
