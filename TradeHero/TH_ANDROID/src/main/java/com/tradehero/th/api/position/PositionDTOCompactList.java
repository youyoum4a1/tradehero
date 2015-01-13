package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
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
        for (PositionDTOCompact positionDTOCompact : this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                sum += positionDTOCompact.shares;
            }
        }
        return sum;
    }

    public double getShareAverageUsAmont(@Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null)
        {
            return 0;
        }

        double sum = 0;
        for (PositionDTOCompact positionDTOCompact : this)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                sum += positionDTOCompact.shares * positionDTOCompact.averagePriceRefCcy;
            }
        }
        return sum;
    }

    //<editor-fold desc="Net Sell Proceeds USD">
    public Double getNetSellProceedsUsd(
            Integer shareCount,
            QuoteDTO quoteDTO, // Do not add Nullable here as it is not ok with Proguard
            PortfolioId portfolioId,
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

    @Nullable public Double getSpentOnQuantityUsd(
            @NonNull Integer shareCount,
            @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        Double total = null;
        for (PositionDTOCompact positionDTO : this)
        {
            if (portfolioCompactDTO.id == positionDTO.portfolioId
                    && positionDTO.averagePriceRefCcy != null
                    && positionDTO.shares != null)
            {
                int localShareCount = Math.max(0, Math.min(shareCount, positionDTO.shares));
                total = (total == null ? 0 : total)
                        + positionDTO.averagePriceRefCcy * portfolioCompactDTO.getProperRefCcyToUsdRate() * localShareCount;
                shareCount -= localShareCount;
            }
        }
        if (shareCount != 0)
        {
            Timber.e(new IllegalArgumentException("Got wrong number of shares passed"), "Just reporting");
        }
        return total;
    }

    public Double getUnRealizedPLRefCcy(
            @NonNull QuoteDTO quoteDTO,
            @NonNull PortfolioCompactDTO portfolioCompactDTO, PositionDTOCompactList positionDTOCompacts)
    {
        double shareAverageUsAmont = getShareAverageUsAmont(portfolioCompactDTO.getPortfolioId());
        Integer shareCount = getShareCountIn(portfolioCompactDTO.getPortfolioId());
        double shareQuoteUsAmont = quoteDTO.bid * shareCount * quoteDTO.toUSDRate;
        double result = shareQuoteUsAmont - shareAverageUsAmont;
        return result;
    }
}
