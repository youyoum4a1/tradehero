package com.tradehero.th.fragments.position;

import android.content.Context;
import android.os.Bundle;
import com.google.common.annotations.VisibleForTesting;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.models.security.ProviderTradableSecuritiesHelper;
import dagger.Lazy;
import javax.inject.Inject;

public class CompetitionLeaderboardPositionListFragment extends PositionListFragment
{
    @Inject Context doNotRemoveOrItFails;

    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardPositionListFragment.class + ".providerId";

    protected ProviderId providerId;
    @Inject Lazy<ProviderTradableSecuritiesHelper> providerTradableSecuritiesHelperLazy;

    public static void putProviderId(Bundle args, ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    private static ProviderId getProviderId(Bundle args)
    {
        Bundle bundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID);
        if (bundle == null) {
            return null;
        }
        return new ProviderId(bundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
    }

    @Override protected void pushTrendingFragment()
    {
        Bundle args = new Bundle();
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        providerTradableSecuritiesHelperLazy.get().pushTradableSecuritiesList(args, ownedPortfolioId, portfolioDTO, providerId);
    }

    @VisibleForTesting public ProviderId getProviderId()
    {
        return providerId;
    }
}
