package com.ayondo.academy.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.View;

import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.portfolio.AssetClass;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityCompactDTOList;
import com.ayondo.academy.api.security.SecurityCompactDTOUtil;
import com.ayondo.academy.api.security.key.SearchSecurityListType;
import com.ayondo.academy.api.security.key.SecurityListType;
import com.ayondo.academy.fragments.BaseSearchRxFragment;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.trade.AbstractBuySellFragment;
import com.ayondo.academy.persistence.security.SecurityCompactListCacheRx;

import javax.inject.Inject;

import timber.log.Timber;

public class SecuritySearchFragment extends BaseSearchRxFragment<
        SecurityListType,
        SecurityCompactDTO,
        SecurityCompactDTOList,
        SecurityCompactDTOList>
        implements HasSelectedItem
{
    private static final String BUNDLE_KEY_ASSET_CLASS = SecuritySearchProviderFragment.class.getName() + ".assetClass";

    @Inject SecurityCompactListCacheRx securityCompactListCache;
    //TODO Change Analytics
    //@Inject Analytics analytics;

    @Nullable protected AssetClass assetClass;

    public static void putAssetClass(@NonNull Bundle args, @NonNull AssetClass assetClass)
    {
        args.putInt(BUNDLE_KEY_ASSET_CLASS, assetClass.getValue());
    }

    @NonNull protected static AssetClass getAssetClassFromBundle(@NonNull Bundle args)
    {
        AssetClass retrieved = AssetClass.create(args.getInt(BUNDLE_KEY_ASSET_CLASS, AssetClass.STOCKS.getValue()));
        if (retrieved == null)
        {
            retrieved = AssetClass.STOCKS;
        }
        return retrieved;
    }

    @NonNull private AssetClass getAssetClass()
    {
        if (assetClass == null)
        {
            assetClass = getAssetClassFromBundle(getArguments());
        }
        return assetClass;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        assetClass = getAssetClassFromBundle(getArguments());
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        switch (getAssetClass())
        {
            case STOCKS:
                searchEmptyTextView.setText(R.string.trending_search_no_stock_found);
                break;
            case WARRANT:
                searchEmptyTextView.setText(R.string.trending_search_no_warrant_found);
                break;
            case FX:
                searchEmptyTextView.setText(R.string.trending_search_no_forex_found);
                break;
        }

        //We set this to true so that the item will show selected state when pressed.
        listView.setDrawSelectorOnTop(true);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        if (mSearchTextField != null)
        {
            switch (getAssetClass())
            {
                case STOCKS:
                    mSearchTextField.setHint(R.string.trending_search_empty_result_for_stock);
                    break;
                case WARRANT:
                    mSearchTextField.setHint(R.string.trending_search_empty_result_for_warrant);
                    break;
                case FX:
                    mSearchTextField.setHint(R.string.trending_search_empty_result_for_forex);
                    break;
            }
        }
    }
    //</editor-fold>

    @Override @Nullable public SecurityCompactDTO getSelectedItem()
    {
        return selectedItem;
    }

    @Override @NonNull protected SecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(
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

        if (getArguments() != null && DashboardNavigator.getReturnFragment(getArguments()) != null)
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
        AbstractBuySellFragment.putRequisite(
                args,
                new AbstractBuySellFragment.Requisite(securityCompactDTO.getSecurityId(),
                        getApplicablePortfolioId(),
                        0));
        navigator.get().pushFragment(SecurityCompactDTOUtil.fragmentFor(securityCompactDTO), args);
    }

    @Override protected void onNext(@NonNull SecurityListType key, @NonNull SecurityCompactDTOList value)
    {
        super.onNext(key, value);
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_Stock));
    }

    @Override protected void onError(@NonNull SecurityListType key, @NonNull Throwable error)
    {
        super.onError(key, error);
        THToast.show(getString(R.string.error_fetch_security_list_info));
        Timber.e("Error fetching the list of securities " + key, error);
    }
}