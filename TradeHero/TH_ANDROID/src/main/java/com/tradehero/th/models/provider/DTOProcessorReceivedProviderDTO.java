package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeFactory;
import com.tradehero.th.api.competition.specific.ProviderSpecificResourcesFactory;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorReceivedProviderDTO implements DTOProcessor<ProviderDTO>
{
    @NotNull protected final ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;
    @NotNull protected final ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    //<editor-fold desc="Constructors">
    public DTOProcessorReceivedProviderDTO(
            @NotNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory,
            @NotNull ProviderSpecificResourcesFactory providerSpecificResourcesFactory)
    {
        this.providerSpecificKnowledgeFactory = providerSpecificKnowledgeFactory;
        this.providerSpecificResourcesFactory = providerSpecificResourcesFactory;
    }
    //</editor-fold>

    @Override public ProviderDTO process(ProviderDTO value)
    {
        if (value != null)
        {
            value.specificKnowledge = providerSpecificKnowledgeFactory.createKnowledge(value);
            value.specificResources = providerSpecificResourcesFactory.createResources(value);
        }
        return value;
    }
}
