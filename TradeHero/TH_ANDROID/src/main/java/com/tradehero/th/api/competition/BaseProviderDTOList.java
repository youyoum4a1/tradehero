package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import android.support.annotation.NonNull;

public class BaseProviderDTOList<ProviderDTOType extends ProviderDTO>
        extends BaseArrayList<ProviderDTOType>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public BaseProviderDTOList()
    {
        super();
    }
    //</editor-fold>

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

    @NonNull public ProviderIdList createKeys()
    {
        ProviderIdList list = new ProviderIdList();
        for (ProviderDTO providerDTO : this)
        {
            list.add(providerDTO.getProviderId());
        }
        return list;
    }
}
