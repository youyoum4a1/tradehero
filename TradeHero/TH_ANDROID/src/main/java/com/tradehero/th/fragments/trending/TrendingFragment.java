package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.metrics.events.TrendingStockEvent;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import timber.log.Timber;

@Routable("trending-securities")
public class TrendingFragment extends SecurityListFragment
        implements WithTutorial
{
    public final static int SECURITY_ID_LIST_LOADER_ID = 2532;

    @Inject Lazy<ExchangeCompactListCacheRx> exchangeCompactListCache;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<ProviderCacheRx> providerCache;
    @Inject Lazy<ProviderListCacheRx> providerListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ExchangeCompactDTOUtil exchangeCompactDTOUtil;
    @Inject Analytics analytics;

    @InjectView(R.id.trending_filter_selector_view) protected TrendingFilterSelectorView filterSelectorView;

    private UserProfileDTO userProfileDTO;

    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs;
    private boolean defaultFilterSelected;
    @NonNull private TrendingFilterTypeDTO trendingFilterTypeDTO;

    private ExtraTileAdapter wrapperAdapter;
    private Runnable handleCompetitionRunnable;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(getActivity().getResources());
        defaultFilterSelected = false;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);

        if (this.filterSelectorView != null)
        {
            this.filterSelectorView.apply(this.trendingFilterTypeDTO);
            this.filterSelectorView.setChangedListener(createTrendingFilterChangedListener());
        }

        fetchExchangeList();
    }

    @Override protected AbsListView.OnScrollListener createListViewScrollListener()
    {
        int trendingFilterHeight = (int) getResources().getDimension(R.dimen.trending_filter_view_pager_height);
        QuickReturnListViewOnScrollListener filterQuickReturnScrollListener =
                new QuickReturnListViewOnScrollListener(QuickReturnType.HEADER, filterSelectorView,
                        -trendingFilterHeight, null, 0);
        return new MultiScrollListener(listViewScrollListener, dashboardBottomTabsListViewScrollListener.get(), filterQuickReturnScrollListener);
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Trade));

        // fetch user
        AndroidObservable.bindFragment(this, userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileFetchObserver());

        // fetch provider list for provider tile
        fetchProviderList();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.trending_header);
        inflater.inflate(R.menu.search_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchIn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        removeCallbacksIfCan(handleCompetitionRunnable);

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        filterSelectorView.setChangedListener(null);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        handleCompetitionRunnable = null;
        super.onDestroy();
    }

    protected void fetchProviderList()
    {
        AndroidObservable.bindFragment(this, providerListCache.get().get(new ProviderListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProviderListFetchObserver());
    }

    @Override protected ListAdapter createSecurityItemViewAdapter()
    {
        SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter =
                new SimpleSecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.trending_security_item);

        //return simpleSecurityItemViewAdapter;
        // use above adapter to disable extra tile on the trending screen
        wrapperAdapter = new ExtraTileAdapter(getActivity(), simpleSecurityItemViewAdapter);
        return wrapperAdapter;
    }

    @Override public int getSecurityIdListLoaderId()
    {
        return SECURITY_ID_LIST_LOADER_ID;
    }

    private void fetchExchangeList()
    {
        ExchangeListType key = new ExchangeListType();
        AndroidObservable.bindFragment(this, exchangeCompactListCache.get().get(key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createExchangeListTypeFetchObserver());
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        setUpFilterSelectorView();
        refreshAdapterWithTiles(userProfileDTO.activeSurveyImageURL != null);
    }

    private void linkWith(@NonNull ExchangeCompactDTOList exchangeDTOs, boolean andDisplay)
    {
        ExchangeCompactSpinnerDTOList spinnerList = new ExchangeCompactSpinnerDTOList(
                getResources(),
                exchangeCompactDTOUtil.filterAndOrderForTrending(
                        exchangeDTOs,
                        new ExchangeCompactDTODescriptionNameComparator<>()));
        // Adding the "All" choice
        spinnerList.add(0, new ExchangeCompactSpinnerDTO(getResources()));
        linkWith(spinnerList, andDisplay);
    }

    private void linkWith(@NonNull ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs, boolean andDisplay)
    {
        this.exchangeCompactSpinnerDTOs = exchangeCompactSpinnerDTOs;
        setUpFilterSelectorView();
    }

    private void setUpFilterSelectorView()
    {
        setDefaultExchange();
        if (filterSelectorView != null)
        {
            if (exchangeCompactSpinnerDTOs != null)
            {
                filterSelectorView.setUpExchangeSpinner(exchangeCompactSpinnerDTOs);
            }
            filterSelectorView.apply(trendingFilterTypeDTO);
        }
    }

    private void setDefaultExchange()
    {
        if (!defaultFilterSelected)
        {
            if (userProfileDTO != null && exchangeCompactSpinnerDTOs != null)
            {
                Country country = userProfileDTO.getCountry();
                if (country != null)
                {
                    ExchangeCompactSpinnerDTO initial = exchangeCompactSpinnerDTOs.findFirstDefaultFor(userProfileDTO.getCountry());
                    if (initial != null)
                    {
                        trendingFilterTypeDTO.exchange = initial;
                        defaultFilterSelected = true;
                    }
                }
            }
        }
    }

    @Override @NonNull public TrendingSecurityListType getSecurityListType(int page)
    {
        return trendingFilterTypeDTO.getSecurityListType(page, perPage);
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }

    //<editor-fold desc="Listeners">
    @Override protected OnItemClickListener createOnItemClickListener()
    {
        return new OnSecurityViewClickListener();
    }

    private class OnSecurityViewClickListener implements OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Object item = parent.getItemAtPosition(position);
            View child = parent.getChildAt(position - parent.getFirstVisiblePosition());
            if (item instanceof SecurityCompactDTO)
            {
                handleSecurityItemOnClick((SecurityCompactDTO) item);
            }
            else if (item instanceof TileType)
            {
                handleExtraTileItemOnClick((TileType) item, child);
            }
        }
    }

    private void handleExtraTileItemOnClick(TileType item, View view)
    {
        switch (item)
        {
            case EarnCredit:
                handleEarnCreditItemOnClick();
                break;
            case ExtraCash:
                handleExtraCashItemOnClick();
                break;
            case ResetPortfolio:
                handleResetPortfolioItemOnClick();
                break;
            case Survey:
                handleSurveyItemOnClick();
                break;
            case FromProvider:
                handleProviderTileOnClick(view);
                break;
        }
    }

    private void handleProviderTileOnClick(View view)
    {
        if (view instanceof ProviderTileView)
        {
            int providerId = ((ProviderTileView) view).getProviderId();
            ProviderDTO providerDTO = providerCache.get().getValue(new ProviderId(providerId));
            switch (providerId)
            {
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    Timber.d("PROVIDER_ID_MACQUARIE_WARRANTS");
                    break;
                default:
                    handleCompetitionItemClicked(providerDTO);
                    break;
            }
        }
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            MainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
            MainCompetitionFragment.putApplicablePortfolioId(args, providerDTO.getAssociatedOwnedPortfolioId());
            navigator.get().pushFragment(MainCompetitionFragment.class, args);
        }
        else if (providerDTO != null)
        {
            navigator.get().pushFragment(CompetitionEnrollmentWebViewFragment.class, providerDTO.getProviderId().getArgs());
        }
    }

    private void handleSurveyItemOnClick()
    {
        AndroidObservable.bindFragment(this, userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .first()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>()
                {
                    @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> args)
                    {
                        if (args.second.activeSurveyURL != null)
                        {
                            Bundle bundle = new Bundle();
                            WebViewFragment.putUrl(bundle, args.second.activeSurveyURL);
                            navigator.get().pushFragment(WebViewFragment.class, bundle, null);
                        }
                    }
                });
    }

    private void handleResetPortfolioItemOnClick()
    {
        detachRequestCode();
        //noinspection unchecked
        requestCode = userInteractor.run((THUIBillingRequest)
                uiBillingRequestBuilderProvider.get()
                        .domainToPresent(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO)
                        .applicablePortfolioId(getApplicablePortfolioId())
                        .startWithProgressDialog(true)
                        .build());
    }

    protected void handleExtraCashItemOnClick()
    {
        detachRequestCode();
        //noinspection unchecked
        requestCode = userInteractor.run((THUIBillingRequest)
                uiBillingRequestBuilderProvider.get()
                        .domainToPresent(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR)
                        .applicablePortfolioId(getApplicablePortfolioId())
                        .startWithProgressDialog(true)
                        .build());
    }

    private void handleEarnCreditItemOnClick()
    {
        navigator.get().pushFragment(FriendsInvitationFragment.class);
    }

    private void handleSecurityItemOnClick(SecurityCompactDTO securityCompactDTO)
    {
        analytics.fireEvent(new TrendingStockEvent(securityCompactDTO.getSecurityId()));

        Bundle args = new Bundle();
        BuySellFragment.putSecurityId(args, securityCompactDTO.getSecurityId());

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

        if (ownedPortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        navigator.get().pushFragment(BuySellFragment.class, args);
    }

    protected TrendingFilterSelectorView.OnFilterTypeChangedListener createTrendingFilterChangedListener()
    {
        return new TrendingOnFilterTypeChangedListener();
    }

    protected class TrendingOnFilterTypeChangedListener implements TrendingFilterSelectorView.OnFilterTypeChangedListener
    {
        @Override public void onFilterTypeChanged(TrendingFilterTypeDTO trendingFilterTypeDTO)
        {
            Timber.d("Filter onFilterTypeChanged");
            if (trendingFilterTypeDTO == null)
            {
                Timber.e(new IllegalArgumentException("onFilterTypeChanged trendingFilterTypeDTO cannot be null"),
                        "onFilterTypeChanged trendingFilterTypeDTO cannot be null");
            }
            TrendingFragment.this.trendingFilterTypeDTO = trendingFilterTypeDTO;
            // TODO
            forceInitialLoad();
        }
    }
    //</editor-fold>

    @Override protected void handleSecurityItemReceived(@Nullable SecurityCompactDTOList securityCompactDTOs)
    {
        if (AppTiming.trendingFilled == 0)
        {
            AppTiming.trendingFilled = System.currentTimeMillis();
        }
        //Timber.d("handleSecurityItemReceived "+securityCompactDTOs.toString());
        if (securityItemViewAdapter != null && securityCompactDTOs != null)
        {
            // It may have been nullified if coming out
            securityItemViewAdapter.setItems(securityCompactDTOs);
            refreshAdapterWithTiles(false);
        }

        Timber.d("splash %d, dash %d, trending %d",
                AppTiming.splashCreate - AppTiming.appCreate,
                AppTiming.dashboardCreate - AppTiming.splashCreate,
                AppTiming.trendingFilled - AppTiming.dashboardCreate);
    }

    private void refreshAdapterWithTiles(boolean refreshTileTypes)
    {
        // TODO hack, experience some synchronization matter here, generateExtraTiles should be call inside wrapperAdapter
        // when data is changed
        // Note that this is just to minimize the chance of happening, need synchronize the data changes inside super class DTOAdapter
        if (wrapperAdapter != null)
        {
            wrapperAdapter.regenerateExtraTiles(false, refreshTileTypes);
        }

        if (securityItemViewAdapter != null)
        {
            securityItemViewAdapter.notifyDataSetChanged();
        }
    }

    //<editor-fold desc="Exchange List Listener">
    protected Observer<Pair<ExchangeListType, ExchangeCompactDTOList>> createExchangeListTypeFetchObserver()
    {
        return new TrendingExchangeListTypeFetchObserver();
    }

    protected class TrendingExchangeListTypeFetchObserver implements Observer<Pair<ExchangeListType, ExchangeCompactDTOList>>
    {
        @Override public void onNext(Pair<ExchangeListType, ExchangeCompactDTOList> pair)
        {
            Timber.d("Filter exchangeListTypeCacheListener onDTOReceived");
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_exchange_list_info));
            Timber.e("Error fetching the list of exchanges", e);
        }
    }
    //</editor-fold>

    //<editor-fold desc="User Profile Listener">
    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileFetchObserver()
    {
        return new TrendingUserProfileFetchObserver();
    }

    protected class TrendingUserProfileFetchObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            Timber.d("Retrieve user with surveyUrl=%s", pair.second.activeSurveyImageURL);
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_user_profile);
        }
    }
    //</editor-fold>

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_trending_screen;
    }

    //<editor-fold desc="Provider List Listener">
    protected Observer<Pair<ProviderListKey, ProviderDTOList>> createProviderListFetchObserver()
    {
        return new TrendingProviderListFetchObserver();
    }

    protected class TrendingProviderListFetchObserver implements Observer<Pair<ProviderListKey, ProviderDTOList>>
    {
        @Override public void onNext(Pair<ProviderListKey, ProviderDTOList> providerListKeyProviderDTOListPair)
        {
            refreshAdapterWithTiles(true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_provider_competition_list);
        }
    }
    //</editor-fold>
}
