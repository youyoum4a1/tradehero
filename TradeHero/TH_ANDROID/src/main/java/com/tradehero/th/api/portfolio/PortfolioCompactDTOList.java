package com.tradehero.th.api.portfolio;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
            if (portfolioCompactDTO.isDefault())
            {
                return portfolioCompactDTO;
            }
        }
        return null;
    }

}
