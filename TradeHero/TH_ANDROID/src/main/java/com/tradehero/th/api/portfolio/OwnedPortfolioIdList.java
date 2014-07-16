package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class OwnedPortfolioIdList extends DTOKeyIdList<OwnedPortfolioId>
{
    //<editor-fold desc="Constructors">
    public OwnedPortfolioIdList()
    {
        super();
    }

    public OwnedPortfolioIdList(
            @NotNull UserBaseKey userBaseKey,
            @NotNull Collection<? extends PortfolioCompactDTO> portfolioCompactDTOs)
    {
        for (PortfolioCompactDTO portfolioCompactDTO : portfolioCompactDTOs)
        {
            add(new OwnedPortfolioId(userBaseKey.key, portfolioCompactDTO.id));
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
