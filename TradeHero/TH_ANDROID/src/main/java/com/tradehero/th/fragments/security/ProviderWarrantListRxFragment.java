package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedViewDTOAdapter;
import com.tradehero.th.api.competition.key.WarrantProviderSecurityListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
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
        return inflater.inflate(R.layout.fragment_provider_warrant_list, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pushSearchFragment()
    {
        Bundle args = new Bundle();
        SecuritySearchProviderFragment.putProviderId(args, providerId);
        SecuritySearchProviderFragment.putAssetClass(args, AssetClass.WARRANT);
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            SecuritySearchProviderFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        navigator.get().pushFragment(SecuritySearchProviderFragment.class, args);
    }

    @NonNull @Override protected PagedViewDTOAdapter<SecurityCompactDTO, SecurityItemView> createItemViewAdapter()
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
        return new WarrantProviderSecurityListType(providerId, warrantType, page, perPage);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        scheduleRequestData();
    }
}
