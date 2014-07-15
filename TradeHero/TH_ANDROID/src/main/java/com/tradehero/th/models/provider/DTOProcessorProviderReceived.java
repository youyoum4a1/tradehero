package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderReceived implements DTOProcessor<ProviderDTO>
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final DTOProcessor<PortfolioCompactDTO> compactProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderReceived(@NotNull CurrentUserId currentUserId)
    {
        this.currentUserId = currentUserId;
        this.compactProcessor = new DTOProcessorPortfolioReceived<>(currentUserId.toUserBaseKey());
    }
    //</editor-fold>

    @Override public ProviderDTO process(ProviderDTO value)
    {
        if (value != null)
        {
            compactProcessor.process(value.associatedPortfolio);
        }
        return value;
    }
}
