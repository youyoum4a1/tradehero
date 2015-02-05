package com.tradehero.th.fragments.location;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.Country;
import java.util.ArrayList;
import java.util.List;

class ListedLocationDTOFactory
{
    @NonNull public static List<ListedLocationDTO> createListToShow()
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
