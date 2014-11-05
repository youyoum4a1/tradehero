package com.tradehero.th.fragments.location;

import com.tradehero.th.api.market.Country;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import android.support.annotation.NonNull;

class ListedLocationDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ListedLocationDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public List<ListedLocationDTO> createListToShow()
    {
        List<ListedLocationDTO> created = new ArrayList<>();
        for (Country country : Country.values())
        {
            if (!country.equals(Country.NONE))
            {
                created.add(new ListedLocationDTO(country));
            }
        }
        return created;
    }
}
