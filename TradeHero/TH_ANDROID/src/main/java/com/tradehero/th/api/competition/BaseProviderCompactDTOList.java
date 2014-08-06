package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public class BaseProviderCompactDTOList<ProviderDTOType extends ProviderCompactDTO>
        extends BaseArrayList<ProviderDTOType>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public BaseProviderCompactDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull public OwnedPortfolioIdList getAssociatedOwnedPortfolioIds(@NotNull UserBaseKey forUser)
    {
        OwnedPortfolioIdList ownedPortfolioIds = new OwnedPortfolioIdList();
        OwnedPortfolioId providerPortfolioId;
        for (ProviderCompactDTO providerDTO : this)
        {
            providerPortfolioId = providerDTO.getAssociatedOwnedPortfolioId(forUser);
            if (providerPortfolioId != null)
            {
                ownedPortfolioIds.add(providerPortfolioId);
            }
        }
        return ownedPortfolioIds;
    }

    @NotNull public ProviderIdList createKeys()
    {
        ProviderIdList list = new ProviderIdList();
        for (@NotNull ProviderCompactDTO providerDTO : this)
        {
            list.add(providerDTO.getProviderId());
        }
        return list;
    }
}
