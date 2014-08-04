package com.tradehero.th.api.competition.specific;

import com.tradehero.th.api.competition.ProviderCompactDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProviderSpecificsPopulator
{
    @NotNull protected final ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;
    @NotNull protected final ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificsPopulator(
            @NotNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory,
            @NotNull ProviderSpecificResourcesFactory providerSpecificResourcesFactory)
    {
        this.providerSpecificKnowledgeFactory = providerSpecificKnowledgeFactory;
        this.providerSpecificResourcesFactory = providerSpecificResourcesFactory;
    }
    //</editor-fold>

    public void populate(@Nullable ProviderCompactDTO providerCompactDTO)
    {
        if (providerCompactDTO != null)
        {
            providerCompactDTO.specificResources = providerSpecificResourcesFactory.createResources(providerCompactDTO);
            providerCompactDTO.specificKnowledge = providerSpecificKnowledgeFactory.createKnowledge(providerCompactDTO);
        }
    }
}
