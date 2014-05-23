package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import java.util.List;

public class SecurityPositionDetailDTO implements DTO
{
    public SecurityCompactDTO security;
    public PositionDTOCompactList positions;
    //public PositionDTOCompact position; // This is a backward compatible element. Do not add back
    public PortfolioDTO portfolio;
    public List<ProviderDTO> providers;
    public int firstTradeAllTime;

    public SecurityPositionDetailDTO()
    {
    }

    public SecurityPositionDetailDTO(SecurityCompactDTO security, PositionDTOCompactList positions,
            PortfolioDTO portfolio, List<ProviderDTO> providers, int firstTradeAllTime)
    {
        this.security = security;
        this.positions = positions;
        this.portfolio = portfolio;
        this.providers = providers;
        this.firstTradeAllTime = firstTradeAllTime;
    }

    public SecurityId getSecurityId()
    {
        if (security == null)
        {
            return null;
        }
        return security.getSecurityId();
    }
}
