package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.android.internal.util.Predicate;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.market.ExchangeSpinner;
import com.tradehero.th.fragments.security.SecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSpinnerIconAdapter;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTOFactory;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.market.ExchangeMarketPreference;
import com.tradehero.th.persistence.prefs.PreferredExchangeMarket;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ProfileEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.metrics.events.TrendingFilterEvent;
import com.tradehero.th.utils.metrics.events.TrendingStockEvent;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import timber.log.Timber;

public class TrendingStockFragment extends TrendingBaseFragment
        implements WithTutorial
{
    private static final String KEY_EXCHANGE_ID = TrendingMainFragment.class.getName() + ".exchangeId";
    private static final String KEY_TAB_TYPE_ID = TrendingMainFragment.class.getName() + ".tabTypeId";

    @Inject ExchangeCompactListCacheRx exchangeCompactListCache;
    @Inject ProviderListCacheRx providerListCache;
    @Inject Analytics analytics;
    @Inject @PreferredExchangeMarket ExchangeMarketPreference preferredExchangeMarket;
    @Inject ProviderUtil providerUtil;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;

    private DTOAdapterNew<ExchangeCompactSpinnerDTO> exchangeAdapter;

    private UserProfileDTO userProfileDTO;
    private ProviderDTOList providerDTOs;
    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs;
    @Nullable private ExchangeIntegerId exchangeIdFromArguments;
    @NonNull private TrendingFilterTypeDTO trendingFilterTypeDTO;

    private ExtraTileAdapterNew wrapperAdapter;
    @Inject protected THBillingInteractorRx userInteractorRx;
    private MenuItem exchangeMenu;
    private ExchangeSpinner mExchangeSelection;

    public static void putExchangeId(@NonNull Bundle args, @NonNull ExchangeIntegerId exchangeId)
    {
        args.putBundle(KEY_EXCHANGE_ID, exchangeId.getArgs());
    }

    @Nullable private static ExchangeIntegerId getExchangeId(@NonNull Bundle args)
    {
        if (!args.containsKey(KEY_EXCHANGE_ID))
        {
            return null;
        }
        return new ExchangeIntegerId(args.getBundle(KEY_EXCHANGE_ID));
    }

    public static void putTabType(@NonNull Bundle args, @NonNull TrendingStockTabType tabType)
    {
        args.putInt(KEY_TAB_TYPE_ID, tabType.ordinal());
    }

    @NonNull private static TrendingStockTabType getTabType(@NonNull Bundle args)
    {
        return TrendingStockTabType.values()[args.getInt(KEY_TAB_TYPE_ID, TrendingStockTabType.getDefault().ordinal())];
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exchangeIdFromArguments = getExchangeId(getArguments());
        getArguments().remove(KEY_EXCHANGE_ID);
        trendingFilterTypeDTO = TrendingFilterTypeDTOFactory.create(getTabType(getArguments()), getResources());
        wrapperAdapter = createSecurityItemViewAdapter();

        exchangeAdapter = new TrendingFilterSpinnerIconAdapter(
                getActivity(),
                R.layout.trending_filter_spinner_item_short);
        exchangeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_trending, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.listView.setAdapter(wrapperAdapter);
        analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Trade));
        fetchExchangeList();
        fetchUserProfile();
        fetchProviderList();
        fetchWatchlist();
        fetchAlertCompactList();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.exchange_menu, menu);
        inflater.inflate(R.menu.search_menu, menu);

        exchangeMenu = menu.findItem(R.id.btn_exchange);
        View updateCenterIcon = exchangeMenu.getActionView();
        if (updateCenterIcon != null)
        {
            mExchangeSelection = (ExchangeSpinner) updateCenterIcon.findViewById(R.id.exchange_selection_menu);
            mExchangeSelection.setAdapter(exchangeAdapter);
            mExchangeSelection.setSelectionById(preferredExchangeMarket.get());
            mExchangeSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    onExchangeSelected(parent, view, position, id);
                }

                @Override public void onNothingSelected(AdapterView<?> parent)
                {
                }
            });
        }
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

    @Override public void onDestroy()
    {
        exchangeAdapter = null;
        wrapperAdapter = null;
        super.onDestroy();
    }

    @Override @NonNull protected SecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(getActivity(), R.layout.trending_security_item);
    }

    @NonNull protected ExtraTileAdapterNew createSecurityItemViewAdapter()
    {
        return new ExtraTileAdapterNew(getActivity(), itemViewAdapter);
    }

    protected void fetchListByFilter(@NonNull TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        boolean hasChanged = !trendingFilterTypeDTO.equals(this.trendingFilterTypeDTO);
        this.trendingFilterTypeDTO = trendingFilterTypeDTO;
        if (hasChanged)
        {
            preferredExchangeMarket.set(trendingFilterTypeDTO.exchange.getExchangeIntegerId());
            scheduleRequestData();
        }
        else
        {
            if (nearEndScrollListener != null)
            {
                nearEndScrollListener.lowerEndFlag();
                nearEndScrollListener.activateEnd();
            }
            requestDtos();
        }
        if (exchangeCompactSpinnerDTOs != null && mExchangeSelection != null)
        {
            mExchangeSelection.setSelectionById(trendingFilterTypeDTO.exchange.id);
        }
    }

    private void fetchExchangeList()
    {
        ExchangeListType key = new ExchangeListType();
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                exchangeCompactListCache.getOne(key)
                        .map(new PairGetSecond<ExchangeListType, ExchangeCompactDTOList>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ExchangeCompactDTOList>()
                        {
                            @Override public void call(ExchangeCompactDTOList list)
                            {
                                linkWith(list);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_exchange_list_info),
                                "Error fetching the list of exchanges")));
    }

    private void linkWith(@NonNull ExchangeCompactDTOList exchangeDTOs)
    {
        ExchangeCompactSpinnerDTOList spinnerList = new ExchangeCompactSpinnerDTOList(
                getResources(),
                ExchangeCompactDTOUtil.filterAndOrderForTrending(
                        exchangeDTOs,
                        new ExchangeCompactDTODescriptionNameComparator<>()));
        // Adding the "All" choice
        spinnerList.add(0, new ExchangeCompactSpinnerDTO(getResources()));
        linkWith(spinnerList);
    }

    private void linkWith(@NonNull ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs)
    {
        this.exchangeCompactSpinnerDTOs = exchangeCompactSpinnerDTOs;
        exchangeAdapter.clear();
        exchangeAdapter.addAll(exchangeCompactSpinnerDTOs);
        exchangeAdapter.notifyDataSetChanged();
    }

    private void fetchUserProfile()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profileDTO)
                            {
                                linkWith(profileDTO);
                            }
                        },
                        new ToastAndLogOnErrorAction(getString(R.string.error_fetch_user_profile), "Failed to fetch user profile")));
    }

    private void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        wrapperAdapter.setSurveyEnabled(userProfileDTO.activeSurveyImageURL != null);
        setUpFilterSelectorView();
    }

    private void fetchProviderList()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                providerListCache.get(new ProviderListKey())
                        .map(new PairGetSecond<ProviderListKey, ProviderDTOList>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ProviderDTOList>()
                        {
                            @Override public void call(ProviderDTOList list)
                            {
                                linkWith(list);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_provider_competition_list))));
    }

    protected void linkWith(@NonNull ProviderDTOList providers)
    {
        providerDTOs = providers;
        wrapperAdapter.setProviderEnabled(!providerDTOs.isEmpty());
    }

    private void fetchWatchlist()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                        this,
                        userWatchlistPositionCache.get(currentUserId.toUserBaseKey()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Pair<UserBaseKey, WatchlistPositionDTOList>>()
                        {
                            @Override public void onCompleted()
                            {

                            }

                            @Override public void onError(Throwable e)
                            {
                                Timber.e(e, "Failed to fetch list of watch list items");
                                THToast.show(R.string.error_fetch_portfolio_list_info);
                            }

                            @Override public void onNext(Pair<UserBaseKey, WatchlistPositionDTOList> userBaseKeyWatchlistPositionDTOListPair)
                            {
                                if (itemViewAdapter != null)
                                {
                                    ((SecurityPagedViewDTOAdapter) itemViewAdapter).setWatchList(userBaseKeyWatchlistPositionDTOListPair.second);
                                    ((SecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
                                }
                            }
                        })
        );
    }

    public void fetchAlertCompactList()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                alertCompactListCache.getSecurityMappedAlerts(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map<SecurityId, AlertCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "There was an error getting the alert ids");
                    }

                    @Override public void onNext(Map<SecurityId, AlertCompactDTO> securityIdAlertIdMap)
                    {
                        if (itemViewAdapter != null)
                        {
                            ((SecurityPagedViewDTOAdapter) itemViewAdapter).setAlertList(securityIdAlertIdMap);
                            ((SecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
                        }
                    }
                })
        );
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

    @Override protected void startAnew()
    {
        wrapperAdapter.clearExtraTiles();
        super.startAnew();
    }

    private void setUpFilterSelectorView()
    {
        if (exchangeCompactSpinnerDTOs != null)
        {
            final ExchangeIntegerId preferredExchangeId;
            if (exchangeIdFromArguments != null)
            {
                preferredExchangeId = exchangeIdFromArguments;
                exchangeIdFromArguments = null;
            }
            else
            {
                if (userProfileDTO != null)
                {
                    preferredExchangeMarket.setDefaultIfUnset(exchangeCompactSpinnerDTOs, userProfileDTO);
                }
                preferredExchangeId = preferredExchangeMarket.getExchangeIntegerId();
            }
            ExchangeCompactSpinnerDTO initial = exchangeCompactSpinnerDTOs.findFirstWhere(
                    new Predicate<ExchangeCompactSpinnerDTO>()
                    {
                        @Override public boolean apply(ExchangeCompactSpinnerDTO exchange)
                        {
                            return exchange.getExchangeIntegerId().equals(preferredExchangeId);
                        }
                    });
            if (initial != null)
            {
                fetchListByFilter(trendingFilterTypeDTO.getByExchange(initial));
            }
        }
    }

    protected void onExchangeSelected(AdapterView<?> parent, View view, int position, long id)
    {
        TrendingFilterTypeDTO newFilterTypeDTO = trendingFilterTypeDTO.getByExchange((ExchangeCompactSpinnerDTO) parent.getItemAtPosition(position));
        fetchListByFilter(newFilterTypeDTO);
        reportAnalytics();
    }

    private void reportAnalytics()
    {
        analytics.fireEvent(new TrendingFilterEvent(trendingFilterTypeDTO));
        if (Constants.RELEASE)
        {
            analytics.localytics().setProfileAttribute(new ProfileEvent(
                    AnalyticsConstants.InterestedExchange,
                    Collections.singletonList(trendingFilterTypeDTO.exchange.name)));
        }
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (isDetached() || item == null)
        {
            return;
        }
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
        final int providerId = view.getProviderId();
        ProviderDTO providerDTO = CollectionUtils.first(providerDTOs, new Predicate<ProviderDTO>()
        {
            @Override public boolean apply(ProviderDTO candidate)
            {
                return candidate.id == providerId;
            }
        });
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
            Bundle args = new Bundle();
            CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                    providerDTO.getProviderId()
            ));
            navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
        }
    }

    private void handleSurveyItemOnClick()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                        .first())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profile)
                            {
                                if (profile.activeSurveyURL != null)
                                {
                                    Bundle bundle = new Bundle();
                                    WebViewFragment.putUrl(bundle, profile.activeSurveyURL);
                                    navigator.get().pushFragment(WebViewFragment.class, bundle, null);
                                }
                            }
                        },
                        new ToastOnErrorAction()));
    }

    private void handleResetPortfolioItemOnClick()
    {
        //noinspection unchecked
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        Actions.empty(),
                        new ToastOnErrorAction()));
    }

    protected void handleExtraCashItemOnClick()
    {
        //noinspection unchecked
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        Actions.empty(),
                        new ToastOnErrorAction()));
    }

    private void handleEarnCreditItemOnClick()
    {
        navigator.get().pushFragment(FriendsInvitationFragment.class);
    }

    private void handleSecurityItemOnClick(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        analytics.fireEvent(new TrendingStockEvent(securityCompactDTO.getSecurityId()));

        Bundle args = new Bundle();
        BuySellStockFragment.putSecurityId(args, securityCompactDTO.getSecurityId());

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

        if (ownedPortfolioId != null)
        {
            BuySellStockFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        navigator.get().pushFragment(BuySellStockFragment.class, args);
    }

    @Override protected void populateArgumentForSearch(@NonNull Bundle args)
    {
        super.populateArgumentForSearch(args);
        SecuritySearchFragment.putAssetClass(args, AssetClass.STOCKS);
    }
}
