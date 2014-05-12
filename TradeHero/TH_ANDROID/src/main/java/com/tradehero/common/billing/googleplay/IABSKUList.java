package com.tradehero.common.billing.googleplay;

import java.util.Collection;

public class IABSKUList extends BaseIABSKUList<IABSKU>
{
    //<editor-fold desc="Constructors">
    public IABSKUList()
    {
        super();
    }

    public IABSKUList(int capacity)
    {
        super(capacity);
    }

    public IABSKUList(Collection<? extends IABSKU> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
