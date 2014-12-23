package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.WarrantType;
import com.tradehero.th.api.security.key.SecurityListType;
import javax.inject.Inject;

public class ProviderWarrantListRxFragment extends ProviderSecurityListRxFragment<SecurityItemView>
{
    private static final String BUNDLE_WARRANT_TYPE_SHORT_CODE_KEY = ProviderWarrantListRxFragment.class + ".warrantType";
    @Inject Context dummyInject;

    @Nullable private WarrantType warrantType;

    public static void putWarrantType(@NonNull Bundle bundle, @NonNull WarrantType warrantType)
    {
        bundle.putString(BUNDLE_WARRANT_TYPE_SHORT_CODE_KEY, warrantType.shortCode);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            String shortCode = getArguments().getString(BUNDLE_WARRANT_TYPE_SHORT_CODE_KEY, null);
            if (shortCode != null)
            {
                this.warrantType = WarrantType.getByShortCode(shortCode);
            }
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_stock, container, false);
    }

    @NonNull @Override protected PagedArrayDTOAdapterNew<SecurityCompactDTO, SecurityItemView> createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew(getActivity(), R.layout.warrant_security_item);
    }

    @NonNull @Override protected DTOCacheRx<SecurityListType, SecurityCompactDTOList> getCache()
    {
        return securityCompactListCache;
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new BasicProviderSecurityListType(providerId, page, perPage);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        scheduleRequestData();
    }
}
