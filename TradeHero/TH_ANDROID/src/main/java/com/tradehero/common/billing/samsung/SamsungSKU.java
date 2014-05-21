package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductIdentifier;

public class SamsungSKU implements ProductIdentifier
{
    public final String groupId;
    public final String itemId;

    public SamsungSKU(String groupId, String itemId)
    {
        this.groupId = groupId;
        this.itemId = itemId;
        checkIsValid();
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
        return groupId != null && !groupId.isEmpty() &&
                itemId != null && !itemId.isEmpty();
    }
}
