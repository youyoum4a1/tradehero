package com.ayondo.academy.models.provider;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.competition.ProviderDTO;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.models.DTOProcessor;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.models.portfolio.DTOProcessorPortfolioReceived;

public class DTOProcessorProviderReceived extends ThroughDTOProcessor<ProviderDTO>
{
    @NonNull private final DTOProcessor<PortfolioCompactDTO> portfolioCompactProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderReceived(@NonNull CurrentUserId currentUserId)
    {
        this.portfolioCompactProcessor = new DTOProcessorPortfolioReceived<>(currentUserId.toUserBaseKey());
    }
    //</editor-fold>

    @Override public ProviderDTO process(@NonNull ProviderDTO value)
    {
        if (value.associatedPortfolio != null)
        {
            portfolioCompactProcessor.process(value.associatedPortfolio);
        }
        return value;
    }
}
