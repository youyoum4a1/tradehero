package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.OrderId;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 11:29 AM To change this template use File | Settings | File Templates. */
public class GoogleOrderId implements OrderId
{
    public static final String TAG = GoogleOrderId.class.getSimpleName();

    public final String orderId;

    public GoogleOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    @Override public boolean equals(Object other)
    {
        return (other != null) && (other instanceof GoogleOrderId) && equals((GoogleOrderId) other);
    }

    public boolean equals(GoogleOrderId other)
    {
        return other != null && (orderId == null ? other.orderId == null : orderId.equals(other.orderId));
    }

    @Override public int hashCode()
    {
        return orderId == null ? 0 : orderId.hashCode();
    }
}
