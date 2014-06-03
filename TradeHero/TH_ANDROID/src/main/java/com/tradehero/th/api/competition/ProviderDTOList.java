package com.tradehero.th.api.competition;

import java.util.ArrayList;
import java.util.Collection;

public class ProviderDTOList extends ArrayList<ProviderDTO>
{
    //<editor-fold desc="Constructors">
    public ProviderDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public ProviderDTOList()
    {
        super();
    }

    public ProviderDTOList(Collection<? extends ProviderDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    public ProviderIdList getIds()
    {
        ProviderIdList ids = new ProviderIdList();
        for (ProviderDTO providerDTO : this)
        {
            if (providerDTO != null)
            {
                ids.add(providerDTO.getProviderId());
            }
        }
        return ids;
    }
}
