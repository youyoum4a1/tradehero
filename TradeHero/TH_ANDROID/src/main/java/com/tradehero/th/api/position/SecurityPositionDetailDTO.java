package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;

public class SecurityPositionDetailDTO implements DTO
{
    public SecurityCompactDTO security;
    public PositionDTOCompactList positions;
    //public PositionDTOCompact position; // This is a backward compatible element. Do not add back
    public PortfolioDTO portfolio;
    public ProviderDTOList providers;
    public int firstTradeAllTime;

    //<editor-fold desc="Constructors">
    public SecurityPositionDetailDTO()
    {
    }

    public SecurityPositionDetailDTO(SecurityCompactDTO security, PositionDTOCompactList positions,
            PortfolioDTO portfolio, ProviderDTOList providers, int firstTradeAllTime)
    {
        this.security = security;
        this.positions = positions;
        this.portfolio = portfolio;
        this.providers = providers;
        this.firstTradeAllTime = firstTradeAllTime;
    }
    //</editor-fold>

    public SecurityId getSecurityId()
    {
        if (security == null)
        {
            return null;
        }
        return security.getSecurityId();
    }

    public OwnedPortfolioIdList getProviderAssociatedOwnedPortfolioIds(UserBaseKey forUser)
    {
        if (providers == null)
        {
            return null;
        }
        return providers.getAssociatedOwnedPortfolioIds(forUser);
    }
}
