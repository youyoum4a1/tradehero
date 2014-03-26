package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductIdentifier;

/**
 * Created by xavier on 3/26/14.
 */
public class SamsungSKU implements ProductIdentifier
{
    public final String groupId;
    public final String itemId;

    public SamsungSKU(String groupId, String itemId)
    {
        this.groupId = groupId;
        this.itemId = itemId;
    }
}
