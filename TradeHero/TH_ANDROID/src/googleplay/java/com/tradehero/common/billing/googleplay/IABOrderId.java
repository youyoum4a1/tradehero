package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.OrderId;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IABOrderId implements OrderId
{
    @NotNull public final String orderId;
    public String packageName;
    public String productId;
    public long purchaseTime;
    public int purchaseState;
    public String developerPayload;
    public String purchaseToken;

    //<editor-fold desc="Constructors">
    public IABOrderId(@NotNull String orderId)
    {
        this.orderId = orderId;
    }
    //</editor-fold>

    @Override public boolean equals(@Nullable Object other)
    {
        return (other instanceof IABOrderId) && equals((IABOrderId) other);
    }

    public boolean equals(@Nullable IABOrderId other)
    {
        return other != null && orderId.equals(other.orderId);
    }

    @Override public int hashCode()
    {
        return orderId.hashCode();
    }
}
