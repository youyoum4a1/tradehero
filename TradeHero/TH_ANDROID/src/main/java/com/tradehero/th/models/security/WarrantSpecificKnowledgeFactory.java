package com.tradehero.th.models.security;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;
import com.tradehero.th.models.provider.ProviderSpecificKnowledgeFactory;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class WarrantSpecificKnowledgeFactory
{
    @NotNull private final Map<ProviderId, OwnedPortfolioId> warrantUsingProviders;
    @NotNull protected final ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public WarrantSpecificKnowledgeFactory(
            @NotNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory)
    {
        super();
        this.providerSpecificKnowledgeFactory = providerSpecificKnowledgeFactory;
        warrantUsingProviders = new HashMap<>();
    }
    //</editor-fold>

    public void add(ProviderId providerId, OwnedPortfolioId applicableOwnedPortfolioId)
    {
        ProviderSpecificKnowledgeDTO knowledgeDTO = providerSpecificKnowledgeFactory.createKnowledge(providerId);
        if (knowledgeDTO != null && knowledgeDTO.includeProviderPortfolioOnWarrants != null &&
                knowledgeDTO.includeProviderPortfolioOnWarrants)
        {
            warrantUsingProviders.put(providerId, applicableOwnedPortfolioId);
        }
    }

    public void clear()
    {
        warrantUsingProviders.clear();
    }
}
