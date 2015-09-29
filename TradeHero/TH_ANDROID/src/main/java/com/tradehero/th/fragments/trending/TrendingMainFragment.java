package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.AssetClassDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.base.LollipopArrayAdapter;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.market.ExchangeSpinner;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSpinnerIconAdapter;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.market.ExchangeMarketPreference;
import com.tradehero.th.persistence.prefs.PreferredExchangeMarket;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

@Routable({
        "trending-securities",
        "trending-stocks/tab-index/:stockPageIndex",
        "trending-stocks/exchange/:exchangeId",
        "trending-fx/tab-index/:fxPageIndex",
})
public class TrendingMainFragment extends DashboardFragment
{
    private static final String KEY_ASSET_CLASS = TrendingMainFragment.class.getName() + ".assetClass";
    private static final String KEY_EXCHANGE_ID = TrendingMainFragment.class.getName() + ".exchangeId";

    @Bind(R.id.exchange_selection_menu) ExchangeSpinner exchangeSpinner;
    @Bind(R.id.trending_sort_by) Spinner sortBy;
    @Bind(R.id.exchange_selection_container) View exchangeContainer;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject THRouter thRouter;
    @Inject Toolbar toolbar;
    @Inject Analytics analytics;
    @Inject @PreferredExchangeMarket ExchangeMarketPreference preferredExchangeMarket;
    @Inject ExchangeCompactListCacheRx exchangeCompactListCache;

    @RouteProperty("stockPageIndex") Integer selectedStockPageIndex;
    @RouteProperty("fxPageIndex") Integer selectedFxPageIndex;
    @RouteProperty("exchangeId") Integer routedExchangeId;

    @NonNull private static TrendingTabType lastType = TrendingTabType.STOCK;
    @NonNull private static TrendingStockTabType lastStockTab = TrendingStockTabType.getDefault();

