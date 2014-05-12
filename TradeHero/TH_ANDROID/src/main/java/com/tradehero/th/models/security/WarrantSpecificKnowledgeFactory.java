package com.tradehero.th.models.security;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;
import com.tradehero.th.models.provider.ProviderSpecificKnowledgeFactory;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class WarrantSpecificKnowledgeFactory
{
    private Map<ProviderId, OwnedPortfolioId> warrantUsingProviders;
    @Inject protected ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;

    @Inject public WarrantSpecificKnowledgeFactory()
    {
        super();
        warrantUsingProviders = new HashMap<>();
    }

    public void add(ProviderId providerId, OwnedPortfolioId applicableOwnedPortfolioId)
    {
        ProviderSpecificKnowledgeDTO knowledgeDTO = providerSpecificKnowledgeFactory.createKnowledge(providerId);
        if (knowledgeDTO != null && knowledgeDTO.includeProviderPortfolioOnWarrants)
        {
            warrantUsingProviders.put(providerId, applicableOwnedPortfolioId);
        }
    }

    public void clear()
    {
        warrantUsingProviders.clear();
    }

    public Map<ProviderId, OwnedPortfolioId> getWarrantApplicablePortfolios()
    {
        return new HashMap<>(warrantUsingProviders);
    }
}
