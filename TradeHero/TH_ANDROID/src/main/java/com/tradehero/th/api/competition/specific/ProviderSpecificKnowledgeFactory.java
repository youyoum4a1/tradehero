package com.tradehero.th.api.competition.specific;

import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.specific.sgxtockwhiz.SgxStockWhizProviderSpecificKnowledgeDTO;

@Deprecated
public class ProviderSpecificKnowledgeFactory
{
    @Nullable public static ProviderSpecificKnowledgeDTO createKnowledge(
            @Nullable ProviderDTO providerDTO)
    {
        ProviderSpecificKnowledgeDTO created = null;
        if (providerDTO != null)
        {
            created = createKnowledge(providerDTO.getProviderId());
        }
        return created;
    }

    @Nullable public static ProviderSpecificKnowledgeDTO createKnowledge(
            @Nullable ProviderId providerId)
    {
        ProviderSpecificKnowledgeDTO created = null;

        if (providerId != null)
        {
            switch (providerId.key)
            {
                case ProviderIdConstants.PROVIDER_ID_SGX_STOCKWHIZ:
                    created = new SgxStockWhizProviderSpecificKnowledgeDTO();
                    break;
            }
        }

        return created;
    }
}
