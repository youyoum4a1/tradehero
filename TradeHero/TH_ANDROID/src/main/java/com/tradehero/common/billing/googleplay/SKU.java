package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifier;

/**
 * Created by julien on 4/11/13
 */
public class SKU implements ProductIdentifier
{
    public final String identifier;

    public SKU(String id)
    {
        identifier = id;
    }

    @Override public boolean equals(Object other)
    {
        return (other != null) && (other instanceof SKU) && equals((SKU) other);
    }

    public boolean equals(SKU other)
    {
        return other != null && (identifier == null ? other.identifier == null : identifier.equals(other.identifier));
    }

    @Override public int hashCode()
    {
        return identifier == null ? 0 : identifier.hashCode();
    }
}
