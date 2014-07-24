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
}
