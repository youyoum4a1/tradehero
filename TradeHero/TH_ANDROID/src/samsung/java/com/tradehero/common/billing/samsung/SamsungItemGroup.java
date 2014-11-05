package com.tradehero.common.billing.samsung;

import com.tradehero.common.persistence.DTOKey;
import android.support.annotation.NonNull;

public class SamsungItemGroup implements DTOKey
{
    @NonNull public final String groupId;

    //<editor-fold desc="Constructors">
    public SamsungItemGroup(@NonNull String groupId)
    {
        super();
        this.groupId = groupId;
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
        return !groupId.isEmpty();
    }

    @Override public int hashCode()
    {
        return groupId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null &&
                getClass().equals(other.getClass()) &&
                equals((SamsungItemGroup) other);
    }

    public boolean equals(SamsungItemGroup other)
    {
        return (other != null)
                && getClass().equals(other.getClass())
                && groupId.equals(other.groupId);
    }

    @Override public String toString()
    {
        return String.format("{groupId:%s}", groupId);
    }
}
