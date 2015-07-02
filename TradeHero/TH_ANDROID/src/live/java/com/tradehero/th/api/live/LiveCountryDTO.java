package com.tradehero.th.api.live;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.Country;

public class LiveCountryDTO implements DTO
{
    public Country country;

    public LiveCountryDTO()
    {
        super();
    }

    public LiveCountryDTO(Country country)
    {
        super();
        this.country = country;
    }
}
