package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import java.util.List;
import timber.log.Timber;

public class PositionDTOCompactUtil
{
    public static double getShareAverageUsAmount(@NonNull List<? extends PositionDTOCompact> dtoCompacts, @Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null)
        {
            return 0;
        }

        double sum = 0;
        for (PositionDTOCompact positionDTOCompact : dtoCompacts)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                sum += positionDTOCompact.shares * positionDTOCompact.averagePriceRefCcy;
            }
        }
        return sum;
    }

    @Nullable public static Integer getShareCountIn(@NonNull List<? extends PositionDTOCompact> dtoCompacts, @Nullable PortfolioId portfolioId)
    {
        if (portfolioId == null)
        {
            return null;
        }

        int sum = 0;
        for (PositionDTOCompact positionDTOCompact : dtoCompacts)
        {
            if (positionDTOCompact.portfolioId == portfolioId.key && positionDTOCompact.shares != null)
            {
                sum += positionDTOCompact.shares;
            }
        }
        return sum;
    }

    @Nullable public static Double getUnRealizedPLRefCcy(
            @NonNull List<? extends PositionDTOCompact> dtoCompacts,
            @NonNull QuoteDTO quoteDTO,
            @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        double shareAverageUsAmount = getShareAverageUsAmount(dtoCompacts, portfolioCompactDTO.getPortfolioId());
        Integer shareCount = getShareCountIn(dtoCompacts, portfolioCompactDTO.getPortfolioId());
        if (shareCount == null || quoteDTO.bid == null)
        {
            return null;
        }
        double shareQuoteUsAmount = quoteDTO.bid * shareCount * quoteDTO.toUSDRate;
        return shareQuoteUsAmount - shareAverageUsAmount;
    }

    @Nullable public static Double getSpentOnQuantityUsd(
            @NonNull List<? extends PositionDTOCompact> dtoCompacts,
            @NonNull Integer shareCount,
            @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        Double total = null;
        for (PositionDTOCompact positionDTO : dtoCompacts)
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

    //<editor-fold desc="Net Sell Proceeds USD">
    @Nullable public static Double getNetSellProceedsUsd(
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
}
