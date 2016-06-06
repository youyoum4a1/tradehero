package com.androidth.general.common.billing.samsung;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifier;

public class SamsungSKU
        implements ProductIdentifier
{
    @NonNull public final String itemId;

    //<editor-fold desc="Constructors">
    public SamsungSKU(@NonNull String itemId)
    {
        super();
        this.itemId = itemId;
        checkIsValid();
    }
    //</editor-fold>

    protected void checkIsValid()
    {
        if (!isValid())
        {
            throw new IllegalArgumentException("One element is null or empty");
        }
    }

    public boolean isValid()
    {
        return !itemId.isEmpty();
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ itemId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null &&
                getClass().equals(other.getClass()) &&
                equals((SamsungSKU) other);
    }

    public boolean equals(SamsungSKU other)
    {
        return (other != null)
                && getClass().equals(other.getClass())
                && itemId.equals(other.itemId);
    }

    @Override public String toString()
    {
        return String.format("{itemId:%s}", itemId);
    }
}
