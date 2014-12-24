package com.tradehero.th.models.security;

import android.os.Bundle;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.ProviderFxListFragment;
import com.tradehero.th.fragments.competition.ProviderSecurityListFragment;
import com.tradehero.th.fragments.security.WarrantCompetitionPagerFragment;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProviderTradableSecuritiesHelper
{
    private DashboardNavigator navigator;

    @Inject ProviderTradableSecuritiesHelper(DashboardNavigator navigator)
    {
        this.navigator = navigator;
    }

    public void pushTradableSecuritiesList(Bundle args, OwnedPortfolioId ownedPortfolioId, PortfolioCompactDTO portfolioCompactDTO,
            ProviderId providerId)
    {
        ProviderSecurityListFragment.putProviderId(args, providerId);
        if (ownedPortfolioId != null)
        {
            ProviderSecurityListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }
        if (portfolioCompactDTO.portfolioType != null)
        {
            switch (portfolioCompactDTO.portfolioType)
            {
                case FX:
                    navigator.pushFragment(ProviderFxListFragment.class, args);
                    break;
                case WARRANT:
                    WarrantCompetitionPagerFragment.putProviderId(args, providerId);
                    navigator.pushFragment(WarrantCompetitionPagerFragment.class, args);
                    break;
                case STOCKS:
                default:
                    navigator.pushFragment(ProviderSecurityListFragment.class, args);
                    break;
            }
        }
        else
        {
            navigator.pushFragment(ProviderSecurityListFragment.class, args);
        }
    }
}
