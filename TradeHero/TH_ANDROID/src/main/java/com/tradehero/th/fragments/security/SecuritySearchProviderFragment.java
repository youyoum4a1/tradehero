package com.tradehero.th.fragments.security;

import android.os.Bundle;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.SearchProviderSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;

public class SecuritySearchProviderFragment extends SecuritySearchFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = SecuritySearchProviderFragment.class.getName() + ".providerId";

    protected ProviderId providerId;

    public static void putProviderId(Bundle args, ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    public static ProviderId getProviderId(Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = getProviderId(getArguments());
        if (savedInstanceState != null)
        {
            providerId = getProviderId(savedInstanceState);
        }
    }

    @Override public SecurityListType makeSearchSecurityListType(int page)
    {
        return new SearchProviderSecurityListType(providerId, mSearchText, page, perPage);

    }
}
