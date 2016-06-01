package com.ayondo.academy.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.key.SearchProviderSecurityListType;
import com.ayondo.academy.api.security.key.SecurityListType;
import javax.inject.Inject;

public class SecuritySearchProviderFragment extends SecuritySearchFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = SecuritySearchProviderFragment.class.getName() + ".providerId";

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;
    @NonNull protected ProviderId providerId;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull public static ProviderId getProviderId(@NonNull Bundle args)
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

    @Override @NonNull public SecurityListType makePagedDtoKey(int page)
    {
        return new SearchProviderSecurityListType(providerId, mSearchText, page, perPage);
    }
}