    private boolean fetchedFXPortfolio = false;
    private Observable<UserProfileDTO> userProfileObservable;
    @Nullable private OwnedPortfolioId fxPortfolioId;
    public static boolean fxDialogShowed = false;
    private BaseLiveFragmentUtil trendingLiveFragmentUtil;
    private DTOAdapterNew<ExchangeCompactSpinnerDTO> exchangeAdapter;
    private final BehaviorSubject<ExchangeCompactSpinnerDTO> exchangeSpinnerDTOSubject = BehaviorSubject.create();
    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOList;
    private Spinner assetTypeSpinner;
    private LollipopArrayAdapter<AssetClassDTO> assetTypeAdapter;
    private LollipopArrayAdapter<TrendingStockTabType> sortByAdapter;

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("trending-fx/my-fx", "trending-fx/tab-index/" + TrendingFXTabType.Portfolio.ordinal());
        router.registerAlias("trending-fx/trade-fx", "trending-fx/tab-index/" + TrendingFXTabType.FX.ordinal());
        //router.registerAlias("trending-stocks/my-stocks", "trending-stocks/tab-index/" + TrendingStockTabType.StocksMain.ordinal());
        //router.registerAlias("trending-stocks/favorites", "trending-stocks/tab-index/" + TrendingStockTabType.Favorites.ordinal());
        router.registerAlias("trending-stocks/trending", "trending-stocks/tab-index/" + TrendingStockTabType.Trending.ordinal());
        router.registerAlias("trending-stocks/price-action", "trending-stocks/tab-index/" + TrendingStockTabType.Price.ordinal());
        router.registerAlias("trending-stocks/unusual-volumes", "trending-stocks/tab-index/" + TrendingStockTabType.Volume.ordinal());
        router.registerAlias("trending-stocks/all-trending", "trending-stocks/tab-index/" + TrendingStockTabType.All.ordinal());
    }

    public static void putAssetClass(@NonNull Bundle args, @NonNull AssetClass assetClass)
    {
        args.putInt(KEY_ASSET_CLASS, assetClass.getValue());
    }

    @Nullable private static AssetClass getAssetClass(@NonNull Bundle args)
    {
        if (!args.containsKey(KEY_ASSET_CLASS))
        {
            return null;
        }
        return AssetClass.create(args.getInt(KEY_ASSET_CLASS));
    }

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

    @NonNull public static String getTradeFxPath()
    {
        return "trending-fx/trade-fx";
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        initUserProfileObservable();
    }

    private void initUserProfileObservable()
    {
        userProfileObservable = userProfileCache.getOne(currentUserId.toUserBaseKey())
                .subscribeOn(Schedulers.computation())
                .map(new Func1<Pair<UserBaseKey, UserProfileDTO>, UserProfileDTO>()
                {
                    @Override public UserProfileDTO call(Pair<UserBaseKey, UserProfileDTO> pair)
                    {
                        fetchedFXPortfolio = true;
                        if (pair.second.fxPortfolio == null)
                        {
                            fxPortfolioId = null;
                        }
                        else
                        {
                            fxPortfolioId = pair.second.fxPortfolio.getOwnedPortfolioId();
                        }
                        return pair.second;
                    }
                })
                .cache(1);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getArguments().remove(KEY_EXCHANGE_ID);
        AssetClass askedAssetClass = getAssetClass(getArguments());
        if (askedAssetClass != null)
        {
            try
            {
                lastType = TrendingTabType.getForAssetClass(askedAssetClass);
            }
            catch (IllegalArgumentException e)
            {
                Timber.e(e, "Unhandled assetClass for user " + currentUserId.get());
            }
        }
        ArrayList<AssetClassDTO> assetClassDTOs = new ArrayList<>(2);
        assetClassDTOs.add(new AssetClassDTO(AssetClass.STOCKS));
        assetClassDTOs.add(new AssetClassDTO(AssetClass.FX));
        assetTypeAdapter =
                new LollipopArrayAdapter<>(getActivity(), R.layout.dropdown_item_title_selected, R.layout.dropdown_item_title, assetClassDTOs);

        exchangeAdapter = new TrendingFilterSpinnerIconAdapter(
                getActivity(),
                R.layout.trending_filter_spinner_item_short);
        exchangeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);

        sortByAdapter = new LollipopArrayAdapter<>(getActivity(), R.layout.trending_sort_item_selected, R.layout.trending_sort_item,
                new ArrayList<>(Arrays.asList(TrendingStockTabType.values())));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.trending_main_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        trendingLiveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);
        setupExchangeSpinner();
        setupSortBy();
        initViews();
        analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Trade));
    }

    private void initViews()
    {
        if (fxPortfolioId == null)
        {
            lastType = TrendingTabType.STOCK;
        }
        if (lastType == null)
        {
            Timber.e(new NullPointerException("Gotcha lastType is null"), "lastType is null");
        }

        if (lastType == TrendingTabType.FX)
        {
            exchangeContainer.setVisibility(View.GONE);

            Fragment created = Fragment.instantiate(getActivity(), TrendingFXFragment.class.getName());
            getChildFragmentManager().beginTransaction().replace(R.id.trending_fragment_container, created).commit();
        }
        else
        {
            exchangeContainer.setVisibility(View.VISIBLE);
            int position = sortByAdapter.getPosition(lastStockTab);
            if (position >= 0)
            {
                handleSortBySelected(lastStockTab);
            }
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        thRouter.inject(this, getArguments());
        trendingLiveFragmentUtil.onResume();
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return lastType.equals(TrendingTabType.STOCK);
    }

    @Override public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        super.onLiveTradingChanged(event);
        if (event.isOn && event.isFromUser)
        {
            trendingLiveFragmentUtil.launchPrompt();
        }
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
    }

    @Override public void onPause()
    {
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        trendingLiveFragmentUtil.onDestroyView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.exchangeAdapter = null;
        this.assetTypeSpinner = null;
        this.exchangeSpinner = null;
        super.onDestroy();
    }

    @Override public void onDetach()
    {
        userProfileObservable = null;
        super.onDetach();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflateCustomToolbarView();
        handlePageRouting();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        trendingLiveFragmentUtil.onActivityResult(requestCode, resultCode, data);
    }

    private void inflateCustomToolbarView()
    {
        if (actionBarOwnerMixin.getActionBar() != null)
        {
            View view = LayoutInflater.from(actionBarOwnerMixin.getActionBar().getThemedContext())
                    .inflate(R.layout.trending_custom_actionbar, toolbar, false);
            setActionBarTitle("");
            setupStockFxSwitcher(view);

            actionBarOwnerMixin.setCustomView(view);
        }
    }

    private void setupStockFxSwitcher(@NonNull View view)
    {
        assetTypeSpinner = (Spinner) view.findViewById(R.id.stock_type_dropdown);
        assetTypeSpinner.setAdapter(assetTypeAdapter);
        assetTypeSpinner.setSelection(lastType == TrendingTabType.STOCK ? 0 : 1, false);
        onDestroyOptionsMenuSubscriptions.add(AdapterViewObservable.selects(assetTypeSpinner)
                .ofType(OnItemSelectedEvent.class)
                .subscribe(new Action1<OnItemSelectedEvent>()
                {
                    @Override public void call(OnItemSelectedEvent onItemSelectedEvent)
                    {
                        final TrendingTabType oldType = lastType;
                        final AssetClassDTO assetClassDTO = assetTypeAdapter.getItem(onItemSelectedEvent.position);
                        final ProgressDialog progressDialog;
                        if (!fetchedFXPortfolio && userProfileCache.getCachedValue(currentUserId.toUserBaseKey()) == null)
                        {
                            progressDialog =
                                    ProgressDialog.show(getActivity(), getString(R.string.loading_loading),
                                            getString(R.string.alert_dialog_please_wait));
                            progressDialog.setCanceledOnTouchOutside(true);
                        }
                        else
                        {
                            progressDialog = null;
                        }
                        Action0 dismissProgress = new DismissDialogAction0(progressDialog);

                        // We want to identify whether to:
                        // - wait for enough info
                        // - pop for FX enroll
                        // - just change the tab

                        if (userProfileObservable == null)
                        {
                            initUserProfileObservable();
                        }
                        onDestroyOptionsMenuSubscriptions.add(AppObservable.bindSupportFragment(
                                TrendingMainFragment.this,
                                userProfileObservable)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnUnsubscribe(dismissProgress)
                                .finallyDo(dismissProgress)
                                .subscribe(new Subscriber<UserProfileDTO>()
                                {
                                    @Override public void onCompleted()
                                    {

                                    }

                                    @Override public void onError(Throwable e)
                                    {
                                        THToast.show(getString(R.string.error_fetch_your_user_profile));
                                    }

                                    @Override public void onNext(UserProfileDTO userProfileDTO)
                                    {
                                        if (assetClassDTO.assetClass == AssetClass.FX && userProfileDTO.fxPortfolio == null && fxPortfolioId == null)
                                        {
                                            if (fxDialogShowed)
                                            {
                                                return;
                                            }
                                            else
                                            {
                                                fxDialogShowed = true;
                                            }
                                            final FxOnBoardDialogFragment onBoardDialogFragment =
                                                    FxOnBoardDialogFragment.showOnBoardDialog(getActivity().getSupportFragmentManager());
                                            onBoardDialogFragment.getUserActionTypeObservable()
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                            new Action1<FxOnBoardDialogFragment.UserAction>()
                                                            {
                                                                @Override public void call(FxOnBoardDialogFragment.UserAction action)
                                                                {
                                                                    handleUserEnrolledFX(action);
                                                                }
                                                            },
                                                            new TimberOnErrorAction1("")
                                                    );
                                        }
                                        else
                                        {
                                            if (assetClassDTO.assetClass == AssetClass.STOCKS)
                                            {
                                                lastType = TrendingTabType.STOCK;
                                            }
                                            else
                                            {
                                                lastType = TrendingTabType.FX;
                                            }
                                            if (!oldType.equals(lastType))
                                            {
                                                //clearChildFragmentManager();
                                                initViews();
                                                getActivity().supportInvalidateOptionsMenu();
                                            }
                                        }
                                    }
                                }));
                    }
                }, new TimberOnErrorAction1("Error on dropdown of asset type")));
    }

    private void setupExchangeSpinner()
    {
        exchangeSpinner.setAdapter(exchangeAdapter);

        //TODO set selection

        exchangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                onExchangeSelected(parent, view, position, id);
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        ExchangeListType key = new ExchangeListType();
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                Observable.combineLatest(
                        exchangeCompactListCache.getOne(key)
                                .map(new PairGetSecond<ExchangeListType, ExchangeCompactDTOList>())
                                .map(new Func1<ExchangeCompactDTOList, ExchangeCompactSpinnerDTOList>()
                                {
                                    @Override public ExchangeCompactSpinnerDTOList call(ExchangeCompactDTOList exchangeDTOs)
                                    {
                                        ExchangeCompactSpinnerDTOList spinnerList = new ExchangeCompactSpinnerDTOList(
                                                getResources(),
                                                ExchangeCompactDTOUtil.filterAndOrderForTrending(
                                                        exchangeDTOs,
                                                        new ExchangeCompactDTODescriptionNameComparator<>()));
                                        // Adding the "All" choice
                                        spinnerList.add(0, new ExchangeCompactSpinnerDTO(getResources()));
                                        return spinnerList;
                                    }
                                })
                                .startWith(exchangeCompactSpinnerDTOList != null
                                        ? Observable.just(exchangeCompactSpinnerDTOList)
                                        : Observable.<ExchangeCompactSpinnerDTOList>empty())
                                .distinctUntilChanged(),
                        userProfileCache.getOne(currentUserId.toUserBaseKey()).map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                        new Func2<ExchangeCompactSpinnerDTOList, UserProfileDTO, Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO>>()
                        {
                            @Override
                            public Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO> call(
                                    ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs,
                                    UserProfileDTO userProfileDTO)
                            {
                                Country defaultCountry = userProfileDTO.getCountry();
                                return Pair.create(
                                        exchangeCompactSpinnerDTOs,
                                        defaultCountry == null
                                                ? null
                                                : exchangeCompactSpinnerDTOs.findFirstDefaultFor(defaultCountry));
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO>>()
                        {
                            @Override public void call(Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO> pair)
                            {
                                exchangeCompactSpinnerDTOList = pair.first;
                                exchangeAdapter.addAll(pair.first);
                                exchangeAdapter.notifyDataSetChanged();
                                handleExchangeRouting(pair.second);
                            }
                        },
                        new TimberAndToastOnErrorAction1(
                                getString(R.string.error_fetch_exchange_list_info),
                                "Error fetching the list of exchanges")));
    }

    protected void onExchangeSelected(AdapterView<?> parent, View view, int position, long id)
    {
        ExchangeCompactSpinnerDTO dto = (ExchangeCompactSpinnerDTO) parent.getItemAtPosition(position);
        preferredExchangeMarket.set(dto.getExchangeIntegerId());
        exchangeSpinnerDTOSubject.onNext(dto);
    }

    private void setupSortBy()
    {
        sortBy.setAdapter(sortByAdapter);

        onDestroyViewSubscriptions.add(AdapterViewObservable.selects(sortBy)
                .ofType(OnItemSelectedEvent.class)
                .subscribe(new Action1<OnItemSelectedEvent>()
                {
                    @Override public void call(OnItemSelectedEvent onItemSelectedEvent)
                    {
                        TrendingStockTabType tabType = sortByAdapter.getItem(onItemSelectedEvent.position);
                        handleSortBySelected(tabType);
                    }
                }, new TimberOnErrorAction1("")));
    }

    protected void handleSortBySelected(TrendingStockTabType tabType)
    {
        Bundle args = new Bundle();
        TrendingStockFragment.putTabType(args, tabType);
        Fragment created = Fragment.instantiate(getActivity(), TrendingStockFragment.class.getName(), args);
        getChildFragmentManager().beginTransaction().replace(R.id.trending_fragment_container, created).commit();
        lastStockTab = tabType;
    }

    protected void handleUserEnrolledFX(@NonNull FxOnBoardDialogFragment.UserAction userAction)
    {
        if (userAction.type.equals(FxOnBoardDialogFragment.UserActionType.ENROLLED))
        {
            //noinspection ConstantConditions
            fxPortfolioId = userAction.created.getOwnedPortfolioId();
            if (fxPortfolioId != null)
            {
                userProfileCache.invalidate(currentUserId.toUserBaseKey());
            }
            lastType = TrendingTabType.FX;
        }
        else
        {
            lastType = TrendingTabType.STOCK;
        }
        clearChildFragmentManager();
        initViews();
    }

    protected void clearChildFragmentManager()
    {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null)
        {
            fragments.clear();
        }
    }

    protected void handlePageRouting()
    {
        //BaseActivity activity = (BaseActivity) getActivity();
        //if (selectedStockPageIndex != null)
        //{
        //    if (lastType.equals(TrendingTabType.STOCK))
        //    {
        //        if (tabViewPager != null)
        //        {
        //            lastStockTab = TrendingStockTabType.values()[selectedStockPageIndex];
        //            tabViewPager.setCurrentItem(selectedStockPageIndex, true);
        //            selectedStockPageIndex = null;
        //        }
        //    }
        //    else if (actionBarOwnerMixin != null && activity != null)
        //    {
        //        lastType = TrendingTabType.STOCK;
        //        //stockFxSwitcher.setIsOn(false, false);
        //    }
        //}
        //else if (selectedFxPageIndex != null)
        //{
        //    if (lastType.equals(TrendingTabType.FX))
        //    {
        //        if (tabViewPager != null)
        //        {
        //            lastFXTab = TrendingFXTabType.values()[selectedFxPageIndex];
        //            tabViewPager.setCurrentItem(selectedFxPageIndex, true);
        //            selectedFxPageIndex = null;
        //        }
        //    }
        //    //else if (stockFxSwitcher != null)
        //    //{
        //    //    stockFxSwitcher.setIsOn(true, false);
        //    //}
        //}
        //else if (lastType.equals(TrendingTabType.STOCK))
        //{
        //    tabViewPager.setCurrentItem(lastStockTab.ordinal(), true);
        //}
        //else if (lastType.equals(TrendingTabType.FX))
        //{
        //    tabViewPager.setCurrentItem(lastFXTab.ordinal(), true);
        //}
        //else
        //{
        //    throw new RuntimeException("Unhandled TrendingTabType." + lastType);
        //}
        //clearRoutingParam();
    }

    private void handleExchangeRouting(@Nullable ExchangeCompactDTO defaultValue)
    {
        if (routedExchangeId != null
                && lastType.equals(TrendingTabType.STOCK))
        {
            exchangeSpinner.setSelectionById(new ExchangeIntegerId(routedExchangeId));
            routedExchangeId = null;
        }
        else if (lastType.equals(TrendingTabType.STOCK) && preferredExchangeMarket.get() > 0)
        {
            exchangeSpinner.setSelectionById(new ExchangeIntegerId(preferredExchangeMarket.get()));
        }
        else if (defaultValue != null)
        {
            exchangeSpinner.setSelectionById(defaultValue.getExchangeIntegerId());
        }
    }

    private void clearRoutingParam()
    {
        //TODO to static
        getArguments().remove("stockPageIndex");
        getArguments().remove("fxPageIndex");
        getArguments().remove("exchangeId");
    }

    public Observable<ExchangeCompactSpinnerDTO> getExchangeSelectionObservable()
    {
        return exchangeSpinnerDTOSubject.asObservable();
    }
}
