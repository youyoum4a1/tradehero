package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.OrderId;

public class IABOrderId implements OrderId
{
    public final String orderId;
    public String packageName;
    public String productId;
    public long purchaseTime;
    public int purchaseState;
    public String developerPayload;
    public String purchaseToken;

    public IABOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    @Override public boolean equals(Object other)
    {
        return (other != null) && (other instanceof IABOrderId) && equals((IABOrderId) other);
    }

    public boolean equals(IABOrderId other)
    {
        return other != null && (orderId == null ? other.orderId == null : orderId.equals(other.orderId));
    }

    @Override public int hashCode()
    {
        return orderId == null ? 0 : orderId.hashCode();
    }
}
