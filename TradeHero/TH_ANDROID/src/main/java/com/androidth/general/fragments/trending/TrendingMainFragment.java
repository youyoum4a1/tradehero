package com.androidth.general.fragments.trending;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.common.SlidingTabLayout;
import com.androidth.general.BuildConfig;
import com.androidth.general.R;
import com.androidth.general.activities.BaseActivity;
import com.androidth.general.adapters.DTOAdapterNew;
import com.androidth.general.api.market.Country;
import com.androidth.general.api.market.ExchangeCompactDTO;
import com.androidth.general.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.androidth.general.api.market.ExchangeCompactDTOList;
import com.androidth.general.api.market.ExchangeCompactDTOUtil;
import com.androidth.general.api.market.ExchangeIntegerId;
import com.androidth.general.api.market.ExchangeListType;
import com.androidth.general.api.portfolio.AssetClass;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.CompositeExchangeSecurityDTO;
import com.androidth.general.api.security.SecurityTypeDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.DisplayNameDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.base.TrendingLiveFragmentUtil;
import com.androidth.general.fragments.fxonboard.FxOnBoardDialogFragment;
import com.androidth.general.fragments.market.ExchangeSpinner;
import com.androidth.general.fragments.market.SecurityTypeSpinner;
import com.androidth.general.fragments.position.FXMainPositionListFragment;
import com.androidth.general.fragments.trending.filter.TrendingFilterSpinnerIconAdapter;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTOList;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.persistence.live.CompositeExchangeSecurityCacheRx;
import com.androidth.general.persistence.market.ExchangeCompactListCacheRx;
import com.androidth.general.persistence.market.ExchangeMarketPreference;
import com.androidth.general.persistence.prefs.PreferredExchangeMarket;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.LiveConstants;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.utils.route.THRouter;
import com.androidth.general.widget.OffOnViewSwitcher;
import com.androidth.general.widget.OffOnViewSwitcherEvent;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @Bind(R.id.pager) ViewPager tabViewPager;
    @Bind(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject THRouter thRouter;
    @Inject Toolbar toolbar;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject @PreferredExchangeMarket ExchangeMarketPreference preferredExchangeMarket;
    @Inject ExchangeCompactListCacheRx exchangeCompactListCache;

    @RouteProperty("stockPageIndex") Integer selectedStockPageIndex;
    @RouteProperty("fxPageIndex") Integer selectedFxPageIndex;
    @RouteProperty("exchangeId") Integer routedExchangeId;

    @NonNull private static TrendingTabType lastType = TrendingTabType.STOCK;
    @NonNull private static TrendingStockTabType lastStockTab = TrendingStockTabType.getDefault();
    @NonNull private static TrendingFXTabType lastFXTab = TrendingFXTabType.getDefault();

    private TradingStockPagerAdapter tradingStockPagerAdapter;
    private TradingFXPagerAdapter tradingFXPagerAdapter;
    private boolean fetchedFXPortfolio = false;
    private Observable<UserProfileDTO> userProfileObservable;
    @Nullable private OwnedPortfolioId fxPortfolioId;
    public static boolean fxDialogShowed = false;
    public TrendingLiveFragmentUtil trendingLiveFragmentUtil;
    private OffOnViewSwitcher stockFxSwitcher;
    private ExchangeSpinner exchangeSpinner;
    private DTOAdapterNew<DTO> exchangeAdapter;
    private DTOAdapterNew<DTO> securitTypeAdapter;
    private BehaviorSubject<ExchangeCompactSpinnerDTO> exchangeSpinnerDTOSubject;
    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOList;

    //Live-related
    private UserProfileDTO userProfileDTO;
    private boolean isInLiveMode;
    private SecurityTypeSpinner securityTypeSpinner;
    @Inject CompositeExchangeSecurityCacheRx compositeExchangeSecurityCacheRx;

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("trending-fx/my-fx", "trending-fx/tab-index/" + TrendingFXTabType.Portfolio.ordinal());
        router.registerAlias("trending-fx/trade-fx", "trending-fx/tab-index/" + TrendingFXTabType.FX.ordinal());
        router.registerAlias("trending-stocks/my-stocks", "trending-stocks/tab-index/" + TrendingStockTabType.StocksMain.ordinal());
        router.registerAlias("trending-stocks/favorites", "trending-stocks/tab-index/" + TrendingStockTabType.Favorites.ordinal());
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

    @Override public void onAttach(Context context)
    {
        super.onAttach(context);
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
        tradingStockPagerAdapter = new TradingStockPagerAdapter(getChildFragmentManager());
        tradingFXPagerAdapter = new TradingFXPagerAdapter(getChildFragmentManager());
        AssetClass askedAssetClass = getAssetClass(getArguments());
        if (askedAssetClass != null)
        {
            try
            {
                lastType = TrendingTabType.getForAssetClass(askedAssetClass);
            } catch (IllegalArgumentException e)
            {
                Timber.e(e, "Unhandled assetClass for user " + currentUserId.get());
            }
        }

        exchangeSpinnerDTOSubject = BehaviorSubject.create();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.trending_main_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        trendingLiveFragmentUtil = new TrendingLiveFragmentUtil(this, view);

        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int i, float v, int i2)
            {
            }

            @Override public void onPageSelected(int i)
            {
                switch (lastType)
                {
                    case STOCK:
                        lastStockTab = TrendingStockTabType.values()[i];
                        break;
                    case FX:
                        lastFXTab = TrendingFXTabType.values()[i];
                        break;
                    default:
                        throw new RuntimeException("Unhandled TrendingTabType." + lastType);
                }
            }

            @Override public void onPageScrollStateChanged(int i)
            {
                if(BuildConfig.HAS_LIVE_ACCOUNT_FEATURE
                        && trendingLiveFragmentUtil!=null){
                    if(trendingLiveFragmentUtil.getLiveFragmentContainer().getVisibility()==View.VISIBLE){
                        trendingLiveFragmentUtil.setCallToActionFragmentGone(tabViewPager);
                    }
                }
            }
        });

        initViews();
        //TODO Change Analytics
        //analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Trade));
    }

    private void initViews()
    {
        if (fxPortfolioId == null)
        {
            lastType = TrendingTabType.STOCK;
        }
        if (tabViewPager == null)
        {
            Timber.e(new NullPointerException("Gotcha TabViewPager is null"), "TabViewPager is null");
        }
        if (lastType == null)
        {
            Timber.e(new NullPointerException("Gotcha lastType is null"), "lastType is null");
        }
        tabViewPager.setAdapter(lastType.equals(TrendingTabType.STOCK) ? tradingStockPagerAdapter : tradingFXPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setDistributeEvenly(!lastType.equals(TrendingTabType.STOCK));
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.general_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
    }

    @Override public void onResume()
    {
        super.onResume();
        thRouter.inject(this, getArguments());
        trendingLiveFragmentUtil.onResume();

        GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_TRENDING_SCREEN);
    }

    @Override public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        super.onLiveTradingChanged(event);
        // TODO force reload cache whenever user toggles

        if(BuildConfig.HAS_LIVE_ACCOUNT_FEATURE && event.isFromUser) {

            userProfileDTO = userProfileCache.getCachedValue(currentUserId.toUserBaseKey());

            ///// securityType and stock/fx spinner settings
            if(event.isOn) { // If LIVE, show securityType and hide stock/fx
                if(securityTypeSpinner!=null){
                    securityTypeSpinner.setVisibility(View.VISIBLE);
                }
                stockFxSwitcher.setVisibility(View.INVISIBLE);
            }
            else // If VIRTUAL, show stock/fx and hide securityType
            {
                stockFxSwitcher.setVisibility(View.VISIBLE);
                if(securityTypeSpinner!=null){
                    securityTypeSpinner.setVisibility(View.INVISIBLE);
                }

            }

            ///// registration page and tab view pages
            if(event.isClickedFromTrending || userProfileDTO.getUserLiveAccounts()==null) {

                LiveConstants.hasLiveAccount = userProfileDTO.getUserLiveAccounts()==null ? false : true;
                /*
                if clicked from trending or doesnt have a live account yet,
                show or unshow registration page
                */
                if(event.isOn){
                    trendingLiveFragmentUtil.setCallToActionFragmentVisible(tabViewPager);

                }else{
                    trendingLiveFragmentUtil.setCallToActionFragmentGone(tabViewPager);
                }

            } else { // user has live account...and not clicked from trending
                //switch from virtual to live, or vice versa
                if(event.isOn) {//switched from virtual to live
                    YoYo.with(Techniques.ZoomInLeft).duration(800).playOn(tabViewPager);
                }
                else {
                    YoYo.with(Techniques.ZoomInRight).duration(800).playOn(tabViewPager);
                    trendingLiveFragmentUtil.setCallToActionFragmentGone(tabViewPager);
                }
            }
        }else{
//            stockFxSwitcher.setVisibility(View.VISIBLE);
//            if(securityTypeSpinner!=null){
//                securityTypeSpinner.setVisibility(View.INVISIBLE);
//            }
        //    trendingLiveFragmentUtil.setCallToActionFragmentGone(tabViewPager);

        }

