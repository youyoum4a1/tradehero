package com.tradehero.th.fragments.position;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.annotations.VisibleForTesting;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.models.security.ProviderTradableSecuritiesHelper;
import javax.inject.Inject;

public class CompetitionLeaderboardPositionListFragment extends PositionListFragment
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

    @Override protected void pushSecuritiesFragment()
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
