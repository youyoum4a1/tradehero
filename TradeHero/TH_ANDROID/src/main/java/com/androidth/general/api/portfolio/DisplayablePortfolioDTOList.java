package com.androidth.general.api.portfolio;

import android.support.annotation.NonNull;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.api.users.UserBaseDTO;
import java.util.Collection;

public class DisplayablePortfolioDTOList extends BaseArrayList<DisplayablePortfolioDTO>
{
    //<editor-fold desc="Constructors">
    public DisplayablePortfolioDTOList()
    {
        super();
    }

    public DisplayablePortfolioDTOList(@NonNull UserBaseDTO userBaseDTO,
            @NonNull Collection<? extends PortfolioDTO> portfolioDTOs)
    {
        super();
        for (PortfolioDTO portfolioDTO : portfolioDTOs)
        {
            add(new DisplayablePortfolioDTO(portfolioDTO.getOwnedPortfolioId(), userBaseDTO, portfolioDTO));
        }
    }
    //</editor-fold>
}
