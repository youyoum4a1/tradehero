package com.tradehero.th.api.position;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:24 PM To change this template use File | Settings | File Templates. */
public class SecurityPositionDetailDTO
{
    public SecurityCompactDTO security;
    public List<PositionDTOCompact> positions;
    public PositionDTOCompact position;
    public PortfolioDTO portfolio;
    public List<ProviderDTO> providers;
    public int firstTradeAllTime;

    public SecurityPositionDetailDTO()
    {
    }

    public SecurityPositionDetailDTO(SecurityCompactDTO security, List<PositionDTOCompact> positions, PositionDTOCompact position,
            PortfolioDTO portfolio, List<ProviderDTO> providers, int firstTradeAllTime)
    {
        this.security = security;
        this.positions = positions;
        this.position = position;
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
