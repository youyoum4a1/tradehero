package com.tradehero.th.fragments.location;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.Country;
import org.jetbrains.annotations.NotNull;

class ListedLocationDTO implements DTO
{
    @NotNull public final Country country;

    //<editor-fold desc="Constructors">
    public ListedLocationDTO(@NotNull Country country)
    {
        this.country = country;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return country.hashCode();
    }

    @Override public boolean equals(Object obj)
    {
        return obj instanceof ListedLocationDTO && ((ListedLocationDTO) obj).country.equals(country);
    }
}
