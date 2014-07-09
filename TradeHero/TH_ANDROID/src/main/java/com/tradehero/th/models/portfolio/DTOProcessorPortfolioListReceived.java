package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorPortfolioListReceived<PortfolioCompactListType extends PortfolioCompactDTOList>
    implements DTOProcessor<PortfolioCompactListType>
{
    @NotNull private final DTOProcessor<PortfolioCompactDTO> individualProcessor;

    public DTOProcessorPortfolioListReceived(@NotNull DTOProcessor<PortfolioCompactDTO> individualProcessor)
    {
        this.individualProcessor = individualProcessor;
    }

    @Override public PortfolioCompactListType process(PortfolioCompactListType value)
    {
        if (value != null)
        {
            for (PortfolioCompactDTO portfolioCompactType : value)
            {
                individualProcessor.process(portfolioCompactType);
            }
        }
        return value;
    }
}
