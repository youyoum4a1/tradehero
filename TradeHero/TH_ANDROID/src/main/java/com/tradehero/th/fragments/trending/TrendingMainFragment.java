package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.CustomDrawerToggle;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.fragments.CallToActionFragment;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.base.LollipopArrayAdapter;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.live.LiveTrendingFragment;
import com.tradehero.th.fragments.market.ExchangeSpinner;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSpinnerIconAdapter;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.market.ExchangeMarketPreference;
import com.tradehero.th.persistence.prefs.IsLiveLogIn;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
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
        "trending-fx/:dummyFXId",
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
    @Inject CustomDrawerToggle drawerToggle;
    @Inject Analytics analytics;
    @Inject @PreferredExchangeMarket ExchangeMarketPreference preferredExchangeMarket;
    @Inject ExchangeCompactListCacheRx exchangeCompactListCache;

    @RouteProperty("stockPageIndex") Integer selectedStockPageIndex;
    @RouteProperty("exchangeId") Integer routedExchangeId;
    @RouteProperty("dummyFXId") Integer dummyFXId;

    @NonNull private static TrendingAssetType lastType = TrendingAssetType.STOCK;
    @NonNull private static TrendingStockSortType lastStockTab = TrendingStockSortType.getDefault();

    public static final int CODE_PROMPT = 1;
    private boolean fetchedFXPortfolio = false;
    private Observable<UserProfileDTO> userProfileObservable;
    @Nullable private OwnedPortfolioId fxPortfolioId;
    public static boolean fxDialogShowed = false;
    private BaseLiveFragmentUtil trendingLiveFragmentUtil;
    private DTOAdapterNew<ExchangeCompactSpinnerDTO> exchangeAdapter;
    private final BehaviorSubject<ExchangeCompactSpinnerDTO> exchangeSpinnerDTOSubject = BehaviorSubject.create();
    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOList;
    private Spinner assetTypeSpinner;
    private TextView liveTitleTextView;
    private LollipopArrayAdapter<TrendingAssetType> assetTypeAdapter;
    private LollipopArrayAdapter<TrendingStockSortType> sortByAdapter;
    private CallToActionFragment callToActionFragment;

    // TODO: Dummy attribute, pending server
    @Inject @IsLiveLogIn BooleanPreference isLiveLogIn;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    private String actionBarTitle = "";

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("trending-stocks/trending", "trending-stocks/tab-index/" + TrendingStockSortType.Trending.ordinal());
        router.registerAlias("trending-stocks/price-action", "trending-stocks/tab-index/" + TrendingStockSortType.Price.ordinal());
        router.registerAlias("trending-stocks/unusual-volumes", "trending-stocks/tab-index/" + TrendingStockSortType.Volume.ordinal());
        router.registerAlias("trending-stocks/all-trending", "trending-stocks/tab-index/" + TrendingStockSortType.All.ordinal());
        router.registerAlias("trending-fx", "trending-fx/1");
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
                lastType = TrendingAssetType.getForAssetClass(askedAssetClass);
            }
            catch (IllegalArgumentException e)
            {
                Timber.e(e, "Unhandled assetClass for user " + currentUserId.get());
            }
        }
        assetTypeAdapter =
                new TrendingAssetTypeAdapter(getActivity());

        exchangeAdapter = new TrendingFilterSpinnerIconAdapter(
                getActivity(),
                R.layout.trending_filter_spinner_item_short);
        exchangeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);

        sortByAdapter = new StockSortTypeAdapter(getActivity());
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

        if (lastType == TrendingAssetType.STOCK)
        {
            if (isLiveTrading.get())
            {
                handleIsLive();
            }
            else
            {
                handleIsVirtual();
            }
        }
    }

    private void initViews()
    {
        if (fxPortfolioId == null)
        {
            lastType = TrendingAssetType.STOCK;
        }
        if (lastType == null)
        {
            Timber.e(new NullPointerException("Gotcha lastType is null"), "lastType is null");
        }

        if (lastType == TrendingAssetType.FX)
        {
            exchangeContainer.setVisibility(View.GONE);
            handleFXSelected();
        }
        else
        {
            exchangeContainer.setVisibility(View.VISIBLE);
            int position = sortByAdapter.getPosition(lastStockTab);
            if (position >= 0)
            {
                handleSortBySelected(lastStockTab);
            }
            else
            {
                handleSortBySelected(TrendingStockSortType.getDefault());
            }
        }
    }

    private void handleFXSelected()
    {
        Fragment created = Fragment.instantiate(getActivity(), TrendingFXFragment.class.getName());
        getChildFragmentManager().beginTransaction().replace(R.id.trending_fragment_container, created).commit();
    }

    @Override public void onResume()
    {
        super.onResume();
        thRouter.inject(this, getArguments());
        trendingLiveFragmentUtil.onResume();
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return lastType.equals(TrendingAssetType.STOCK);
    }

    @Override public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        super.onLiveTradingChanged(event);
        if (event.isOn && event.isFromUser)
        {
            if (!isLiveLogIn.get())
            {
                //trendingLiveFragmentUtil.launchLiveLogin();
                if (callToActionFragment == null)
                {
                    callToActionFragment = new CallToActionFragment();
                    callToActionFragment.setFragment(this);
                }

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.trending_main_container, callToActionFragment);
                fragmentTransaction.commit();
                drawerToggle.setDrawerIndicatorEnabled(true);
            }
            else
            {
                handleIsLive();
            }
        }
        else if (!event.isOn && event.isFromUser)
        {
            if (callToActionFragment != null)
            {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.remove(callToActionFragment);
                fragmentTransaction.commit();
            }

            handleIsVirtual();
        }

        liveTitleTextView.setText(actionBarTitle);
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

        if (requestCode == CODE_PROMPT && resultCode == Activity.RESULT_OK)
        {
            if (isLiveTrading.get())
            {
                handleIsLive();
            }
        }
    }

    private void inflateCustomToolbarView()
    {
        if (actionBarOwnerMixin.getActionBar() != null)
        {
            View view = LayoutInflater.from(actionBarOwnerMixin.getActionBar().getThemedContext())
                    .inflate(R.layout.trending_custom_actionbar, toolbar, false);
            setActionBarTitle(actionBarTitle);
            setupStockFxSwitcher(view);

            actionBarOwnerMixin.setCustomView(view);
        }
    }

    private void setupStockFxSwitcher(@NonNull View view)
    {
        assetTypeSpinner = (Spinner) view.findViewById(R.id.stock_type_dropdown);
        assetTypeSpinner.setAdapter(assetTypeAdapter);
        assetTypeSpinner.setSelection(lastType.ordinal(), false);
        onDestroyOptionsMenuSubscriptions.add(AdapterViewObservable.selects(assetTypeSpinner)
                .ofType(OnItemSelectedEvent.class)
                .subscribe(new Action1<OnItemSelectedEvent>()
                {
                    @Override public void call(OnItemSelectedEvent onItemSelectedEvent)
                    {
                        final TrendingAssetType oldType = lastType;
                        final TrendingAssetType newType = assetTypeAdapter.getItem(onItemSelectedEvent.position);
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
                                        if (newType.assetClass == AssetClass.FX && userProfileDTO.fxPortfolio == null && fxPortfolioId == null)
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
                                            if (newType.assetClass == AssetClass.STOCKS)
                                            {
                                                lastType = TrendingAssetType.STOCK;
                                            }
                                            else
                                            {
                                                lastType = TrendingAssetType.FX;
                                            }
                                            if (!oldType.equals(lastType))
                                            {
                                                initViews();
                                                getActivity().supportInvalidateOptionsMenu();
                                            }
                                        }
                                    }
                                }));
                    }
                }, new TimberOnErrorAction1("Error on dropdown of asset type")));

        // TODO: check if have better way after live API plug in
        liveTitleTextView = (TextView) view.findViewById(R.id.live_title);
        liveTitleTextView.setText(actionBarTitle);
        assetTypeSpinner.setVisibility(View.VISIBLE);

        if (isLiveTrading.get())
        {
            assetTypeSpinner.setVisibility(View.GONE);
        }
    }

    private void setupExchangeSpinner()
    {
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
                        TrendingStockSortType tabType = sortByAdapter.getItem(onItemSelectedEvent.position);
                        handleSortBySelected(tabType);
                    }
                }, new TimberOnErrorAction1("")));
    }

    protected void handleSortBySelected(TrendingStockSortType tabType)
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
            lastType = TrendingAssetType.FX;
        }
        else
        {
            lastType = TrendingAssetType.STOCK;
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
        if (selectedStockPageIndex != null)
        {
            lastType = TrendingAssetType.STOCK;
            lastStockTab = TrendingStockSortType.values()[selectedStockPageIndex];
            initViews();
            selectedStockPageIndex = null;
        }
        else if (dummyFXId != null)
        {
            lastType = TrendingAssetType.FX;
            initViews();
            dummyFXId = null;
        }
        clearRoutingParam();
    }

    private void handleExchangeRouting(@Nullable ExchangeCompactDTO defaultValue)
    {
        if (routedExchangeId != null
                && lastType.equals(TrendingAssetType.STOCK))
        {
            exchangeSpinner.setSelectionById(new ExchangeIntegerId(routedExchangeId));
            routedExchangeId = null;
        }
        else if (lastType.equals(TrendingAssetType.STOCK) && preferredExchangeMarket.get() > 0)
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
        getArguments().remove("exchangeId");
        getArguments().remove("dummyFXId");
    }

    public Observable<ExchangeCompactSpinnerDTO> getExchangeSelectionObservable()
    {
        return exchangeSpinnerDTOSubject.asObservable();
    }

    private static class TrendingAssetTypeAdapter extends LollipopArrayAdapter<TrendingAssetType>
    {
        public TrendingAssetTypeAdapter(Context context)
        {
            super(context, R.layout.dropdown_item_title_selected, R.layout.dropdown_item_title,
                    Arrays.asList(TrendingAssetType.values()));
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);
            if (v instanceof TextView)
            {
                ((TextView) v).setText(getItem(position).titleStringResId);
            }
            return v;
        }

        @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View v = super.getDropDownView(position, convertView, parent);
            if (v instanceof TextView)
            {
                ((TextView) v).setText(getItem(position).titleStringResId);
            }
            return v;
        }
    }

    private static class StockSortTypeAdapter extends LollipopArrayAdapter<TrendingStockSortType>
    {
        public StockSortTypeAdapter(Context context)
        {
            super(context, R.layout.trending_sort_item_selected, R.layout.trending_sort_item,
                    new ArrayList<>(Arrays.asList(TrendingStockSortType.values())));
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);
            if (v instanceof TextView)
            {
                ((TextView) v).setText(getItem(position).titleStringResId);
            }
            return v;
        }

        @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View v = super.getDropDownView(position, convertView, parent);
            if (v instanceof TextView)
            {
                ((TextView) v).setText(getItem(position).titleStringResId);
            }
            return v;
        }
    }

    public BaseLiveFragmentUtil getTrendingLiveFragmentUtil()
    {
        return trendingLiveFragmentUtil;
    }

    public void handleIsLive()
    {
        if (assetTypeSpinner != null)
        {
            assetTypeSpinner.setVisibility(View.GONE);
        }

        actionBarTitle = "CFD";
        exchangeContainer.setVisibility(View.GONE);
        Fragment created = Fragment.instantiate(getActivity(), LiveTrendingFragment.class.getName());
        getChildFragmentManager().beginTransaction().replace(R.id.trending_fragment_container, created).commitAllowingStateLoss();
    }

    private void handleIsVirtual()
    {
        if (assetTypeSpinner != null)
        {
            assetTypeSpinner.setVisibility(View.VISIBLE);
        }

        actionBarTitle = "";
        exchangeContainer.setVisibility(View.VISIBLE);
        int position = sortByAdapter.getPosition(lastStockTab);

        if (position >= 0)
        {
            handleSortBySelected(lastStockTab);
        }
        else
        {
            handleSortBySelected(TrendingStockSortType.getDefault());
        }
    }
}
