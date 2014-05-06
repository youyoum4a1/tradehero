package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseProductIdentifierList;
import java.util.Collection;


public class BaseSamsungSKUList<SamsungSKUType extends SamsungSKU>
        extends BaseProductIdentifierList<SamsungSKUType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungSKUList()
    {
        super();
    }

    public BaseSamsungSKUList(int capacity)
    {
        super(capacity);
    }

    public BaseSamsungSKUList(Collection<? extends SamsungSKUType> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
