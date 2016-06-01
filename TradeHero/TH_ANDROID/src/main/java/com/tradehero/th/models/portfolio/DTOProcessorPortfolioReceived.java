package com.ayondo.academy.models.portfolio;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.models.ThroughDTOProcessor;

public class DTOProcessorPortfolioReceived<PortfolioCompactType extends PortfolioCompactDTO>
        extends ThroughDTOProcessor<PortfolioCompactType>
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
}
