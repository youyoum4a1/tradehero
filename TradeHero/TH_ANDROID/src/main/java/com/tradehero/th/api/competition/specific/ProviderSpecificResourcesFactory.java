package com.tradehero.th.api.competition.specific;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.specific.etoro.EToroProviderSpecificResourcesDTO;
import com.tradehero.th.api.competition.specific.macquarie.MacquarieProviderSpecificResourcesDTO;
import com.tradehero.th.api.competition.specific.macquarie.PhillipMacquarieProviderSpecificResourcesDTO;
import javax.inject.Inject;
import android.support.annotation.Nullable;

public class ProviderSpecificResourcesFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificResourcesFactory()
    {
        super();
    }
    //</editor-fold>

    @Nullable
    public ProviderSpecificResourcesDTO createResources(@Nullable ProviderDTO providerDTO)
    {
        ProviderSpecificResourcesDTO created = null;
        if (providerDTO != null)
        {
            created = createResources(providerDTO.getProviderId());
        }
        return created;
    }

    @Nullable
    public ProviderSpecificResourcesDTO createResources(@Nullable ProviderId providerId)
    {
        ProviderSpecificResourcesDTO created = null;
        if (providerId != null)
        {
            switch(providerId.key)
            {
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    created = new MacquarieProviderSpecificResourcesDTO();
                    break;
                case ProviderIdConstants.PROVIDER_ID_PHILLIP_MACQUARIE_WARRANTS:
                    created = new PhillipMacquarieProviderSpecificResourcesDTO();
                    break;
                case ProviderIdConstants.PROVIDER_ID_E_TORO:
                    created = new EToroProviderSpecificResourcesDTO();
                    break;
                case ProviderIdConstants.PROVIDER_ID_SGX_STOCKWHIZ:
                    // Nothing to do for now
                    break;
            }
        }

        return created;
    }
}
