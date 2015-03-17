package com.tradehero.th.fragments.position;

import android.os.Bundle;
import com.google.common.annotations.VisibleForTesting;
import com.tradehero.th.api.competition.ProviderId;

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

    @VisibleForTesting public ProviderId getProviderId()
    {
        return providerId;
    }
}
