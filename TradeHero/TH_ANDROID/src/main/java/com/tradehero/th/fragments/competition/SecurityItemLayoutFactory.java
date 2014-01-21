package com.tradehero.th.fragments.competition;

import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/21/14.
 */
@Singleton public class SecurityItemLayoutFactory
{
    public static final String TAG = SecurityItemLayoutFactory.class.getSimpleName();

    @Inject public SecurityItemLayoutFactory()
    {
    }

    public int getProviderLayout(ProviderId providerId)
    {
        switch (providerId.key)
        {
            case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                return R.layout.warrant_security_item;

            default:
                return R.layout.trending_security_item;
        }
    }
}
