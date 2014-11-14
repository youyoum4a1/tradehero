package com.tradehero.th.models.provider;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;

public class DTOProcessorProviderReceived extends ThroughDTOProcessor<ProviderDTO>
{
    @NonNull private final DTOProcessor<PortfolioCompactDTO> portfolioCompactProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderReceived(@NonNull CurrentUserId currentUserId)
    {
        this.portfolioCompactProcessor = new DTOProcessorPortfolioReceived<>(currentUserId.toUserBaseKey());
    }
    //</editor-fold>

    @Override public ProviderDTO process(ProviderDTO value)
    {
        if (value != null && value.associatedPortfolio != null)
        {
            portfolioCompactProcessor.process(value.associatedPortfolio);
        }
        return value;
    }
}
