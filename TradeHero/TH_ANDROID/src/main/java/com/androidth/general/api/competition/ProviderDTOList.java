package com.androidth.general.api.competition;

import android.support.annotation.NonNull;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.OwnedPortfolioIdList;

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
