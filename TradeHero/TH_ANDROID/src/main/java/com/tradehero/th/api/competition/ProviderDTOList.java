package com.ayondo.academy.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.portfolio.OwnedPortfolioIdList;

public class ProviderDTOList extends BaseArrayList<ProviderDTO>
    implements DTO
{
    @NonNull public OwnedPortfolioIdList getAssociatedOwnedPortfolioIds()
    {
        OwnedPortfolioIdList ownedPortfolioIds = new OwnedPortfolioIdList();
        OwnedPortfolioId providerPortfolioId;
        for (ProviderDTO providerDTO : this)
        {
            providerPortfolioId = providerDTO.getAssociatedOwnedPortfolioId();
            if (providerPortfolioId != null)
            {
                ownedPortfolioIds.add(providerPortfolioId);
            }
        }
        return ownedPortfolioIds;
    }
}
