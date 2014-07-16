package com.tradehero.th.api.portfolio;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        for (@NotNull PortfolioCompactDTO portfolioCompactDTO : this)
        {
            if (portfolioCompactDTO.isDefault())
            {
                return portfolioCompactDTO;
            }
        }
        return null;
    }

}
