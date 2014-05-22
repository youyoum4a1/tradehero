package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.models.provider.etoro.EToroProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.macquarie.MacquarieProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.macquarie.PhillipMacquarieProviderSpecificResourcesDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ProviderSpecificResourcesFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificResourcesFactory()
    {
        super();
    }
    //</editor-fold>

    public ProviderSpecificResourcesDTO createResourcesDTO(ProviderDTO providerDTO)
    {
        ProviderSpecificResourcesDTO created = null;
        if (providerDTO != null)
        {
            created = createResourcesDTO(providerDTO.getProviderId());
        }
        return created;
    }

    public ProviderSpecificResourcesDTO createResourcesDTO(ProviderId providerId)
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
            }
        }
        return created;
    }
}
