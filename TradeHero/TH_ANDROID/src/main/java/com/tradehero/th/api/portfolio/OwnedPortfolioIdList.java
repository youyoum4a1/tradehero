package com.tradehero.th.api.portfolio;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class OwnedPortfolioIdList extends DTOKeyIdList<OwnedPortfolioId>
{
    //<editor-fold desc="Constructors">
    public OwnedPortfolioIdList()
    {
        super();
    }

    public OwnedPortfolioIdList(
            @NonNull Collection<? extends OwnedPortfolioId> ownedPortfolioIds)
    {
        super(ownedPortfolioIds);
    }

    public OwnedPortfolioIdList(
            @NonNull Collection<? extends PortfolioCompactDTO> portfolioCompactDTOs,
            @SuppressWarnings("UnusedParameters") @NonNull PortfolioCompactDTO typeQualifier)
    {
        super();
        for (PortfolioCompactDTO compactDTO : portfolioCompactDTOs)
        {
            add(compactDTO.getOwnedPortfolioId());
        }
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
