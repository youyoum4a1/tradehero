package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.market.Country;

public class IdentityPromptInfoKey implements DTOKey
{
    @NonNull public final Country country;

    public IdentityPromptInfoKey(@NonNull Country country)
    {
        this.country = country;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof IdentityPromptInfoKey)) return false;

        IdentityPromptInfoKey that = (IdentityPromptInfoKey) o;

        return country.equals(that.country);
    }

    @Override public int hashCode()
    {
        return country.hashCode();
    }
}
