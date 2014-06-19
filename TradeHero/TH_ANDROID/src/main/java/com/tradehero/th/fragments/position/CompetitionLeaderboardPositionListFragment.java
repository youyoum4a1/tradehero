package com.tradehero.th.fragments.position;

import android.os.Bundle;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.competition.ProviderSecurityListFragment;

public class CompetitionLeaderboardPositionListFragment extends LeaderboardPositionListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardPositionListFragment.class + ".providerId";

    protected ProviderId providerId;

    public static void putProviderId(Bundle args, ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    private static ProviderId getProviderId(Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
    }

    @Override protected void pushSecurityFragment()
    {
        Bundle args = new Bundle();
        ProviderSecurityListFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
        ProviderSecurityListFragment.putProviderId(args, providerId);
        getDashboardNavigator().pushFragment(ProviderSecurityListFragment.class, args);
    }
}
