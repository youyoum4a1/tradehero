package com.tradehero.th.api.position;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.io.Serializable;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class PositionDTOCompactList extends BaseArrayList<PositionDTOCompact> implements Serializable
{
    //<editor-fold desc="Constructors">
    public PositionDTOCompactList()
    {
        super();
    }
    //</editor-fold>

    public Integer getShareCountIn(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null || portfolioId.key == null)
        {
            return null;
        }

        int sum = 0;
        for (PositionDTOCompact positionDTOCompact : this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                sum += positionDTOCompact.shares;
            }
        }
        return sum;
    }

    public Integer getPositionId(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null || portfolioId.key == null)
        {
            return null;
        }
        int position = 0;
        for (PositionDTOCompact positionDTOCompact : this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                position = positionDTOCompact.id;
                return position;
            }
        }
        return position;
    }

    //获取成本均价
    public Double getAvPrice(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null || portfolioId.key == null)
        {
            return null;
        }

        double avprice = 0;
        for (PositionDTOCompact positionDTOCompact : this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                if (positionDTOCompact.fxRate == null || positionDTOCompact.fxRate == 0)
                {
                    avprice = positionDTOCompact.averagePriceRefCcy;
                }
                else
                {
                    avprice = positionDTOCompact.averagePriceRefCcy / positionDTOCompact.fxRate;
                }

                return avprice;
            }
        }
        return avprice;
    }

    //<editor-fold desc="Net Sell Proceeds USD">

    /**
     * If it returns a negative number it means it will eat into the cash available.
     */
    public Double getMaxNetSellProceedsUsd(
            @Nullable QuoteDTO quoteDTO,
            @Nullable PortfolioId portfolioId,
            boolean includeTransactionCostUsd)
    {
        return getMaxNetSellProceedsUsd(
                quoteDTO,
                portfolioId,
                includeTransactionCostUsd,
                SecurityUtils.DEFAULT_TRANSACTION_COST_USD);
    }

    /**
     * If it returns a negative number it means it will eat into the cash available.
     */
    public Double getMaxNetSellProceedsUsd(
            @Nullable QuoteDTO quoteDTO,
            @Nullable PortfolioId portfolioId,
            boolean includeTransactionCostUsd,
            double txnCostUsd)
    {
        return getNetSellProceedsUsd(
                getShareCountIn(portfolioId),
                quoteDTO,
                portfolioId,
                includeTransactionCostUsd,
                txnCostUsd);
    }

    public Double getNetSellProceedsUsd(
            @Nullable Integer shareCount,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PortfolioId portfolioId,
            boolean includeTransactionCostUsd)
    {
        return getNetSellProceedsUsd(
                shareCount,
                quoteDTO,
                portfolioId,
                includeTransactionCostUsd,
                SecurityUtils.DEFAULT_TRANSACTION_COST_USD);
    }

    public Double getNetSellProceedsUsd(
            @Nullable Integer shareCount,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PortfolioId portfolioId,
            boolean includeTransactionCostUsd,
            double txnCostUsd)
    {
        if (shareCount == null || quoteDTO == null || portfolioId == null || portfolioId.key == null)
        {
            return null;
        }
        Double bidUsd = quoteDTO.getBidUSD();
        if (bidUsd == null)
        {
            return null;
        }
        return shareCount * bidUsd - (includeTransactionCostUsd ? txnCostUsd : 0);
    }
    //</editor-fold>

    public Double getTotalSpent(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null)
        {
            return null;
        }
        Double total = null;
        for (PositionDTOCompact positionDTO : this)
        {
            if (portfolioId.key.equals(positionDTO.portfolioId)
                    && positionDTO.averagePriceRefCcy != null
                    && positionDTO.shares != null)
            {
                total = (total == null ? 0 : total) + positionDTO.averagePriceRefCcy * positionDTO.shares;
            }
        }
        return total;
    }

    public Double getSpentOnQuantity(
            @Nullable Integer shareCount,
            @Nullable PortfolioId portfolioId)
    {
        if (shareCount == null || portfolioId == null)
        {
            return null;
        }
        Double total = null;
        for (PositionDTOCompact positionDTO : this)
        {
            if (portfolioId.key.equals(positionDTO.portfolioId)
                    && positionDTO.averagePriceRefCcy != null
                    && positionDTO.shares != null)
            {
                int localShareCount = Math.max(0, Math.min(shareCount, positionDTO.shares));
                total = (total == null ? 0 : total) + positionDTO.averagePriceRefCcy * localShareCount;
                shareCount -= localShareCount;
            }
        }
        if (shareCount != 0)
        {
            Timber.e(new IllegalArgumentException("Got wrong number of shares passed"), "Just reporting");
        }
        return total;
    }

    //<editor-fold desc="Max Sellable Shares">
    public Integer getMaxSellableShares(
            QuoteDTO quoteDTO,
            PortfolioCompactDTO portfolioCompactDTO)
    {
        return getMaxSellableShares(quoteDTO, portfolioCompactDTO, true);
    }

    public Integer getMaxSellableShares(
            QuoteDTO quoteDTO,
            PortfolioCompactDTO portfolioCompactDTO,
            boolean includeTransactionCost)
    {
        if (quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        double txnCostUsd = portfolioCompactDTO.getProperTxnCostUsd();
        Integer shareCount = getShareCountIn(portfolioCompactDTO.getPortfolioId());
        Double netSellProceedsUsd = getMaxNetSellProceedsUsd(quoteDTO, portfolioCompactDTO.getPortfolioId(), includeTransactionCost, txnCostUsd);
        if (netSellProceedsUsd == null)
        {
            return null;
        }
        netSellProceedsUsd += portfolioCompactDTO.getCashBalanceUsd();

        // If we are underwater after a sell, we cannot sell
        return netSellProceedsUsd < 0 ? 0 : shareCount;
    }
    //</editor-fold>
}
