package com.androidth.general.models.provider;

import android.support.annotation.NonNull;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.models.DTOProcessor;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.models.portfolio.DTOProcessorPortfolioReceived;

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
