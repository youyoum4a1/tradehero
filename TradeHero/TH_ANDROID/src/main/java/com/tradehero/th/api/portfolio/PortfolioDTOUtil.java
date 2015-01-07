package com.tradehero.th.api.portfolio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import dagger.Lazy;
import javax.inject.Inject;

public class PortfolioDTOUtil
{
    @NonNull private final Lazy<PortfolioCompactDTOUtil> portfolioCompactDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioDTOUtil(@NonNull Lazy<PortfolioCompactDTOUtil> portfolioCompactDTOUtil)
    {
        super();
        this.portfolioCompactDTOUtil = portfolioCompactDTOUtil;
    }
    //</editor-fold>

    @Nullable public String getLongTitle(@NonNull Context context, @NonNull PortfolioDTO portfolioDTO)
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

    public String getLongSubTitle(Context context, PortfolioDTO portfolioDTO)
    {
        return getLongSubTitle(context, portfolioDTO, null);
    }

    public String getLongSubTitle(Context context, PortfolioDTO portfolioDTO, String userName)
    {
        return portfolioCompactDTOUtil.get().getPortfolioSubtitle(context, portfolioDTO, userName);
    }
}
