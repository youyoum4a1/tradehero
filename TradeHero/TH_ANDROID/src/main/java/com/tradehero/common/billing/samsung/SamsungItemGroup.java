package com.tradehero.common.billing.samsung;

import com.tradehero.common.persistence.DTOKey;
import timber.log.Timber;

/**
 * Created by xavier on 4/1/14.
 */
public class SamsungItemGroup implements DTOKey
{
    public final String groupId;

    public SamsungItemGroup(String groupId)
    {
        super();
        this.groupId = groupId;
    }

    protected void checkIsValid()
    {
        if (!isValid())
        {
            throw new IllegalArgumentException("One element is null or empty");
        }
    }

    public boolean isValid()
    {
        return groupId != null && !groupId.isEmpty();
    }

    @Override public int hashCode()
    {
        return groupId == null ? 0 : groupId.hashCode();
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
                && (groupId == null ? other.groupId == null : groupId.equals(other.groupId));
    }

    @Override public String toString()
    {
        return String.format("{groupId:%s}", groupId);
    }

}
