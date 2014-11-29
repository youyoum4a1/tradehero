package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.utils.SecurityUtils;
import timber.log.Timber;

public class PositionDTOCompactList extends BaseArrayList<PositionDTOCompact>
{
    //<editor-fold desc="Constructors">
    public PositionDTOCompactList()
    {
        super();
    }
    //</editor-fold>

    public Integer getShareCountIn(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null)
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

    //<editor-fold desc="Net Sell Proceeds USD">
    /**
     * If it returns a negative number it means it will eat into the cash available.
     * @param quoteDTO
     * @param portfolioId
     * @param includeTransactionCostUsd
     * @return
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
     * @param quoteDTO
     * @param portfolioId
     * @param includeTransactionCostUsd
     * @param txnCostUsd
     * @return
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

    public Double getNetSellProceedsRefCcy(
            @Nullable Integer shareCount,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PortfolioId portfolioId,
            boolean includeTransactionCostUsd)
    {
        return getNetSellProceedsRefCcy(
                shareCount,
                quoteDTO,
                portfolioId,
                includeTransactionCostUsd,
                SecurityUtils.DEFAULT_TRANSACTION_COST_USD);
    }

    public Double getNetSellProceedsRefCcy(
            @Nullable Integer shareCount,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PortfolioId portfolioId,
            boolean includeTransactionCostUsd,
            double txnCostUsd)
    {
        Double netProceedsUsd = getNetSellProceedsUsd(shareCount, quoteDTO, portfolioId, includeTransactionCostUsd, txnCostUsd);
        if (netProceedsUsd == null || quoteDTO == null || quoteDTO.toUSDRate == null || quoteDTO.toUSDRate == 0)
        {
            return null;
        }
        return netProceedsUsd / quoteDTO.toUSDRate;
    }
    //</editor-fold>

    public Double getTotalSpentUsd(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null)
        {
            return null;
        }
        Double total = null;
        for (PositionDTOCompact positionDTO: this)
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

    @Nullable public Double getSpentOnQuantityUsd(
            @NonNull Integer shareCount,
            @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        Double total = null;
        for (PositionDTOCompact positionDTO: this)
        {
            if (portfolioCompactDTO.id == positionDTO.portfolioId
                    && positionDTO.averagePriceRefCcy != null
                    && positionDTO.shares != null)
            {
                int localShareCount = Math.max(0, Math.min(shareCount, positionDTO.shares));
                total = (total == null ? 0 : total) + positionDTO.averagePriceRefCcy * portfolioCompactDTO.getProperRefCcyToUsdRate() * localShareCount;
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
