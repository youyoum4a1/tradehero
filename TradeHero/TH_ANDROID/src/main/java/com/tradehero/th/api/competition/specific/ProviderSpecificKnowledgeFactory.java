package com.tradehero.th.api.competition.specific;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.specific.macquarie.MacquarieProviderSpecificKnowledgeDTO;
import com.tradehero.th.api.competition.specific.macquarie.PhillipMacquarieProviderSpecificKnowledgeDTO;
import com.tradehero.th.api.competition.specific.sgxtockwhiz.SgxStockWhizProviderSpecificKnowledgeDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class ProviderSpecificKnowledgeFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificKnowledgeFactory()
    {
        super();
    }
    //</editor-fold>

    @Contract("null -> null; !null -> !null") @Nullable
    public ProviderSpecificKnowledgeDTO createKnowledge(@Nullable ProviderDTO providerDTO)
    {
        ProviderSpecificKnowledgeDTO created = null;
        if (providerDTO != null)
        {
            created = createKnowledge(providerDTO.getProviderId());
        }
        return created;
    }

    @Contract("null -> null; !null -> _") @Nullable
    public ProviderSpecificKnowledgeDTO createKnowledge(@Nullable ProviderId providerId)
    {
        ProviderSpecificKnowledgeDTO created = null;

        if (providerId != null)
        {
            switch (providerId.key)
            {
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    created = new MacquarieProviderSpecificKnowledgeDTO();
                    break;
                case ProviderIdConstants.PROVIDER_ID_PHILLIP_MACQUARIE_WARRANTS:
                    created = new PhillipMacquarieProviderSpecificKnowledgeDTO();
                    break;
                case ProviderIdConstants.PROVIDER_ID_E_TORO:
                    // Nothing to do for now
                    break;
                case ProviderIdConstants.PROVIDER_ID_SGX_STOCKWHIZ:
                    created = new SgxStockWhizProviderSpecificKnowledgeDTO();
                    break;

                default:
                    Timber.e(new IllegalArgumentException(), "Unhandled ProviderId.key == %d", providerId.key);
            }
        }

        return created;
    }
}
