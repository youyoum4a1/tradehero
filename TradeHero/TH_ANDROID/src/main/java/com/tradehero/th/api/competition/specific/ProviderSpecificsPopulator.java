package com.tradehero.th.api.competition.specific;

import com.tradehero.th.api.competition.ProviderDTO;
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

    public void populate(@Nullable ProviderDTO providerDTO)
    {
        if (providerDTO != null)
        {
            providerDTO.specificResources = providerSpecificResourcesFactory.createResources(providerDTO);
            providerDTO.specificKnowledge = providerSpecificKnowledgeFactory.createKnowledge(providerDTO);
        }
    }
}
