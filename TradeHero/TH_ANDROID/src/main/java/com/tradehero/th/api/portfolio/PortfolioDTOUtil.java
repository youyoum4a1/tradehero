package com.tradehero.th.api.portfolio;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;

public class PortfolioDTOUtil
{
    @Nullable public static String getLongTitle(@NonNull Resources resources, @NonNull PortfolioDTO portfolioDTO)
    {
        if (portfolioDTO.isWatchlist)
        {
            return resources.getString(R.string.watchlist_title);
        }
        if (portfolioDTO.title != null)
        {
            return portfolioDTO.title;
        }
        if (portfolioDTO.providerId != null)
        {
            return resources.getString(R.string.competition_portfolio_unsure);
        }
        return null;
    }

    @Nullable public static String getLongSubTitle(@NonNull Resources resources, @Nullable PortfolioDTO portfolioDTO)
    {
        return getLongSubTitle(resources, portfolioDTO, null);
    }

    @Nullable public static String getLongSubTitle(@NonNull Resources resources, @Nullable PortfolioDTO portfolioDTO, @Nullable String userName)
    {
        return PortfolioCompactDTOUtil.getPortfolioSubtitle(resources, portfolioDTO, userName);
    }
}
