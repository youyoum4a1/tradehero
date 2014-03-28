package com.tradehero.common.billing.googleplay;

import com.tradehero.th.billing.THOrderId;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 11:29 AM To change this template use File | Settings | File Templates. */
public class IABOrderId implements THOrderId
{
    public static final String TAG = IABOrderId.class.getSimpleName();

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
