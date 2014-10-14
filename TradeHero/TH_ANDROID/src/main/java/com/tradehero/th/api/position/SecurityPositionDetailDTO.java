package com.tradehero.th.api.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import org.jetbrains.annotations.Nullable;

public class SecurityPositionDetailDTO implements DTO
{
    public SecurityCompactDTO security;
    @Nullable public PositionDTOCompactList positions;
    //public PositionDTOCompact position; // This is a backward compatible element. Do not add back
    @Deprecated public PortfolioDTO portfolio; // Does it always comes back as null
    @Nullable public ProviderDTOList providers;
    public int firstTradeAllTime;

    //<editor-fold desc="Constructors">
    public SecurityPositionDetailDTO()
    {
    }

    public SecurityPositionDetailDTO(SecurityCompactDTO security, @Nullable PositionDTOCompactList positions,
            PortfolioDTO portfolio, @Nullable ProviderDTOList providers, int firstTradeAllTime)
    {
        this.security = security;
        this.positions = positions;
        this.portfolio = portfolio;
        this.providers = providers;
        this.firstTradeAllTime = firstTradeAllTime;
    }
    //</editor-fold>

    @Nullable public SecurityId getSecurityId()
    {
        if (security == null)
        {
            return null;
        }
        return security.getSecurityId();
    }

    @Nullable public OwnedPortfolioIdList getProviderAssociatedOwnedPortfolioIds()
    {
        if (providers == null)
        {
            return null;
        }
        return providers.getAssociatedOwnedPortfolioIds();
    }
}
