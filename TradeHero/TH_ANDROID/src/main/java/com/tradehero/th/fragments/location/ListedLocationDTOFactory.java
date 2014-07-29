package com.tradehero.th.fragments.location;

import com.tradehero.th.api.market.Country;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

class ListedLocationDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ListedLocationDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NotNull public List<ListedLocationDTO> createListToShow()
    {
        List<ListedLocationDTO> created = new ArrayList<>();
        for (@NotNull Country country : Country.values())
        {
            if (!country.equals(Country.NONE))
            {
                created.add(new ListedLocationDTO(country));
            }
        }
        return created;
    }
}
