package com.androidth.general.api.system;

import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTOKey;

public class SystemStatusKey implements DTOKey
{
    @Override public int hashCode()
    {
        return 0;
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof SystemStatusKey;
    }
}