//        BaseLiveFragmentUtil.setDarkBackgroundColor(isLive, pagerSlidingTabStrip);
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
        this.tradingStockPagerAdapter = null;
        this.tradingFXPagerAdapter = null;
        this.exchangeAdapter = null;
        this.exchangeSpinnerDTOSubject = null;
        this.stockFxSwitcher = null;
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        int colorId = getActivity().getResources().getColor(R.color.general_brand_color);
        if(BuildConfig.HAS_LIVE_ACCOUNT_FEATURE){

            try{
                isInLiveMode = trendingLiveFragmentUtil.getLiveActivityUtil().getLiveSwitcher().getIsOn();
                colorId = isInLiveMode? getActivity().getResources().getColor(R.color.general_red_live) : getActivity().getResources().getColor(R.color.general_brand_color);
                setActionBarColor(colorId);
            }catch (Exception e){
                //not yet set up
                setActionBarColor(colorId);
            }
        }else{
            setActionBarColor(colorId);
        }

        super.onPrepareOptionsMenu(menu);

    }

    private void inflateCustomToolbarView()
    {
        if (actionBarOwnerMixin.getActionBar() != null)
        {
            View view = LayoutInflater.from(actionBarOwnerMixin.getActionBar().getThemedContext())
                    .inflate(R.layout.trending_custom_actionbar, toolbar, false);
            setActionBarTitle("");
            setupStockFxSwitcher(view);
            setupExchangeSpinner(view);
            setupSecurityTypeSpinner(view);
        //    securityTypeSpinner = (SecurityTypeSpinner) view.findViewById(R.id.security_type_selection_menu);
            actionBarOwnerMixin.setCustomView(view);
        }
    }

    private void setupStockFxSwitcher(@NonNull View view)
    {
        stockFxSwitcher = (OffOnViewSwitcher) view.findViewById(R.id.switch_stock_fx);
        onDestroyOptionsMenuSubscriptions.add(stockFxSwitcher.getSwitchObservable()
                .subscribe(
                        new Action1<OffOnViewSwitcherEvent>()
                        {
                            @Override public void call(final OffOnViewSwitcherEvent offOnViewSwitcherEvent)
                            {
                                final TrendingTabType oldType = lastType;

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
                                            {   //TODO This is onboard logic. We will comment it now till it's back
                                                if (offOnViewSwitcherEvent.isOn && userProfileDTO.fxPortfolio == null && fxPortfolioId == null)
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
                                                    if (!offOnViewSwitcherEvent.isOn)
                                                    {
                                                        lastType = TrendingTabType.STOCK;
                                                    }
                                                    else
                                                    {
                                                        lastType = TrendingTabType.FX;
                                                    }
                                                    if (!oldType.equals(lastType))
                                                    {
                                                        clearChildFragmentManager();
                                                        initViews();
                                                        getActivity().supportInvalidateOptionsMenu();
                                                    }
                                                }
                                            }
                                        }));
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to stockFxSwitcher in trendingMain")));

        stockFxSwitcher.setIsOn(lastType.equals(TrendingTabType.FX), false);
    }

    private void setupExchangeSpinner(@NonNull View view)
    {
        exchangeSpinner = (ExchangeSpinner) view.findViewById(R.id.exchange_selection_menu);
        if (lastType == TrendingTabType.FX)
        {
            exchangeSpinner.setVisibility(View.GONE);
            return;
        }
        else if (!TrendingStockTabType.values()[tabViewPager.getCurrentItem()].showExchangeSelection)
        {
            exchangeSpinner.setVisibility(View.GONE);
            return;
        }

        exchangeSpinner.setVisibility(View.VISIBLE);

        exchangeAdapter = new TrendingFilterSpinnerIconAdapter(
                getActivity(),
                R.layout.trending_filter_spinner_item_short);
        exchangeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
        exchangeSpinner.setAdapter(exchangeAdapter);

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
        onDestroyOptionsMenuSubscriptions.add(AppObservable.bindSupportFragment(
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
        BaseActivity activity = (BaseActivity) getActivity();
        if (selectedStockPageIndex != null)
        {
            if (lastType.equals(TrendingTabType.STOCK))
            {
                if (tabViewPager != null)
                {
                    lastStockTab = TrendingStockTabType.values()[selectedStockPageIndex];
                    tabViewPager.setCurrentItem(selectedStockPageIndex, true);
                    selectedStockPageIndex = null;
                }
            }
            else if (actionBarOwnerMixin != null && activity != null)
            {
                lastType = TrendingTabType.STOCK;
                stockFxSwitcher.setIsOn(false, false);
            }
        }
        else if (selectedFxPageIndex != null)
        {
            if (lastType.equals(TrendingTabType.FX))
            {
                if (tabViewPager != null)
                {
                    lastFXTab = TrendingFXTabType.values()[selectedFxPageIndex];
                    tabViewPager.setCurrentItem(selectedFxPageIndex, true);
                    selectedFxPageIndex = null;
                }
            }
            else if (stockFxSwitcher != null)
            {
                stockFxSwitcher.setIsOn(true, false);
            }
        }
        else if (lastType.equals(TrendingTabType.STOCK))
        {
            tabViewPager.setCurrentItem(lastStockTab.ordinal(), true);
        }
        else if (lastType.equals(TrendingTabType.FX))
        {
            tabViewPager.setCurrentItem(lastFXTab.ordinal(), true);
        }
        else
        {
            throw new RuntimeException("Unhandled TrendingTabType." + lastType);
        }
        clearRoutingParam();
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

    public static void setLastType(@NonNull AssetClass assetClass)
    {
        if (assetClass.equals(AssetClass.STOCKS))
        {
            lastType = TrendingTabType.STOCK;
            lastStockTab = TrendingStockTabType.getDefault();
        }
        else if (assetClass.equals(AssetClass.FX))
        {
            lastType = TrendingTabType.FX;
            lastFXTab = TrendingFXTabType.getDefault();
        }
    }

    private class TradingStockPagerAdapter extends FragmentPagerAdapter
    {
        @NonNull final SparseArray<Fragment> registeredFragments;

        public TradingStockPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
            registeredFragments = new SparseArray<>();
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            TrendingStockTabType tabType = TrendingStockTabType.values()[position];
            Class fragmentClass = tabType.fragmentClass;
            TrendingStockFragment.putTabType(args, tabType);
            Fragment created = Fragment.instantiate(getActivity(), fragmentClass.getName(), args);
            registeredFragments.put(position, created);
            return created;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(TrendingStockTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return TrendingStockTabType.values().length;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }
    }

    private class TradingFXPagerAdapter extends FragmentPagerAdapter
    {
        public TradingFXPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            Class fragmentClass = TrendingFXTabType.values()[position].fragmentClass;
            if (fragmentClass.equals((Class) FXMainPositionListFragment.class))
            {
                FXMainPositionListFragment.putMainFXPortfolioId(args, fxPortfolioId);
            }
            return Fragment.instantiate(getActivity(), fragmentClass.getName(), args);
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(TrendingFXTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return TrendingFXTabType.values().length;
        }
    }

    /**setupSecurityTypeSpinner
     * Live security types dropdown spinner
     */
    private void setupSecurityTypeSpinner(View view)
    {
        securityTypeSpinner = (SecurityTypeSpinner) view.findViewById(R.id.security_type_selection_menu);
        isInLiveMode = trendingLiveFragmentUtil.getLiveActivityUtil().getLiveSwitcher().getIsOn();
        if(isInLiveMode)
        {

            securityTypeSpinner.setVisibility(View.VISIBLE);
            stockFxSwitcher.setVisibility(View.INVISIBLE);
        }
        else // if in virtual mode
        {
            securityTypeSpinner.setVisibility(View.INVISIBLE);
            stockFxSwitcher.setVisibility(View.VISIBLE);

        }
        
        if (lastType == TrendingTabType.FX)
        {
            securityTypeSpinner.setVisibility(View.INVISIBLE);
            if(!isInLiveMode){
                stockFxSwitcher.setVisibility(View.VISIBLE);
            }
            return;
        }
        else if (!TrendingStockTabType.values()[tabViewPager.getCurrentItem()].showExchangeSelection)
        {
            securityTypeSpinner.setVisibility(View.INVISIBLE);
            if(!isInLiveMode){
                stockFxSwitcher.setVisibility(View.VISIBLE);
            }
            return;
        }
        securitTypeAdapter = new TrendingFilterSpinnerIconAdapter(
                getActivity(),
                R.layout.trending_filter_spinner_item_short);

        securitTypeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);


        securityTypeSpinner.setAdapter(securitTypeAdapter);

        securityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
//                onExchangeSelected(parent, view, position, id);
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

//        SecurityTypeDTO dto = new SecurityTypeDTO();
//        dto.name = "Sample name";
//        dto.id = 1;
//        dto.isEnabled = true;
//        dto.imageUrl = "https://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/sectypes/bond.jpeg";
//        securitTypeAdapter.add(dto);
//        securitTypeAdapter.notifyDataSetChanged();

        CompositeExchangeSecurityDTO compositeExchangeSecurityDTO = null;

        if(compositeExchangeSecurityDTO==null){
            compositeExchangeSecurityCacheRx.fetch(currentUserId.toUserBaseKey()).subscribe(new Action1<CompositeExchangeSecurityDTO>() {
                @Override
                public void call(CompositeExchangeSecurityDTO compositeExchangeSecurityDTO) {
                    Log.v(getTag(), "!!!"+compositeExchangeSecurityDTO);
                    securitTypeAdapter.addAll(compositeExchangeSecurityDTO.getSecurityTypes());
                    securitTypeAdapter.notifyDataSetChanged();
                }
            });

        }else{

            Log.v(getTag(), "!!!22"+compositeExchangeSecurityDTO);
            securitTypeAdapter.addAll(compositeExchangeSecurityDTO.getSecurityTypes());
            securitTypeAdapter.notifyDataSetChanged();
        }

//        ExchangeListType key = new ExchangeListType();
//        onDestroyOptionsMenuSubscriptions.add(AppObservable.bindSupportFragment(
//                this,
//                Observable.combineLatest(
//                        exchangeCompactListCache.getOne(key)
//                                .map(new PairGetSecond<ExchangeListType, ExchangeCompactDTOList>())
//                                .map(new Func1<ExchangeCompactDTOList, ExchangeCompactSpinnerDTOList>()
//                                {
//                                    @Override public ExchangeCompactSpinnerDTOList call(ExchangeCompactDTOList exchangeDTOs)
//                                    {
//                                        ExchangeCompactSpinnerDTOList spinnerList = new ExchangeCompactSpinnerDTOList(
//                                                getResources(),
//                                                ExchangeCompactDTOUtil.filterAndOrderForTrending(
//                                                        exchangeDTOs,
//                                                        new ExchangeCompactDTODescriptionNameComparator<>()));
//                                        // Adding the "All" choice
//                                        spinnerList.add(0, new ExchangeCompactSpinnerDTO(getResources()));
//                                        return spinnerList;
//                                    }
//                                })
//                                .startWith(exchangeCompactSpinnerDTOList != null
//                                        ? Observable.just(exchangeCompactSpinnerDTOList)
//                                        : Observable.<ExchangeCompactSpinnerDTOList>empty())
//                                .distinctUntilChanged(),
//                        userProfileCache.getOne(currentUserId.toUserBaseKey()).map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
//                        new Func2<ExchangeCompactSpinnerDTOList, UserProfileDTO, Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO>>()
//                        {
//                            @Override
//                            public Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO> call(
//                                    ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs,
//                                    UserProfileDTO userProfileDTO)
//                            {
//                                Country defaultCountry = userProfileDTO.getCountry();
//                                return Pair.create(
//                                        exchangeCompactSpinnerDTOs,
//                                        defaultCountry == null
//                                                ? null
//                                                : exchangeCompactSpinnerDTOs.findFirstDefaultFor(defaultCountry));
//                            }
//                        }))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Action1<Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO>>()
//                        {
//                            @Override public void call(Pair<ExchangeCompactSpinnerDTOList, ExchangeCompactSpinnerDTO> pair)
//                            {
//                                exchangeCompactSpinnerDTOList = pair.first;
//                                exchangeAdapter.addAll(pair.first);
//                                exchangeAdapter.notifyDataSetChanged();
//                                handleExchangeRouting(pair.second);
//                            }
//                        },
//                        new TimberAndToastOnErrorAction1(
//                                getString(R.string.error_fetch_exchange_list_info),
//                                "Error fetching the list of exchanges")));
    }

    protected void onSecurityTypeSelected(AdapterView<?> parent, View view, int position, long id)
    {
//        ExchangeCompactSpinnerDTO dto = (ExchangeCompactSpinnerDTO) parent.getItemAtPosition(position);
//        preferredExchangeMarket.set(dto.getExchangeIntegerId());
//        exchangeSpinnerDTOSubject.onNext(dto);
    }
}
