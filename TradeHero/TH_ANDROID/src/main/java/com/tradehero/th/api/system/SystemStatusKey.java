package com.ayondo.academy.api.system;

import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKey;

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
