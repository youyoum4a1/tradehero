package com.tradehero.th.api.position;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
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
}
