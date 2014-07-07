package com.tradehero.th.api.portfolio;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.specific.ProviderSpecificResourcesDTO;
import com.tradehero.th.api.competition.specific.ProviderSpecificResourcesFactory;
import dagger.Lazy;
import javax.inject.Inject;

public class PortfolioDTOUtil
{
    @Inject Lazy<PortfolioCompactDTOUtil> portfolioCompactDTOUtil;
    @Inject Lazy<ProviderSpecificResourcesFactory> providerSpecificResourcesFactory;

    @Inject public PortfolioDTOUtil()
    {
        super();
    }

    public String getLongTitleType(Context context, PortfolioDTO portfolioDTO)
    {
        if (portfolioDTO != null)
        {
            if (portfolioDTO.isDefault())
            {
                return context.getString(R.string.portfolio_default_title);
            }
            if (portfolioDTO.isWatchlist)
            {
                return context.getString(R.string.watchlist_title);
            }
            if (portfolioDTO.providerId != null)
            {
                ProviderSpecificResourcesDTO resourcesDTO = providerSpecificResourcesFactory.get().createResources(portfolioDTO.getProviderIdKey());
                if (resourcesDTO != null && resourcesDTO.competitionPortfolioTitleResId > 0)
                {
                    return context.getString(resourcesDTO.competitionPortfolioTitleResId);
                }
                else if (portfolioDTO.title != null)
                {
                    return portfolioDTO.title;
                }
                return context.getString(R.string.competition_portfolio_unsure);
            }
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
