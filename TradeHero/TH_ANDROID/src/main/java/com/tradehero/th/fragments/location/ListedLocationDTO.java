package com.tradehero.th.fragments.location;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.Country;
import android.support.annotation.NonNull;

class ListedLocationDTO implements DTO
{
    @NonNull public final Country country;

    //<editor-fold desc="Constructors">
    public ListedLocationDTO(@NonNull Country country)
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
