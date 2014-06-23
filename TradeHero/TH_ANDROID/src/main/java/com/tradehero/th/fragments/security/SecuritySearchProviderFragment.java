package com.tradehero.th.fragments.security;

import android.os.Bundle;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.SearchProviderSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import org.jetbrains.annotations.NotNull;

public class SecuritySearchProviderFragment extends SecuritySearchFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = SecuritySearchProviderFragment.class.getName() + ".providerId";

    @NotNull protected ProviderId providerId;

    public static void putProviderId(@NotNull Bundle args, @NotNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NotNull public static ProviderId getProviderId(@NotNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = getProviderId(getArguments());
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_PROVIDER_ID))
        {
            providerId = getProviderId(savedInstanceState);
        }
    }

    @Override @NotNull public SecurityListType makeSearchSecurityListType(int page)
    {
        return new SearchProviderSecurityListType(providerId, mSearchText, page, perPage);

    }
}
