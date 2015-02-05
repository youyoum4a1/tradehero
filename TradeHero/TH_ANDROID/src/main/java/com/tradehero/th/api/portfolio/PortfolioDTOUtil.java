package com.tradehero.th.api.portfolio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;

public class PortfolioDTOUtil
{
    @Nullable public static String getLongTitle(@NonNull Context context, @NonNull PortfolioDTO portfolioDTO)
    {
        if (portfolioDTO.isWatchlist)
        {
            return context.getString(R.string.watchlist_title);
        }
        if (portfolioDTO.title != null)
        {
            return portfolioDTO.title;
        }
        if (portfolioDTO.providerId != null)
        {
            return context.getString(R.string.competition_portfolio_unsure);
        }
        return null;
    }

    @Nullable public static String getLongSubTitle(@NonNull Context context, @Nullable PortfolioDTO portfolioDTO)
    {
        return getLongSubTitle(context, portfolioDTO, null);
    }

    @Nullable public static String getLongSubTitle(@NonNull Context context, @Nullable PortfolioDTO portfolioDTO, @Nullable String userName)
    {
        return PortfolioCompactDTOUtil.getPortfolioSubtitle(context, portfolioDTO, userName);
    }
}
