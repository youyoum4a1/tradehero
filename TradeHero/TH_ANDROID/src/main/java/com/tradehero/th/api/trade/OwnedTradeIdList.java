package com.tradehero.th.api.trade;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:50 PM To change this template use File | Settings | File Templates. */
public class OwnedTradeIdList extends DTOKeyIdList<OwnedTradeId>
{
    public static final String TAG = OwnedTradeIdList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public OwnedTradeIdList()
    {
        super();
    }

    public OwnedTradeIdList(int capacity)
    {
        super(capacity);
    }

    public OwnedTradeIdList(Collection<? extends OwnedTradeId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
