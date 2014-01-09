package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 7:43 PM To change this template use File | Settings | File Templates. */
public class PositionDTOCompactList extends ArrayList<PositionDTOCompact>
{
    public static final String TAG = PositionDTOCompactList.class.getSimpleName();

    public int getShareCount(PortfolioId portfolioId)
    {
        int total = 0;

        for (PositionDTOCompact positionDTOCompact: this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                total += positionDTOCompact.shares;
            }
        }

        return total;
    }

    public Integer getMaxSellableShares(QuoteDTO quoteDTO, PortfolioId portfolioId, UserProfileDTO userProfileDTO)
    {
        return getMaxSellableShares(quoteDTO, portfolioId, userProfileDTO, true);
    }

    public Integer getMaxSellableShares(QuoteDTO quoteDTO, PortfolioId portfolioId, UserProfileDTO userProfileDTO,
            boolean includeTransactionCost)
    {
        Integer total = getShareCount(portfolioId);
        Double netProceeds = getMaxSellProceeds(quoteDTO, portfolioId);

        if (userProfileDTO != null && userProfileDTO.portfolio != null)
        {
            if (netProceeds == null)
            {
                netProceeds = 0d;
            }
            netProceeds += userProfileDTO.portfolio.cashBalance; // TODO Care about the portfolio given its id
        }

        if (includeTransactionCost && netProceeds != null && netProceeds < SecurityUtils.DEFAULT_TRANSACTION_COST)
        {
            total = 0;
        }
        else if (includeTransactionCost && netProceeds == null)
        {
            total = null;
        }

        return total;
    }

    public Double getMaxSellProceeds(QuoteDTO quoteDTO, PortfolioId portfolioId)
    {
        if (quoteDTO == null || quoteDTO.bid == null || quoteDTO.toUSDRate == null ||
                portfolioId == null || portfolioId.key == null)
        {
            return null;
        }

        double netProceeds = 0;

        for (PositionDTOCompact positionDTOCompact: this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                netProceeds += positionDTOCompact.shares * quoteDTO.bid * quoteDTO.toUSDRate;
            }
        }

        return netProceeds;
    }
}
