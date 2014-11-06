package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheRx;
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
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.metrics.events.TrendingStockEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import timber.log.Timber;

@Routable("trending-securities")
public class TrendingFragment extends SecurityListRxFragment<SecurityItemView>
        implements WithTutorial
{
    @Inject Lazy<ExchangeCompactListCacheRx> exchangeCompactListCache;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<ProviderCacheRx> providerCache;
    @Inject Lazy<ProviderListCacheRx> providerListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ExchangeCompactDTOUtil exchangeCompactDTOUtil;
    @Inject Analytics analytics;

    @InjectView(R.id.trending_filter_selector_view) protected TrendingFilterSelectorView filterSelectorView;

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
        this.filterSelectorView.setChangedListener(createTrendingFilterChangedListener());
        this.listView.setAdapter(wrapperAdapter);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchExchangeList();
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
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        filterSelectorView.setChangedListener(null);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        wrapperAdapter = null;
        super.onDestroy();
    }

    @Override protected int getFragmentLayoutResId()
    {
        return R.layout.fragment_trending;
    }

    private void fetchExchangeList()
    {
        ExchangeListType key = new ExchangeListType();
        AndroidObservable.bindFragment(this, exchangeCompactListCache.get().get(key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createExchangeListTypeFetchObserver());
    }

    protected void fetchProviderList()
    {
        AndroidObservable.bindFragment(this, providerListCache.get().get(new ProviderListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProviderListFetchObserver());
    }

    protected Observer<Pair<ProviderListKey, ProviderDTOList>> createProviderListFetchObserver()
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
            scheduleRequestData();
        }
    }

    @Override protected SecurityItemViewAdapterNew createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew(getActivity(), R.layout.trending_security_item);
    }

    protected ExtraTileAdapter createSecurityItemViewAdapter()
    {
        //return simpleSecurityItemViewAdapter;
        // use above adapter to disable extra tile on the trending screen
        return new ExtraTileAdapter(getActivity(), itemViewAdapter);
    }

    @Override protected DTOCacheRx<SecurityListType, SecurityCompactDTOList> getCache()
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

    @Override protected Observer<Pair<SecurityListType, SecurityCompactDTOList>> createListCacheObserver(@NonNull SecurityListType key)
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

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        setUpFilterSelectorView();
        refreshAdapterWithTiles(userProfileDTO.activeSurveyImageURL != null);
    }
    //</editor-fold>

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
}
