package com.androidth.general.api.level.key;

import com.androidth.general.common.persistence.DTOKey;

public class LevelDefListId implements DTOKey
{
    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof LevelDefListId;
    }
}
