package com.tradehero.th.api.competition.specific;

import com.tradehero.th.api.competition.ProviderDTO;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ProviderSpecificsPopulator
{
    @NonNull protected final ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;
    @NonNull protected final ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificsPopulator(
            @NonNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory,
            @NonNull ProviderSpecificResourcesFactory providerSpecificResourcesFactory)
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
