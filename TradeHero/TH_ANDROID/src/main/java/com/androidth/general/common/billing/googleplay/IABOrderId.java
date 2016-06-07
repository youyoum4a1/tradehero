package com.androidth.general.common.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.OrderId;

public class IABOrderId implements OrderId
{
    @NonNull public final String orderId;
    public String packageName;
    public String productId;
    public long purchaseTime;
    public int purchaseState;
    public String developerPayload;
    public String purchaseToken;

    //<editor-fold desc="Constructors">
    public IABOrderId(@NonNull String orderId)
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
