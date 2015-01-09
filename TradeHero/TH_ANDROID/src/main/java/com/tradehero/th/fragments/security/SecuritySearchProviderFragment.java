package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.SearchProviderSecurityListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.security.key.SecurityListType;
import javax.inject.Inject;

public class SecuritySearchProviderFragment extends SecuritySearchFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = SecuritySearchProviderFragment.class.getName() + ".providerId";
    private static final String BUNDLE_KEY_PROVIDER_TYPE = SecuritySearchProviderFragment.class.getName() + ".providerType";

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;
    @NonNull protected ProviderId providerId;
    public AssetClass assetClass;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull public static ProviderId getProviderId(@NonNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    public static void putProviderType(@NonNull Bundle args, @NonNull AssetClass assetClass)
    {
        args.putInt(BUNDLE_KEY_PROVIDER_TYPE, assetClass.getValue());
    }

    @NonNull public static AssetClass getProviderType(@NonNull Bundle args)
    {
        return AssetClass.create(args.getInt(BUNDLE_KEY_PROVIDER_TYPE,AssetClass.STOCKS.getValue()));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = getProviderId(getArguments());
        assetClass = getProviderType(getArguments());
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
