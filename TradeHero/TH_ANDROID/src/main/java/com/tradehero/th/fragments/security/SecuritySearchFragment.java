package com.tradehero.th.fragments.security;

import android.view.View;
import android.view.Menu;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.BaseSearchFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

import javax.inject.Inject;

public class SecuritySearchFragment extends BaseSearchFragment<
        SecurityListType,
        SecurityCompactDTO,
        SecurityCompactDTOList,
        SecurityItemView<SecurityCompactDTO>>
        implements HasSelectedItem
{
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;

    protected void initViews(View view)
    {
        super.initViews(view);
        searchEmptyTextView.setText(R.string.trending_search_no_stock_found);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        if (mSearchTextField != null)
        {
            mSearchTextField.setHint(R.string.trending_search_empty_result_for_stock);
        }
    }
    //</editor-fold>

    @Override @Nullable public SecurityCompactDTO getSelectedItem()
    {
        return selectedItem;
    }

    @Override protected SecurityItemViewAdapterNew<SecurityCompactDTO> createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew<>(
                getActivity(),
                R.layout.search_security_item);
    }

    @Override protected DTOCacheNew<SecurityListType, SecurityCompactDTOList> getListCache()
    {
        return securityCompactListCache.get();
    }

    @NotNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new SearchSecurityListType(mSearchText, page, perPage);
    }

    protected void handleDtoClicked(SecurityCompactDTO clicked)
    {
        super.handleDtoClicked(clicked);

        if (getArguments() != null && getArguments().containsKey(
                Navigator.BUNDLE_KEY_RETURN_FRAGMENT))
        {
            getDashboardNavigator().popFragment();
            return;
        }

        if (clicked == null)
        {
            Timber.e(new NullPointerException("clicked was null"), null);
        }
        else
        {
            pushTradeFragmentIn(clicked.getSecurityId());
        }
    }

    protected void pushTradeFragmentIn(SecurityId securityId)
    {
    }

    private DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityIdListCacheListener()
    {
        return new SecurityIdListCacheListener();
    }

    private class SecurityIdListCacheListener extends ListCacheListener
    {
        @Override
        public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            super.onDTOReceived(key, value);
        }

        @Override public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            super.onErrorThrown(key, error);
            THToast.show(getString(R.string.error_fetch_security_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
}