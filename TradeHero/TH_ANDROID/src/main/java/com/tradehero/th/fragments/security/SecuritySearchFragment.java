package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.BaseSearchRxFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trade.BuySellFXFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

public class SecuritySearchFragment extends BaseSearchRxFragment<
        SecurityListType,
        SecurityCompactDTO,
        SecurityCompactDTOList,
        SecurityCompactDTOList,
        SecurityItemView>
        implements HasSelectedItem
{
    @Inject SecurityCompactListCacheRx securityCompactListCache;
    @Inject Analytics analytics;

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        searchEmptyTextView.setText(R.string.trending_search_no_stock_found);

        //We set this to true so that the item will show selected state when pressed.
        listView.setDrawSelectorOnTop(true);
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

    @Override @NonNull protected SecurityItemViewAdapterNew createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew(
                getActivity(),
                R.layout.search_security_item);
    }

    @Override @NonNull protected DTOCacheRx<SecurityListType, SecurityCompactDTOList> getCache()
    {
        return securityCompactListCache;
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
            pushTradeFragmentIn(clicked);
        }
    }

    protected void pushTradeFragmentIn(SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            BuySellFXFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
            navigator.get().pushFragment(BuySellFXFragment.class, args);
        }
        else
        {
            BuySellStockFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
            navigator.get().pushFragment(BuySellStockFragment.class, args);
        }
    }

    @Override @NonNull protected Observer<Pair<SecurityListType, SecurityCompactDTOList>> createListCacheObserver(@NonNull SecurityListType key)
    {
        return new SecurityIdListCacheObserver(key);
    }

    protected class SecurityIdListCacheObserver extends ListCacheObserver
    {
        protected SecurityIdListCacheObserver(@NonNull SecurityListType key)
        {
            super(key);
        }

        @Override public void onNext(Pair<SecurityListType, SecurityCompactDTOList> pair)
        {
            super.onNext(pair);
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_Stock));
        }

        @Override public void onError(Throwable error)
        {
            super.onError(error);
            THToast.show(getString(R.string.error_fetch_security_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
}