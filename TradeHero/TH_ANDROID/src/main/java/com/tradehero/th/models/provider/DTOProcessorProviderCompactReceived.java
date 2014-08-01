package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderCompactReceived implements DTOProcessor<ProviderCompactDTO>
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final DTOProcessor<PortfolioCompactDTO> portfolioCompactProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderCompactReceived(@NotNull CurrentUserId currentUserId)
    {
        this.currentUserId = currentUserId;
        this.portfolioCompactProcessor = new DTOProcessorPortfolioReceived<>(currentUserId.toUserBaseKey());
    }
    //</editor-fold>

    @Override public ProviderCompactDTO process(ProviderCompactDTO value)
    {
        if (value != null && value.associatedPortfolio != null)
        {
            portfolioCompactProcessor.process(value.associatedPortfolio);
        }
        return value;
    }
}
