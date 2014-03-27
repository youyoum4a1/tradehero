package com.tradehero.common.billing.samsung;

import java.util.Collection;

/**
 * Created by xavier on 3/26/14.
 */
public class SamsungSKUList
    extends BaseSamsungSKUList<SamsungSKU>
{
    //<editor-fold desc="Constructors">
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
    //</editor-fold>
}
