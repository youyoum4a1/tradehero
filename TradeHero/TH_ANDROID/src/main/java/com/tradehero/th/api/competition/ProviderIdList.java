package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.DTOKeyIdList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 6:55 PM To change this template use File | Settings | File Templates. */
public class ProviderIdList extends DTOKeyIdList<ProviderListKey, ProviderId>
{
    public static final String TAG = ProviderIdList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public ProviderIdList()
    {
        super();
    }

    public ProviderIdList(int capacity)
    {
        super(capacity);
    }

    public ProviderIdList(Collection<? extends ProviderId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
