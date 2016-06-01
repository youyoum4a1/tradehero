package com.ayondo.academy.fragments.live.ayondo;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.market.Country;

class PhoneNumberDTO
{
    @NonNull public final Country dialingCountry;
    public final int dialingPrefix;
    @NonNull public final String typedNumber;

    PhoneNumberDTO(
            @NonNull Country dialingCountry,
            int dialingPrefix, @NonNull String typedNumber)
    {
        this.dialingCountry = dialingCountry;
        this.typedNumber = typedNumber;
        this.dialingPrefix = dialingPrefix;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumberDTO that = (PhoneNumberDTO) o;

        if (dialingPrefix != that.dialingPrefix) return false;
        if (dialingCountry != that.dialingCountry) return false;
        return typedNumber.equals(that.typedNumber);
    }

    @Override public String toString()
    {
        return "PhoneNumberDTO{" +
                "dialingCountry=" + dialingCountry +
                ", dialingPrefix=" + dialingPrefix +
                ", typedNumber='" + typedNumber + '\'' +
                '}';
    }
}
