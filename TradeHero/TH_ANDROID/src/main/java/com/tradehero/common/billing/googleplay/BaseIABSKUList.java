package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseProductIdentifierList;
import java.util.Collection;

public class BaseIABSKUList<IABSKUType extends IABSKU>
        extends BaseProductIdentifierList<IABSKUType>
{
    //<editor-fold desc="Constructors">
    public BaseIABSKUList()
    {
        super();
    }

    public BaseIABSKUList(int capacity)
    {
        super(capacity);
    }

    public BaseIABSKUList(Collection<? extends IABSKUType> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
