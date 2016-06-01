package com.ayondo.academy.models.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.billing.BasePurchaseManagerFragment;
import com.ayondo.academy.fragments.competition.ProviderFxListFragment;
import com.ayondo.academy.fragments.security.ProviderSecurityListRxFragment;
import com.ayondo.academy.fragments.security.WarrantCompetitionPagerFragment;

public class ProviderTradableSecuritiesHelper
{
    public static void pushTradableSecuritiesList(
            @NonNull DashboardNavigator navigator,
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
