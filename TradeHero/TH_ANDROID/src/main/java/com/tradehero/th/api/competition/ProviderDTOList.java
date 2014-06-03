package com.tradehero.th.api.competition;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.ArrayList;
import java.util.Collection;

public class ProviderDTOList extends ArrayList<ProviderDTO>
{
    //<editor-fold desc="Constructors">
    public ProviderDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public ProviderDTOList()
    {
        super();
    }

    public ProviderDTOList(Collection<? extends ProviderDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    public ProviderIdList getIds()
    {
        ProviderIdList ids = new ProviderIdList();
        for (ProviderDTO providerDTO : this)
        {
            if (providerDTO != null)
            {
                ids.add(providerDTO.getProviderId());
            }
        }
        return ids;
    }

    public OwnedPortfolioIdList getAssociatedOwnedPortfolioIds(UserBaseKey forUser)
    {
        OwnedPortfolioIdList ownedPortfolioIds = new OwnedPortfolioIdList();
        OwnedPortfolioId providerPortfolioId;
        for (ProviderDTO providerDTO : this)
        {
            providerPortfolioId = providerDTO.getAssociatedOwnedPortfolioId(forUser);
            if (providerPortfolioId != null)
            {
                ownedPortfolioIds.add(providerPortfolioId);
            }
        }
        return ownedPortfolioIds;
    }
}
