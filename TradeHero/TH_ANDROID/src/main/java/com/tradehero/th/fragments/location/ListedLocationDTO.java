package com.ayondo.academy.fragments.location;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.market.Country;

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
