package com.tradehero.th.api.live;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.market.Country;

public class IdentityPromptInfoKey implements DTOKey
{
    public Country country;

    public IdentityPromptInfoKey(Country country)
    {
        this.country = country;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof IdentityPromptInfoKey)) return false;

        IdentityPromptInfoKey that = (IdentityPromptInfoKey) o;

        return country == that.country;
    }

    @Override public int hashCode()
    {
        return country != null ? country.hashCode() : 0;
    }
}
