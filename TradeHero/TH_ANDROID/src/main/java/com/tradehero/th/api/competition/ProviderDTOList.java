package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public class ProviderDTOList extends BaseProviderCompactDTOList<ProviderDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public ProviderDTOList()
    {
        super();
    }
    //</editor-fold>
}
