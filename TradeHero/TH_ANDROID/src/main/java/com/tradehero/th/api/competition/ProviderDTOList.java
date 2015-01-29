package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;

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
