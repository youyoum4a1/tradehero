package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductIdentifier;

/**
 * Created by xavier on 3/26/14.
 */
public class SamsungSKU
        extends SamsungItemGroup
        implements ProductIdentifier
{
    public final String itemId;

    public SamsungSKU(String groupId, String itemId)
    {
        super(groupId);
        this.itemId = itemId;
        checkIsValid();
    }

    public boolean isValid()
    {
        return super.isValid() &&
                itemId != null && !itemId.isEmpty();
    }

    public SamsungItemGroup getGroupId()
    {
        return new SamsungItemGroup(groupId);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (itemId == null ? 0 : itemId.hashCode());
    }

    @Override public boolean equals(SamsungItemGroup other)
    {
        return super.equals(other) &&
                equals((SamsungSKU) other);
    }

    public boolean equals(SamsungSKU other)
    {
        return super.equals(other) &&
                (itemId == null ? other.itemId == null : itemId.equals(other.itemId));
    }

    @Override public String toString()
    {
        return String.format("{groupId:%s, itemId:%s}", groupId, itemId);
    }
}
