package com.androidth.general.api.portfolio;

import android.support.annotation.Nullable;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;

public class PortfolioCompactDTOList extends BaseArrayList<PortfolioCompactDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public PortfolioCompactDTOList()
    {
        super();
    }
    //</editor-fold>

    @Nullable public PortfolioCompactDTO getDefaultPortfolio()
    {
        for (PortfolioCompactDTO portfolioCompactDTO : this)
        {
            if (portfolioCompactDTO.isDefault() && !portfolioCompactDTO.isFx())
            {
                return portfolioCompactDTO;
            }
        }
        return null;
    }

    @Nullable public PortfolioCompactDTO getDefaultFxPortfolio()
    {
        for (PortfolioCompactDTO portfolioCompactDTO : this)
        {
            if (portfolioCompactDTO.isDefault() && portfolioCompactDTO.isFx())
            {
                return portfolioCompactDTO;
            }
        }
        return null;
    }
}
