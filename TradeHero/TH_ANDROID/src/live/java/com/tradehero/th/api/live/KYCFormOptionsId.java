package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKey;

public class KYCFormOptionsId implements DTOKey
{
    @NonNull public final LiveBrokerId brokerId;

    public KYCFormOptionsId(@NonNull LiveBrokerId brokerId)
    {
        this.brokerId = brokerId;
    }

    @Override public int hashCode()
    {
        return brokerId.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        return o instanceof KYCFormOptionsId && ((KYCFormOptionsId) o).brokerId.equals(brokerId);
    }
}
