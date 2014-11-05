package com.tradehero.th.api.portfolio;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.th.api.users.UserBaseDTO;
import java.util.Collection;
import android.support.annotation.NonNull;

public class DisplayablePortfolioDTOList extends BaseArrayList<DisplayablePortfolioDTO>
{
    //<editor-fold desc="Constructors">
    public DisplayablePortfolioDTOList()
    {
        super();
    }

    public DisplayablePortfolioDTOList(@NonNull Collection<? extends DisplayablePortfolioDTO> c)
    {
        super(c);
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
