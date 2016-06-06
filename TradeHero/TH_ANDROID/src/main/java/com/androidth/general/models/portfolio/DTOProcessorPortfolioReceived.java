package com.androidth.general.models.portfolio;

import android.support.annotation.NonNull;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.models.ThroughDTOProcessor;

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
