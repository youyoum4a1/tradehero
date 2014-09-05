package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IABSKU implements ProductIdentifier
{
    @NotNull public final String identifier;

    //<editor-fold desc="Constructors">
    public IABSKU(@NotNull String id)
    {
        identifier = id;
    }
    //</editor-fold>

    @Override public boolean equals(@Nullable Object other)
    {
        return (other instanceof IABSKU) && equals((IABSKU) other);
    }

    public boolean equals(@Nullable IABSKU other)
    {
        return other != null && identifier.equals(other.identifier);
    }

    @Override public int hashCode()
    {
        return identifier.hashCode();
    }

    @Override public String toString()
    {
        return "IABSKU{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
