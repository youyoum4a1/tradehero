package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.Country;

public class LiveCountryDTO implements DTO
{
    @NonNull public final Country country;

    public LiveCountryDTO(@NonNull Country country)
    {
        super();
        this.country = country;
    }

    @Override public int hashCode()
    {
        return country.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        return o instanceof LiveCountryDTO && ((LiveCountryDTO) o).country.equals(country);
    }
}
