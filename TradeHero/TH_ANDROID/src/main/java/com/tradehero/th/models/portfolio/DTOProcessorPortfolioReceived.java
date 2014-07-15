package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorPortfolioReceived<PortfolioCompactType extends PortfolioCompactDTO>
        implements DTOProcessor<PortfolioCompactType>
{
    @NotNull private final UserBaseKey userBaseKey;

    //<editor-fold desc="Constructors">
    public DTOProcessorPortfolioReceived(@NotNull UserBaseKey userBaseKey)
    {
        this.userBaseKey = userBaseKey;
    }
    //</editor-fold>

    @Override public PortfolioCompactType process(PortfolioCompactType value)
    {
        value.userId = userBaseKey.key;
        return value;
    }
}
