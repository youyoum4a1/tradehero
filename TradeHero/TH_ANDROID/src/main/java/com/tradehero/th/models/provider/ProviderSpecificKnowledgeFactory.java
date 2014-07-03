package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.models.provider.macquarie.MacquarieProviderSpecificKnowledgeDTO;
import com.tradehero.th.models.provider.macquarie.PhillipMacquarieProviderSpecificKnowledgeDTO;
import com.tradehero.th.models.provider.sgxtockwhiz.SgxStockWhizProviderSpecificKnowledgeDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ProviderSpecificKnowledgeFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificKnowledgeFactory()
    {
        super();
    }
    //</editor-fold>

    public ProviderSpecificKnowledgeDTO createKnowledge(ProviderDTO providerDTO)
    {
        ProviderSpecificKnowledgeDTO created = null;
        if (providerDTO != null)
        {
            created = createKnowledge(providerDTO.getProviderId());
        }
        return created;
    }

    public ProviderSpecificKnowledgeDTO createKnowledge(ProviderId providerId)
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
                case ProviderIdConstants.PROVIDER_ID_SGX_STOCKWHIZ:
                    created = new SgxStockWhizProviderSpecificKnowledgeDTO();
                    break;
            }
        }

        return created;
    }
}
