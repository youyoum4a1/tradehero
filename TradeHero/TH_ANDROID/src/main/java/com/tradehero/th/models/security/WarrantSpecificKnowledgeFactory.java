package com.tradehero.th.models.security;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class WarrantSpecificKnowledgeFactory
{
    @NotNull private final Map<ProviderId, OwnedPortfolioId> warrantUsingProviders;

    //<editor-fold desc="Constructors">
    @Inject public WarrantSpecificKnowledgeFactory()
    {
        super();
        warrantUsingProviders = new HashMap<>();
    }
    //</editor-fold>

    public void add(@NotNull ProviderDTO providerDTO)
    {
        if (providerDTO.specificKnowledge != null &&
                providerDTO.specificKnowledge.includeProviderPortfolioOnWarrants != null &&
                providerDTO.specificKnowledge.includeProviderPortfolioOnWarrants)
        {
            warrantUsingProviders.put(
                    providerDTO.getProviderId(),
                    providerDTO.getAssociatedOwnedPortfolioId());
        }
    }

    public void clear()
    {
        warrantUsingProviders.clear();
    }
}
