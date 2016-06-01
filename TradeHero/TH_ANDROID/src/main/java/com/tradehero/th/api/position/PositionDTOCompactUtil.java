package com.ayondo.academy.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.portfolio.PortfolioId;
import com.ayondo.academy.api.quote.QuoteDTO;
import java.util.List;

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
}
