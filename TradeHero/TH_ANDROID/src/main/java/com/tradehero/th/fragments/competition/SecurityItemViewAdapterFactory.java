package com.tradehero.th.fragments.competition;

import android.app.Activity;
import android.content.Context;
import android.widget.ListAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/19/14.
 */
@Singleton public class SecurityItemViewAdapterFactory
{
    public static final String TAG = SecurityItemViewAdapterFactory.class.getSimpleName();

    @Inject protected SecurityItemLayoutFactory securityItemLayoutFactory;

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
                case ProviderIdConstants.PROVIDER_ID_PHILIPS_MACQUARIE_WARRANTS:
                    THLog.d(TAG, "Macquarie adapter");
                    return new MacquarieWarrantItemViewAdapter(
                            activity,
                            activity.getLayoutInflater(),
                            securityItemLayoutFactory.getProviderLayout(providerId));
                default:
                    THLog.d(TAG, "Unhandled providerId.key " + providerId.key);
            }

        }
        THLog.d(TAG, "Regular adapter");
        return new SimpleSecurityItemViewAdapter(
                activity,
                activity.getLayoutInflater(),
                securityItemLayoutFactory.getProviderLayout(providerId));
    }
}
