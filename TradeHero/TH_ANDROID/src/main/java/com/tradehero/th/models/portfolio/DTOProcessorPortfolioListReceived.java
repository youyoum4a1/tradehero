package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import android.support.annotation.NonNull;
import rx.functions.Func1;

public class DTOProcessorPortfolioListReceived<PortfolioCompactListType extends PortfolioCompactDTOList>
    implements DTOProcessor<PortfolioCompactListType>, Func1<PortfolioCompactListType, PortfolioCompactListType>
{
    @NonNull private final DTOProcessor<PortfolioCompactDTO> individualProcessor;

    public DTOProcessorPortfolioListReceived(@NonNull UserBaseKey userBaseKey)
    {
        this.individualProcessor = new DTOProcessorPortfolioReceived<>(userBaseKey);
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

    @Override public PortfolioCompactListType call(PortfolioCompactListType value)
    {
        return process(value);
    }
}
