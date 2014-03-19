package com.tradehero.common.billing.googleplay;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:32 PM To change this template use File | Settings | File Templates. */
public class BaseIABSKUList<IABSKUType extends IABSKU>
        extends ArrayList<IABSKUType> implements DTO
{
    public static final String TAG = BaseIABSKUList.class.getSimpleName();

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
