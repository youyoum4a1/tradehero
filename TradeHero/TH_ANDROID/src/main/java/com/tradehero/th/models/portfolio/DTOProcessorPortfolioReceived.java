package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import android.support.annotation.NonNull;
import rx.functions.Func1;

public class DTOProcessorPortfolioReceived<PortfolioCompactType extends PortfolioCompactDTO>
        implements DTOProcessor<PortfolioCompactType>, Func1<PortfolioCompactType, PortfolioCompactType>
{
    @NonNull private final UserBaseKey userBaseKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorPortfolioReceived(@NonNull UserBaseKey userBaseKey)
    {
        this.userBaseKey = userBaseKey;
    }
    //</editor-fold>

    @Override public PortfolioCompactType process(@NonNull PortfolioCompactType value)
    {
        value.userId = userBaseKey.key;
        return value;
    }

    @Override public PortfolioCompactType call(@NonNull PortfolioCompactType portfolioCompactType)
    {
        return process(portfolioCompactType);
    }
}
