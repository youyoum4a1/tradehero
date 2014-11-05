package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        return other instanceof DisplayNameDTO && isSameName(((DisplayNameDTO) other).displayName);
    }

    public boolean isSameName(@Nullable String otherName)
    {
        return otherName != null && displayName.equals(otherName);
    }
}
