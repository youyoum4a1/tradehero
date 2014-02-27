package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 7:43 PM To change this template use File | Settings | File Templates. */
public class PositionDTOCompactList extends ArrayList<PositionDTOCompact>
{
    public static final String TAG = PositionDTOCompactList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public PositionDTOCompactList(int capacity)
    {
        super(capacity);
    }

    public PositionDTOCompactList()
    {
        super();
    }

    public PositionDTOCompactList(Collection<? extends PositionDTOCompact> collection)
    {
        super(collection);
    }
    //</editor-fold>

    public Integer getShareCountIn(PortfolioId portfolioId)
    {
        if (portfolioId == null || portfolioId.key == null)
        {
            return null;
        }

        int sum = 0;
        for (PositionDTOCompact positionDTOCompact: this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                sum += positionDTOCompact.shares;
            }
        }
        return sum;
    }

    /**
     * If it returns a negative number it means it will eat into the cash available.
     * @param quoteDTO
     * @param portfolioId
     * @param includeTransactionCost
     * @return
     */
    public Double getMaxNetSellProceedsUsd(QuoteDTO quoteDTO, PortfolioId portfolioId, boolean includeTransactionCost)
    {
        if (quoteDTO == null || portfolioId == null || portfolioId.key == null)
        {
            return null;
        }
        Double bidUsd = quoteDTO.getBidUSD();
        Integer shareCount = getShareCountIn(portfolioId);
        if (bidUsd == null || shareCount == null)
        {
            return null;
        }
        return shareCount * bidUsd - (includeTransactionCost ? SecurityUtils.DEFAULT_TRANSACTION_COST : 0);
    }

    public Integer getMaxSellableShares(QuoteDTO quoteDTO, PortfolioCompactDTO portfolioCompactDTO)
    {
        return getMaxSellableShares(quoteDTO, portfolioCompactDTO, true);
    }

    public Integer getMaxSellableShares(QuoteDTO quoteDTO, PortfolioCompactDTO portfolioCompactDTO, boolean includeTransactionCost)
    {
        if (quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Integer shareCount = getShareCountIn(portfolioCompactDTO.getPortfolioId());
        Double netSellProceedsUsd = getMaxNetSellProceedsUsd(quoteDTO, portfolioCompactDTO.getPortfolioId(), includeTransactionCost);
        if (netSellProceedsUsd == null)
        {
            return null;
        }
        netSellProceedsUsd += portfolioCompactDTO.getCashBalanceUsd();

        // If we are underwater after a sell, we cannot sell
        return netSellProceedsUsd < 0 ? 0 : shareCount;
    }
}
