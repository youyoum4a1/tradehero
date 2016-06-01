package com.ayondo.academy.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKey;

public class DisplayNameDTO implements DTOKey
{
    @NonNull public final String displayName;

    //<editor-fold desc="Constructors">
    public DisplayNameDTO(@NonNull String displayName)
    {
        this.displayName = displayName;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return displayName.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof DisplayNameDTO
                && ((DisplayNameDTO) other).displayName.equals(displayName);
    }
}
