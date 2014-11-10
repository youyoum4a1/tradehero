package com.tradehero.th.api.competition.specific;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderDTO;
import javax.inject.Inject;

@Deprecated
public class ProviderSpecificsPopulator
{
    @NonNull protected final ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificsPopulator(
            @NonNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory)
    {
        this.providerSpecificKnowledgeFactory = providerSpecificKnowledgeFactory;
    }
    //</editor-fold>

    public void populate(@Nullable ProviderDTO providerDTO)
    {
        if (providerDTO != null)
        {
            providerDTO.specificKnowledge = providerSpecificKnowledgeFactory.createKnowledge(providerDTO);
        }
    }
}
