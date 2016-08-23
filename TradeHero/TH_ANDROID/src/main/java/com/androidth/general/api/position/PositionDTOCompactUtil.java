package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioId;
import com.androidth.general.api.quote.QuoteDTO;
import com.androidth.general.fragments.security.LiveQuoteDTO;

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
            @NonNull LiveQuoteDTO quoteDTO,
            @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        double shareAverageUsAmount = getShareAverageUsAmount(dtoCompacts, portfolioCompactDTO.getPortfolioId());
        Integer shareCount = getShareCountIn(dtoCompacts, portfolioCompactDTO.getPortfolioId());
        if (shareCount == null || quoteDTO.getBidPrice() == null)
        {
            return null;
        }
        double shareQuoteUsAmount = quoteDTO.getBidPrice() * shareCount * quoteDTO.getUsdRate();
        return shareQuoteUsAmount - shareAverageUsAmount;
    }
}
