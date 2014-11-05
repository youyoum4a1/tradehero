package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import android.support.annotation.NonNull;

public class ProviderIdList extends DTOKeyIdList<ProviderId>
{
    //<editor-fold desc="Constructors">
    public ProviderIdList()
    {
        super();
    }

    public ProviderIdList(@NonNull Collection<? extends ProviderDTO> providerDTOs)
    {
        super();
        for (ProviderDTO providerDTO: providerDTOs)
        {
            add(providerDTO.getProviderId());
        }
    }
    //</editor-fold>
}
