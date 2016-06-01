package com.ayondo.academy.fragments.position;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.annotations.VisibleForTesting;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.models.security.ProviderTradableSecuritiesHelper;
import javax.inject.Inject;

//TODO need refactor by alex
public class CompetitionLeaderboardPositionListFragment extends TabbedPositionListFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardPositionListFragment.class + ".providerId";

    protected ProviderId providerId;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @Nullable private static ProviderId getProviderId(@NonNull Bundle args)
    {
        Bundle bundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID);
        if (bundle == null)
        {
            return null;
        }
        return new ProviderId(bundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
    }

    protected void pushTrendingFragment()
    {
        Bundle args = new Bundle();
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId(getArguments());
        ProviderTradableSecuritiesHelper.pushTradableSecuritiesList(navigator.get(), args, ownedPortfolioId, portfolioDTO, providerId);
    }

    @VisibleForTesting public ProviderId getProviderId()
    {
        return providerId;
    }
}
