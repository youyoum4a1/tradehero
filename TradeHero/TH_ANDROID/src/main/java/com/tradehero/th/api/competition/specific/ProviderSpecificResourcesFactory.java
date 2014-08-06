package com.tradehero.th.api.competition.specific;

import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.specific.etoro.EToroProviderSpecificResourcesDTO;
import com.tradehero.th.api.competition.specific.macquarie.MacquarieProviderSpecificResourcesDTO;
import com.tradehero.th.api.competition.specific.macquarie.PhillipMacquarieProviderSpecificResourcesDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class ProviderSpecificResourcesFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificResourcesFactory()
    {
        super();
    }
    //</editor-fold>

    @Contract("null -> null; !null -> !null") @Nullable
    public ProviderSpecificResourcesDTO createResources(@Nullable ProviderCompactDTO providerCompactDTO)
    {
        ProviderSpecificResourcesDTO created = null;
        if (providerCompactDTO != null)
        {
            created = createResources(providerCompactDTO.getProviderId());
        }
        return created;
    }

    @Contract("null -> null; !null -> _") @Nullable
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
