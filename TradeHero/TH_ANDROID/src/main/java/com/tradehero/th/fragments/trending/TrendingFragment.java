package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityItemViewAdapterNew;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.metrics.events.TrendingStockEvent;
import com.tradehero.th.widget.MultiScrollListener;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import timber.log.Timber;

@Routable("trending-securities")
public class TrendingFragment extends SecurityListRxFragment<SecurityItemView>
        implements WithTutorial
{
    @Inject CurrentUserId currentUserId;
    @Inject ExchangeCompactListCacheRx exchangeCompactListCache;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject ProviderListCacheRx providerListCache;
    @Inject ExchangeCompactDTOUtil exchangeCompactDTOUtil;
    @Inject Analytics analytics;

    @InjectView(R.id.trending_filter_selector_view) protected TrendingFilterSelectorView filterSelectorView;

    private SubscriptionList subscriptions;
    private UserProfileDTO userProfileDTO;
    private ProviderDTOList providerDTOs;
    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs;
    private boolean defaultFilterSelected;
    @NonNull private TrendingFilterTypeDTO trendingFilterTypeDTO;

    private ExtraTileAdapter wrapperAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(getActivity().getResources());
        defaultFilterSelected = false;
        wrapperAdapter = createSecurityItemViewAdapter();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.filterSelectorView.apply(this.trendingFilterTypeDTO);
        this.listView.setAdapter(wrapperAdapter);
    }

    @Override public void onStart()
    {
        super.onStart();
        subscriptions = new SubscriptionList();
        subscriptions.add(AndroidObservable.bindFragment(this,
                this.filterSelectorView.getObservableFilter())
                .subscribe(createTrendingFilterChangedObserver()));
        fetchExchangeList();
        fetchUserProfile();
        fetchProviderList();
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Trade));
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
        subscriptions.unsubscribe();
        subscriptions = null;
        super.onStop();
    }

    @Override public void onDestroy()
    {
        wrapperAdapter = null;
        super.onDestroy();
    }

    @Override @NonNull protected SecurityItemViewAdapterNew createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew(getActivity(), R.layout.trending_security_item)
        {
            @Override public void notifyDataSetChanged()
            {
                wrapperAdapter.notifyDataSetChanged();
                super.notifyDataSetChanged();
            }
        };
    }

    @NonNull protected ExtraTileAdapter createSecurityItemViewAdapter()
    {
        return new ExtraTileAdapter(getActivity(), itemViewAdapter);
    }

    @Override protected int getFragmentLayoutResId()
    {
        return R.layout.fragment_trending;
    }

    @Override @NonNull protected AbsListView.OnScrollListener createListViewScrollListener()
    {
        int trendingFilterHeight = (int) getResources().getDimension(R.dimen.trending_filter_view_pager_height);
        QuickReturnListViewOnScrollListener filterQuickReturnScrollListener =
                new QuickReturnListViewOnScrollListener(QuickReturnType.HEADER, filterSelectorView,
                        -trendingFilterHeight, null, 0);
        return new MultiScrollListener(super.createListViewScrollListener(), filterQuickReturnScrollListener);
    }

    private void fetchExchangeList()
    {
        ExchangeListType key = new ExchangeListType();
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                exchangeCompactListCache.get(key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createExchangeListTypeFetchObserver()));
    }

    @NonNull protected Observer<Pair<ExchangeListType, ExchangeCompactDTOList>> createExchangeListTypeFetchObserver()
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

    private void fetchUserProfile()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileFetchObserver()));
    }

    @NonNull protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileFetchObserver()
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

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        setUpFilterSelectorView();
        refreshAdapterWithTiles(userProfileDTO.activeSurveyImageURL != null);
    }

    private void fetchProviderList()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                providerListCache.get(new ProviderListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProviderListFetchObserver()));
    }

    @NonNull protected Observer<Pair<ProviderListKey, ProviderDTOList>> createProviderListFetchObserver()
    {
        return new TrendingProviderListFetchObserver();
    }

    protected class TrendingProviderListFetchObserver implements Observer<Pair<ProviderListKey, ProviderDTOList>>
    {
        @Override public void onNext(Pair<ProviderListKey, ProviderDTOList> pair)
        {
            providerDTOs = pair.second;
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

    protected Observer<TrendingFilterTypeDTO> createTrendingFilterChangedObserver()
    {
        return new TrendingOnFilterTypeChangedObserver();
    }

    protected class TrendingOnFilterTypeChangedObserver extends EmptyObserver<TrendingFilterTypeDTO>
    {
        @Override public void onNext(@NonNull TrendingFilterTypeDTO trendingFilterTypeDTO)
        {
            Timber.d("Filter onFilterTypeChanged");
            TrendingFragment.this.trendingFilterTypeDTO = trendingFilterTypeDTO;
            // TODO
            scheduleRequestData();
        }
    }

    @Override @NonNull protected DTOCacheRx<SecurityListType, SecurityCompactDTOList> getCache()
    {
        return securityCompactListCache;
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return trendingFilterTypeDTO.getSecurityListType(page, perPage);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_trending_screen;
    }

    @Override @NonNull protected Observer<Pair<SecurityListType, SecurityCompactDTOList>> createListCacheObserver(@NonNull SecurityListType key)
    {
        return new TrendingFragmentListCacheObserver(key);
    }

    protected class TrendingFragmentListCacheObserver extends ListCacheObserver
    {
        protected TrendingFragmentListCacheObserver(@NonNull SecurityListType key)
        {
            super(key);
        }

        @Override public void onNext(Pair<SecurityListType, SecurityCompactDTOList> pair)
        {
            super.onNext(pair);
            refreshAdapterWithTiles(false);
        }
    }

    private void refreshAdapterWithTiles(boolean refreshTileTypes)
    {
        // TODO hack, experience some synchronization matter here, generateExtraTiles should be call inside wrapperAdapter
        // when data is changed
        // Note that this is just to minimize the chance of happening, need synchronize the data changes inside super class DTOAdapter
        wrapperAdapter.regenerateExtraTiles(false, refreshTileTypes);
        wrapperAdapter.notifyDataSetChanged();
    }

    private void setUpFilterSelectorView()
    {
        setDefaultExchange();
        if (exchangeCompactSpinnerDTOs != null)
        {
            filterSelectorView.setUpExchangeSpinner(exchangeCompactSpinnerDTOs);
        }
        filterSelectorView.apply(trendingFilterTypeDTO);
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

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }

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
        else
        {
            throw new IllegalArgumentException("Unhandled item " + item);
        }
    }

    private void handleExtraTileItemOnClick(@NonNull TileType item, View view)
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
                handleProviderTileOnClick((ProviderTileView) view);
                break;
        }
    }

    private void handleProviderTileOnClick(ProviderTileView view)
    {
        int providerId = view.getProviderId();
        ProviderDTO providerDTO = CollectionUtils.first(providerDTOs, candidate -> candidate.id == providerId);
        handleCompetitionItemClicked(providerDTO);
    }

    private void handleCompetitionItemClicked(@Nullable ProviderDTO providerDTO)
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
        AndroidObservable.bindFragment(this, userProfileCache.get(currentUserId.toUserBaseKey()))
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
}
