package com.tradehero.th.api.live;

import com.tradehero.common.persistence.DTOKey;

public class LiveCountryListId implements DTOKey
{
    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object o)
    {
        return o instanceof LiveCountryListId;
    }
}
