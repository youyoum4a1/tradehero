package com.tradehero.th.api.system;

import com.tradehero.common.persistence.DTOKey;

public class SystemStatusKey implements DTOKey
{
    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof SystemStatusKey;
    }
}
