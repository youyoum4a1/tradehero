package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifier;

/**
 * Created by julien on 4/11/13
 */
public class IABSKU implements ProductIdentifier
{
    public final String identifier;

    public IABSKU(String id)
    {
        identifier = id;
    }

    @Override public boolean equals(Object other)
    {
        return (other != null) && (other instanceof IABSKU) && equals((IABSKU) other);
    }

    public boolean equals(IABSKU other)
    {
        return other != null && (identifier == null ? other.identifier == null : identifier.equals(other.identifier));
    }

    @Override public int hashCode()
    {
        return identifier == null ? 0 : identifier.hashCode();
    }

    @Override public String toString()
    {
        return "IABSKU{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
