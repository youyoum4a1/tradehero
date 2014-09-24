package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ProviderIdList extends DTOKeyIdList<ProviderId>
{
    //<editor-fold desc="Constructors">
    public ProviderIdList()
    {
        super();
    }

    public ProviderIdList(@NotNull Collection<? extends ProviderDTO> providerDTOs)
    {
        super();
        for (ProviderDTO providerDTO: providerDTOs)
        {
            add(providerDTO.getProviderId());
        }
    }
    //</editor-fold>
}
