package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.competition.ProviderCache;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
public class CompetitionFragment extends DashboardFragment
{
    public static final String TAG = CompetitionFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_PROVIDER_ID = CompetitionFragment.class.getName() + ".providerId";

    private ProviderId providerId;
    private ProviderDTO providerDTO;
    @Inject ProviderCache providerCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_PROVIDER_ID))
        {
            this.providerId = new ProviderId(savedInstanceState.getBundle(BUNDLE_KEY_PROVIDER_ID));
        }
        else if (getArguments() != null && getArguments().containsKey(BUNDLE_KEY_PROVIDER_ID))
        {
            this.providerId = new ProviderId(getArguments().getBundle(BUNDLE_KEY_PROVIDER_ID));
        }
    }



    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
