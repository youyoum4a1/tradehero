package com.ayondo.academyapp.api.kyc;

import android.support.annotation.NonNull;
import com.ayondo.academyapp.common.persistence.DTOKey;
import com.ayondo.academyapp.api.live.LiveBrokerId;

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
