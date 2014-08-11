package com.tradehero.th.api.level.key;

import com.tradehero.common.persistence.DTOKey;

public class LevelDefListId implements DTOKey
{
    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }

        return o instanceof LevelDefListId;
    }
}
