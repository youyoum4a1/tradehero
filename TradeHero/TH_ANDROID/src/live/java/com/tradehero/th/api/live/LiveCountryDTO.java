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
}
