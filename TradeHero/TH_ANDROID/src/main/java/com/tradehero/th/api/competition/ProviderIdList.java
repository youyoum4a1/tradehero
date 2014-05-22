package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class ProviderIdList extends DTOKeyIdList<ProviderId>
{
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
