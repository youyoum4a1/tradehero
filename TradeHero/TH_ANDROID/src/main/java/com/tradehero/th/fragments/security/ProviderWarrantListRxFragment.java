package com.ayondo.academy.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.key.WarrantProviderSecurityListType;
import com.ayondo.academy.api.portfolio.AssetClass;
import com.ayondo.academy.api.security.WarrantType;
import com.ayondo.academy.api.security.WarrantTypeShortCode;
import com.ayondo.academy.api.security.key.SecurityListType;
import javax.inject.Inject;

public class ProviderWarrantListRxFragment extends ProviderSecurityListRxFragment
{
    private static final String BUNDLE_WARRANT_TYPE_SHORT_CODE_KEY = ProviderWarrantListRxFragment.class + ".warrantType";
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Nullable private WarrantType warrantType;

    public static void putWarrantType(@NonNull Bundle bundle, @NonNull WarrantType warrantType)
    {
        bundle.putString(BUNDLE_WARRANT_TYPE_SHORT_CODE_KEY, warrantType.shortCode);
    }

    @Nullable private static WarrantType getWarrantType(@NonNull Bundle bundle)
    {
        @WarrantTypeShortCode String shortCode = bundle.getString(BUNDLE_WARRANT_TYPE_SHORT_CODE_KEY, null);
        if (shortCode == null)
        {
            return null;
        }
        return WarrantType.getByShortCode(shortCode);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.warrantType = getWarrantType(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_provider_warrant_list, container, false);
    }

    @Override protected void populateSearchArguments(@NonNull Bundle args)
    {
        super.populateSearchArguments(args);
        SecuritySearchProviderFragment.putAssetClass(args, AssetClass.WARRANT);
    }

    @NonNull @Override protected SecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(getActivity(), R.layout.warrant_security_item);
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new WarrantProviderSecurityListType(providerId, warrantType, page, perPage);
    }
}
