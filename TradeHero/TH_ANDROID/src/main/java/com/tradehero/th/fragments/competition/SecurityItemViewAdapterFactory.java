package com.tradehero.th.fragments.competition;

import android.app.Activity;
import android.widget.ListAdapter;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class SecurityItemViewAdapterFactory
{
    @Inject SecurityItemLayoutFactory securityItemLayoutFactory;

    @Inject public SecurityItemViewAdapterFactory()
    {
        super();
    }

    protected ListAdapter create(Activity activity, ProviderId providerId)
    {
        if (providerId != null)
        {
            switch (providerId.key)
            {
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                case ProviderIdConstants.PROVIDER_ID_PHILLIP_MACQUARIE_WARRANTS:
                    Timber.d("Macquarie adapter");
                    return new MacquarieWarrantItemViewAdapter(
                            activity,
                            activity.getLayoutInflater(),
                            securityItemLayoutFactory.getProviderLayout(providerId));
                default:
                    Timber.d("Unhandled providerId.key %s", providerId.key);
            }

        }
        Timber.d("Regular adapter");
        return new SimpleSecurityItemViewAdapter(
                activity,
                activity.getLayoutInflater(),
                securityItemLayoutFactory.getProviderLayout(providerId));
    }
}
