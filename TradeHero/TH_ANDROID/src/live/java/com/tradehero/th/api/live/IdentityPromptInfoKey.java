package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKey;

public class IdentityPromptInfoKey implements DTOKey
{
    @NonNull public final LiveBrokerId brokerId;

    public IdentityPromptInfoKey(@NonNull LiveBrokerId brokerId)
    {
        this.brokerId = brokerId;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof IdentityPromptInfoKey)) return false;

        IdentityPromptInfoKey that = (IdentityPromptInfoKey) o;

        return brokerId.equals(that.brokerId);
    }

    @Override public int hashCode()
    {
        return brokerId.hashCode();
    }
}
