package com.androidth.general.fragments.position;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.models.security.ProviderTradableSecuritiesHelper;
import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;

//TODO need refactor by alex
public class CompetitionLeaderboardPositionListFragment extends TabbedPositionListFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardPositionListFragment.class + ".providerId";

    private String navigationLogoUrl;
    private String hexcolor;
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarColorSelf(navigationLogoUrl, hexcolor);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        this.providerId = getProviderId(bundle);

        if(bundle.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR)!=null){
            hexcolor = bundle.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR);
        }

        if(bundle.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL)!=null){
            navigationLogoUrl = bundle.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL);
        }
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
