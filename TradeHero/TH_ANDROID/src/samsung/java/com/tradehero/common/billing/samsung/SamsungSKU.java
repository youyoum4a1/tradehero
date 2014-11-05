package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductIdentifier;
import android.support.annotation.NonNull;

public class SamsungSKU
        extends SamsungItemGroup
        implements ProductIdentifier
{
    @NonNull public final String itemId;

    //<editor-fold desc="Constructors">
    public SamsungSKU(@NonNull String groupId, @NonNull String itemId)
    {
        super(groupId);
        this.itemId = itemId;
        checkIsValid();
    }
    //</editor-fold>

    public boolean isValid()
    {
        return super.isValid() && !itemId.isEmpty();
    }

    public SamsungItemGroup getGroupId()
    {
        return new SamsungItemGroup(groupId);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ itemId.hashCode();
    }

    @Override public boolean equals(SamsungItemGroup other)
    {
        return super.equals(other) &&
                equals((SamsungSKU) other);
    }

    public boolean equals(SamsungSKU other)
    {
        return super.equals(other) && itemId.equals(other.itemId);
    }

    @Override public String toString()
    {
        return String.format("{groupId:%s, itemId:%s}", groupId, itemId);
    }
}
