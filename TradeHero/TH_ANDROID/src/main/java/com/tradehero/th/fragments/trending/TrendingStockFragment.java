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
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.LiveActivityUtil;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
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
import com.tradehero.th.fragments.security.SecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trade.AbstractBuySellFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.LiveBuySellFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTOFactory;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ProfileEvent;
import com.tradehero.th.utils.metrics.events.TrendingFilterEvent;
import com.tradehero.th.utils.metrics.events.TrendingStockEvent;
import com.tradehero.th.widget.LiveWidgetScrollListener;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import timber.log.Timber;

public class TrendingStockFragment extends TrendingBaseFragment
        implements WithTutorial
{
    private static final String KEY_EXCHANGE_ID = TrendingMainFragment.class.getName() + ".exchangeId";
    private static final String KEY_TAB_TYPE_ID = TrendingMainFragment.class.getName() + ".tabTypeId";

    @Inject ProviderListCacheRx providerListCache;
    @Inject Analytics analytics;
    @Inject ProviderUtil providerUtil;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject LiveActivityUtil liveActivityUtil;

    private ProviderDTOList providerDTOs;

    private ExtraTileAdapterNew wrapperAdapter;
    @NonNull private TrendingFilterTypeDTO trendingFilterTypeDTO;
    @Inject protected THBillingInteractorRx userInteractorRx;
    private Subscription exchangeSubscription;

    //TODO: Dummy temporary, pending for server
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;

    public static void putTabType(@NonNull Bundle args, @NonNull TrendingStockSortType tabType)
    {
        args.putInt(KEY_TAB_TYPE_ID, tabType.ordinal());
    }

    @NonNull private static TrendingStockSortType getTabType(@NonNull Bundle args)
    {
        return TrendingStockSortType.values()[args.getInt(KEY_TAB_TYPE_ID, TrendingStockSortType.getDefault().ordinal())];
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getArguments().remove(KEY_EXCHANGE_ID);
        trendingFilterTypeDTO = TrendingFilterTypeDTOFactory.create(getTabType(getArguments()), getResources());
        wrapperAdapter = createSecurityItemViewAdapter();
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
        fetchUserProfile();
        fetchProviderList();
        fetchWatchlist();
        fetchAlertCompactList();

        if (getParentFragment() instanceof  TrendingMainFragment)
        {
            LiveWidgetScrollListener liveWidgetScrollListener = new LiveWidgetScrollListener(fragmentElements.get(), ((TrendingMainFragment) getParentFragment()).getTrendingLiveFragmentUtil());
            this.listView.setOnScrollListener(
                    new MultiScrollListener(nearEndScrollListener, fragmentElements.get().getListViewScrollListener(), liveWidgetScrollListener));
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        if (getParentFragment() != null && getParentFragment() instanceof TrendingMainFragment)
        {
            exchangeSubscription =
                    ((TrendingMainFragment) getParentFragment()).getExchangeSelectionObservable()
                            .distinctUntilChanged()
                            .map(new Func1<ExchangeCompactSpinnerDTO, TrendingFilterTypeDTO>()
                            {
                                @Override public TrendingFilterTypeDTO call(ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
                                {
                                    return trendingFilterTypeDTO.getByExchange(exchangeCompactSpinnerDTO);
                                }
                            })
                            .doOnNext(new Action1<TrendingFilterTypeDTO>()
                            {
                                @Override public void call(TrendingFilterTypeDTO trendingFilterTypeDTO)
                                {
                                    analytics.fireEvent(new TrendingFilterEvent(trendingFilterTypeDTO));
                                    if (Constants.RELEASE)
                                    {
                                        analytics.localytics().setProfileAttribute(new ProfileEvent(
                                                AnalyticsConstants.InterestedExchange,
                                                Collections.singletonList(trendingFilterTypeDTO.exchange.name)));
                                    }
                                }
                            })
                            .subscribe(
                                    new Action1<TrendingFilterTypeDTO>()
                                    {
                                        @Override public void call(TrendingFilterTypeDTO trendingFilterTypeDTO)
                                        {
                                            fetchListByFilter(trendingFilterTypeDTO);
                                        }
                                    },
                                    new TimberOnErrorAction1("Failed to listen to exchange in trendingStock"));
        }
    }

    @Override public void onPause()
    {
        exchangeSubscription.unsubscribe();
        exchangeSubscription = null;
        super.onPause();
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
                pushSearchIn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroy()
    {
        wrapperAdapter = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
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
        //ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO = new ExchangeCompactSpinnerDTO();


        boolean hasChanged = !trendingFilterTypeDTO.equals(this.trendingFilterTypeDTO);
        this.trendingFilterTypeDTO = trendingFilterTypeDTO;
        if (hasChanged)
        {
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
    }

    private void fetchUserProfile()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
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
                        new TimberAndToastOnErrorAction1(getString(R.string.error_fetch_user_profile), "Failed to fetch user profile")));
    }

    private void linkWith(UserProfileDTO userProfileDTO)
    {
        wrapperAdapter.setSurveyEnabled(userProfileDTO.activeSurveyImageURL != null);
    }

    private void fetchProviderList()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
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
                        new ToastOnErrorAction1(getString(R.string.error_fetch_provider_competition_list))));
    }

    protected void linkWith(@NonNull ProviderDTOList providers)
    {
        providerDTOs = providers;
        wrapperAdapter.setProviderEnabled(!providerDTOs.isEmpty());
    }

    private void fetchWatchlist()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
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
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
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
        liveActivityUtil.onTrendingTileClicked(item);
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
            OwnedPortfolioId applicablePortfolioId = providerDTO.getAssociatedOwnedPortfolioId();
            if (applicablePortfolioId != null)
            {
                MainCompetitionFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
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
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
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
                        new ToastOnErrorAction1()));
    }

    private void handleResetPortfolioItemOnClick()
    {
        //noinspection unchecked
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        Actions.empty(),
                        new ToastOnErrorAction1()));
    }

    protected void handleExtraCashItemOnClick()
    {
        //noinspection unchecked
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        Actions.empty(),
                        new ToastOnErrorAction1()));
    }

    private void handleEarnCreditItemOnClick()
    {
        navigator.get().pushFragment(FriendsInvitationFragment.class);
    }

    private void handleSecurityItemOnClick(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        analytics.fireEvent(new TrendingStockEvent(securityCompactDTO.getSecurityId()));

        Bundle args = new Bundle();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        final AbstractBuySellFragment.Requisite requisite;
        if (applicablePortfolioId != null)
        {
            requisite = new AbstractBuySellFragment.Requisite(
                    securityCompactDTO.getSecurityId(),
                    applicablePortfolioId,
                    0);
        }
        else
        {
            requisite = new AbstractBuySellFragment.Requisite(
                    securityCompactDTO.getSecurityId(),
                    new Bundle(),
                    portfolioCompactListCache,
                    currentUserId);
        }
        BuySellStockFragment.putRequisite(args, requisite);

        //if (isLiveTrading.get())
        //{
        //    navigator.get().pushFragment(LiveBuySellFragment.class, args);
        //}
        //else
        //{
        //    navigator.get().pushFragment(BuySellStockFragment.class, args);
        //}
        navigator.get().pushFragment(BuySellStockFragment.class, args);

    }

    @Override protected void populateArgumentForSearch(@NonNull Bundle args)
    {
        super.populateArgumentForSearch(args);
        SecuritySearchFragment.putAssetClass(args, AssetClass.STOCKS);
    }
}
