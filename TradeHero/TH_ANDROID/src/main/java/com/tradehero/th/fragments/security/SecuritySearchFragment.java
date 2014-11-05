package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.BaseSearchFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Subscription;
import timber.log.Timber;

public class SecuritySearchFragment extends BaseSearchFragment<
        SecurityListType,
        SecurityCompactDTO,
        SecurityCompactDTOList,
        SecurityCompactDTOList,
        SecurityItemView<SecurityCompactDTO>>
        implements HasSelectedItem
{
    @Inject Lazy<SecurityCompactListCacheRx> securityCompactListCache;
    private Subscription stockSearchSubscription;

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

    @Override protected void unregisterCache(DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> listener)
    {
        Subscription subscriptionCopy = stockSearchSubscription;
        if (subscriptionCopy != null)
        {
            subscriptionCopy.unsubscribe();
        }
        stockSearchSubscription = null;
    }

    @Override protected void registerCache(SecurityListType key, DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> listener)
    {
        //securityCompactListCache.get().register(key, listener);
    }

    @Override protected void requestCache(SecurityListType key)
    {
        //securityCompactListCache.get().getOrFetchAsync(key);
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new SearchSecurityListType(mSearchText, page, perPage);
    }

    protected void handleDtoClicked(SecurityCompactDTO clicked)
    {
        super.handleDtoClicked(clicked);

        if (getArguments() != null && getArguments().containsKey(DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT))
        {
            navigator.get().popFragment();
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
        Bundle args = new Bundle();
        BuySellFragment.putSecurityId(args, securityId);
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        navigator.get().pushFragment(BuySellFragment.class, args);
    }

    private DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityIdListCacheListener()
    {
        return new SecurityIdListCacheListener();
    }

    private class SecurityIdListCacheListener extends ListCacheListener
    {
        @Override
        public void onDTOReceived(@NonNull SecurityListType key, @NonNull SecurityCompactDTOList value)
        {
            super.onDTOReceived(key, value);
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_Stock));
        }

        @Override public void onErrorThrown(@NonNull SecurityListType key, @NonNull Throwable error)
        {
            super.onErrorThrown(key, error);
            THToast.show(getString(R.string.error_fetch_security_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
}