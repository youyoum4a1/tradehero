package com.tradehero.th.models.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.competition.ProviderFxListFragment;
import com.tradehero.th.fragments.security.ProviderSecurityListRxFragment;
import com.tradehero.th.fragments.security.WarrantCompetitionPagerFragment;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProviderTradableSecuritiesHelper
{
    private DashboardNavigator navigator;

    //<editor-fold desc="Constructors">
    @Inject ProviderTradableSecuritiesHelper(DashboardNavigator navigator)
    {
        this.navigator = navigator;
    }
    //</editor-fold>

    public void pushTradableSecuritiesList(
            @NonNull Bundle args,
            @Nullable OwnedPortfolioId ownedPortfolioId,
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull ProviderId providerId)
    {
        if (ownedPortfolioId != null)
        {
            BasePurchaseManagerFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }
        if (portfolioCompactDTO.assetClass != null)
        {
            switch (portfolioCompactDTO.assetClass)
            {
                case FX:
                    ProviderFxListFragment.putProviderId(args, providerId);
                    navigator.pushFragment(ProviderFxListFragment.class, args);
                    break;
                case WARRANT:
                    WarrantCompetitionPagerFragment.putProviderId(args, providerId);
                    navigator.pushFragment(WarrantCompetitionPagerFragment.class, args);
                    break;
                case STOCKS:
                default:
                    ProviderSecurityListRxFragment.putProviderId(args, providerId);
                    navigator.pushFragment(ProviderSecurityListRxFragment.class, args);
                    break;
            }
        }
        else
        {
            ProviderSecurityListRxFragment.putProviderId(args, providerId);
            navigator.pushFragment(ProviderSecurityListRxFragment.class, args);
        }
    }
}
