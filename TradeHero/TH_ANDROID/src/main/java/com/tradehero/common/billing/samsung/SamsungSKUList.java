package com.tradehero.common.billing.samsung;

import java.util.Collection;

public class SamsungSKUList
    extends BaseSamsungSKUList<SamsungSKU>
{
    public SamsungSKUList()
    {
        super();
    }

    public SamsungSKUList(int capacity)
    {
        super(capacity);
    }

    public SamsungSKUList(Collection<? extends SamsungSKU> collection)
    {
        super(collection);
    }
}
