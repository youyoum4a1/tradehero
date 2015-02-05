package com.tradehero.th.api.competition.specific;

import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderDTO;

@Deprecated
public class ProviderSpecificsPopulator
{
    public static void populate(@Nullable ProviderDTO providerDTO)
    {
        if (providerDTO != null)
        {
            providerDTO.specificKnowledge = ProviderSpecificKnowledgeFactory.createKnowledge(providerDTO);
        }
    }
}
