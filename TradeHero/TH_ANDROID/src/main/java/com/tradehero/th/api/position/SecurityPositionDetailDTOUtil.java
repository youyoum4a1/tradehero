package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import javax.inject.Inject;

public class SecurityPositionDetailDTOUtil
{
    @Inject protected PositionDTOCompactListUtil positionDTOCompactListUtil;

    @Inject public SecurityPositionDetailDTOUtil()
    {
        super();
    }

    public Integer getMaxSellableShares(SecurityPositionDetailDTO securityPositionDetailDTO, QuoteDTO quoteDTO, PortfolioCompactDTO portfolioCompactDTO)
    {
        if (securityPositionDetailDTO != null && securityPositionDetailDTO.positions != null && portfolioCompactDTO != null)
        {
            return securityPositionDetailDTO.positions.getMaxSellableShares(quoteDTO, portfolioCompactDTO);
        }
        return null;
    }

    public Double projectedPLValue(SecurityPositionDetailDTO securityPositionDetailDTO, QuoteDTO quoteDTO, Integer shareQuantity)
    {
        return projectedPLValue(securityPositionDetailDTO, quoteDTO, shareQuantity, true);
    }

    public Double projectedPLValue(SecurityPositionDetailDTO securityPositionDetailDTO, QuoteDTO quoteDTO, Integer shareQuantity, boolean includeTransactionCost)
    {
        if (securityPositionDetailDTO != null)
        {
            return positionDTOCompactListUtil.projectedPLValue(
                    securityPositionDetailDTO.positions,
                    quoteDTO, shareQuantity, includeTransactionCost);
        }
        else
        {
            return null;
        }
    }
}
