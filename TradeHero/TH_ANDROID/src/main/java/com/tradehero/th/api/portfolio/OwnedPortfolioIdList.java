package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class OwnedPortfolioIdList extends DTOKeyIdList<OwnedPortfolioId>
{
    //<editor-fold desc="Constructors">
    public OwnedPortfolioIdList()
    {
        super();
    }

    public OwnedPortfolioIdList(int capacity)
    {
        super(capacity);
    }

    public OwnedPortfolioIdList(Collection<? extends OwnedPortfolioId> collection)
    {
        super(collection);
    }
    //</editor-fold>

    public PortfolioIdList getPortfolioIds()
    {
        PortfolioIdList ids = new PortfolioIdList();
        for (OwnedPortfolioId ownedPortfolioId: this)
        {
            ids.add(ownedPortfolioId.getPortfolioIdKey());
        }
        return ids;
    }
}
