package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;

public class AmazonSKU implements ProductIdentifier
{
    @NonNull public final String skuId;

    //<editor-fold desc="Constructors">
    public AmazonSKU(@NonNull String skuId)
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

    protected boolean equalsFields(@NonNull AmazonSKU other)
    {
        return skuId.equals(other.skuId);
    }
}
