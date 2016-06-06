package com.androidth.general.fragments.location;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.market.Country;

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
