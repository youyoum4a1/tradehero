package com.androidth.general.api.portfolio;

import android.support.annotation.Nullable;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;
import java.util.Collection;

public class PortfolioDTOList extends BaseArrayList<PortfolioDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public PortfolioDTOList()
    {
        super();
    }

    public PortfolioDTOList(Collection<? extends PortfolioDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    @Nullable public PortfolioDTO getDefaultPortfolio()
    {
        for (PortfolioDTO portfolioDTO : this)
        {
            if (portfolioDTO.isDefault())
            {
                return portfolioDTO;
            }
        }
        return null;
    }

}
