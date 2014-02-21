package com.tradehero.common.billing.googleplay;

import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:32 PM To change this template use File | Settings | File Templates. */
public class IABSKUList extends BaseIABSKUList<IABSKU>
{
    public static final String TAG = IABSKUList.class.getSimpleName();

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
