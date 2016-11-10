package com.androidth.general.fragments.position;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.android.common.SlidingTabLayout;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.live.LiveViewProvider;
import com.androidth.general.api.live1b.LivePositionDTO;
import com.androidth.general.api.portfolio.AssetClass;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioId;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.position.GetPositionsDTOKeyFactory;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.utils.SDKUtils;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.portfolio.header.PortfolioHeaderFactory;
import com.androidth.general.fragments.portfolio.header.PortfolioHeaderView;
import com.androidth.general.fragments.trade.AbstractBuySellPopupDialogFragment;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.Live1BServiceWrapper;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.LiveConstants;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.utils.route.THRouter;
import com.androidth.general.widget.OffOnViewSwitcherEvent;
import com.androidth.general.R;
import com.androidth.general.fragments.base.DashboardFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;


public class TabbedPositionsCashFragment extends DashboardFragment {
    @Bind(R.id.positions_cash_pager) ViewPager tabViewPager;
    @Bind(R.id.positions_cash_tabs) SlidingTabLayout pagerSlidingTabStrip;


    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = TabbedPositionsCashFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = TabbedPositionsCashFragment.class.getName() + ".userBaseKey";
    private static final String BUNDLE_KEY_IS_FX = TabbedPositionsCashFragment.class.getName() + "isFX";
    private static final String BUNDLE_KEY_POSITION_TYPE = TabbedPositionsCashFragment.class.getName() + "position.type";
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = TabbedPositionsCashFragment.class.getName() + ".purchaseApplicablePortfolioId";

    private static final String BUNDLE_KEY_PROVIDER_ID = TabbedPositionsCashFragment.class + ".providerId";
    private static final boolean DEFAULT_IS_FX = false;
    private static final String LEADERBOARD_DEF_TIME_RESTRICTED = "LEADERBOARD_DEF_TIME_RESTRICTED";
    private static final boolean DEFAULT_IS_TIME_RESTRICTED = false;
    private static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";

    @Inject THRouter thRouter;
    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;
    @Inject Live1BServiceWrapper live1BServiceWrapper;

    protected GetPositionsDTOKey getPositionsDTOKey;
    protected PortfolioDTO portfolioDTO;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;
    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    boolean isFX;

    ProviderId providerId;


    protected String actionBarNavUrl, actionBarColor;

    @Inject
    PortfolioCacheRx portfolioCache;
    @Inject
    UserProfileCacheRx userProfileCache;
    @Bind(R.id.positions_cash_list_header_stub)
    ViewStub headerStub;

    @Nullable protected UserProfileDTO shownUserProfileDTO;
    private View inflatedView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private PortfolioHeaderView portfolioHeaderView;

    @Inject
    CurrentUserId currentUserId;

    @Inject
    RequestHeaders requestHeaders;

    private Subscription portfolioSubscription;
    private Subscription getPositionsSubscription;

    private LivePositionDTO livePositionDTOFromBuySell;
    private String requestIdFromBuySell;

    public enum TabType
    {
        //        LONG(R.string.position_list_header_open_positions, R.string.position_list_header_open_long_unsure),
//        SHORT(R.string.position_list_header_open_short_unsure, R.string.position_list_header_open_short_unsure),
//        CLOSED(R.string.position_list_header_closed_unsure, R.string.position_list_header_closed_unsure),
        LONG(R.string.position_list_header_open_positions, R.string.position_list_header_open_long_unsure);


        @StringRes private final int stockTitle;
        @StringRes private final int fxTitle;

        TabType(@StringRes int stockTitle, @StringRes int fxTitle)
        {
            this.stockTitle = stockTitle;
            this.fxTitle = fxTitle;
        }
    }
    private static TabType[] STOCK_TYPES = new TabType[] {
            TabType.LONG
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thRouter.inject(this);
        Bundle args = getArguments();
        if (args.containsKey(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE))
        {
            shownUser = getShownUser(args);
        }
        else
        {
            shownUser = injectedUserBaseKey;
        }
        if (args.containsKey(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE))
        {
            getPositionsDTOKey = getGetPositionsDTOKey(args);
        }
        else
        {
            getPositionsDTOKey = new OwnedPortfolioId(injectedUserBaseKey.key, injectedPortfolioId.key);
        }
        isFX = isFX(args);
        providerId = getProviderId(args);

        this.purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());

        if(args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR)!=null){
            actionBarColor = args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR);
        }

        if(args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL)!=null){
            actionBarNavUrl = args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL);
        }

        if(getArguments().containsKey(AbstractBuySellPopupDialogFragment.KEY_LIVE_DTO)){
            livePositionDTOFromBuySell = getArguments().getParcelable(AbstractBuySellPopupDialogFragment.KEY_LIVE_DTO);
        }

        if(getArguments().containsKey(AbstractBuySellPopupDialogFragment.KEY_LIVE_REQUEST_ID)){
            requestIdFromBuySell = getArguments().getString(AbstractBuySellPopupDialogFragment.KEY_LIVE_REQUEST_ID);
        }

    }

    public static void putGetPositionsDTOKey(@NonNull Bundle args, @NonNull GetPositionsDTOKey getPositionsDTOKey)
    {
        args.putBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE, getPositionsDTOKey.getArgs());
    }

    public static void putShownUser(@NonNull Bundle args, @NonNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    public static void putIsFX(@NonNull Bundle args, @Nullable AssetClass assetClass)
    {
        if (assetClass == null)
        {
            args.putBoolean(BUNDLE_KEY_IS_FX, DEFAULT_IS_FX);
        }
        args.putBoolean(BUNDLE_KEY_IS_FX, assetClass == AssetClass.FX);
    }

    public static void putPositionType(@NonNull Bundle args, String positionType)
    {
        args.putString(BUNDLE_KEY_POSITION_TYPE, positionType);
    }

    private static String getPositionType(@NonNull Bundle args)
    {
        return args.getString(BUNDLE_KEY_POSITION_TYPE, "LONG");
    }

    private static boolean isFX(@NonNull Bundle args)
    {
        return false;
    }

    @NonNull private static UserBaseKey getShownUser(@NonNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE));
    }

    @Nullable private static GetPositionsDTOKey getGetPositionsDTOKey(@NonNull Bundle args)
    {
        return GetPositionsDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE));
    }

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @Nullable private static ProviderId getProviderId(@NonNull Bundle args)
    {
        Bundle bundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID);
        if (bundle == null)
        {
            return null;
        }
        return new ProviderId(bundle);
    }

    public static void putApplicablePortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    @Nullable public static OwnedPortfolioId getApplicablePortfolioId(@NonNull Bundle args)
    {
        Bundle portfolioBundle = args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
        if (portfolioBundle != null)
        {
            return new OwnedPortfolioId(portfolioBundle);
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tabbed_positions_cash_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }


    private void initViews()
    {
        tabViewPager.setAdapter(new TabbedPositionsCashPageAdapter(getChildFragmentManager(), new String[]{ "My Stocks","Funding","History" }, 3));
//        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.general_tab_indicator_color));
        pagerSlidingTabStrip.setDistributeEvenly(true);
        pagerSlidingTabStrip.setViewPager(tabViewPager);


    }
    @NonNull protected Observable<Pair<UserProfileDTO, PortfolioHeaderView>> getProfileAndHeaderObservable()
    {
        return Observable.combineLatest(
                getShownUserProfileObservable(),
                getPortfolioObservable(),
                new Func2<UserProfileDTO, PortfolioDTO, Pair<UserProfileDTO, PortfolioHeaderView>>()
                {
                    @Override public Pair<UserProfileDTO, PortfolioHeaderView> call(
                            @NonNull UserProfileDTO shownProfile,
                            @NonNull PortfolioDTO portfolioDTO)
                    {

                        if(portfolioDTO!=null){
                            linkPortfolioHeaderView(shownProfile, portfolioDTO);
                        }

                        return Pair.create(shownProfile, portfolioHeaderView);
                    }
                });
    }

    private void linkPortfolioHeaderView(UserProfileDTO userProfileDTO, @Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioHeaderView == null || inflatedView == null)
        {
            // portfolio header
            int headerLayoutId = PortfolioHeaderFactory.layoutIdFor(getPositionsDTOKey, portfolioCompactDTO, currentUserId);
            headerStub.setLayoutResource(headerLayoutId);
            inflatedView = headerStub.inflate();
            portfolioHeaderView = (PortfolioHeaderView) inflatedView;


//            if(portfolioCompactDTO.getPortfolioId()!=null){
//                connectPortfolioSignalR(portfolioCompactDTO);
//            }

        }

        portfolioHeaderView.linkWith(userProfileDTO);
        portfolioHeaderView.linkWith(portfolioCompactDTO);

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @SuppressLint("NewApi") @Override public void onGlobalLayout()
            {
                if(inflatedView!=null){
                    ViewTreeObserver observer = inflatedView.getViewTreeObserver();
                    if (observer != null)
                    {
                        if (SDKUtils.isJellyBeanOrHigher())
                        {
                            observer.removeOnGlobalLayoutListener(this);
                        }
                        else
                        {
                            observer.removeGlobalOnLayoutListener(this);
                        }
                    }
                }else{
                    Log.d(getTag(), "Inflated view is null");
                    return;
                }

                int headerHeight = inflatedView.getMeasuredHeight();
                Timber.d("Header Height %d", headerHeight);
            }
        };
        inflatedView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    @NonNull protected Observable<UserProfileDTO> getShownUserProfileObservable()
    {
        return userProfileCache.get(shownUser)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Pair<UserBaseKey, UserProfileDTO>, UserProfileDTO>()
                {
                    @Override public UserProfileDTO call(Pair<UserBaseKey, UserProfileDTO> pair)
                    {
                        UserProfileDTO shownProfile = pair.second;
                        shownUserProfileDTO = shownProfile;
//                        positionItemAdapter.linkWith(shownProfile);
                        return shownProfile;
                    }
                })
                .doOnError(new ToastOnErrorAction1(getString(R.string.error_fetch_user_profile)));
    }

    @NonNull protected Observable<PortfolioDTO> getPortfolioObservable()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            return portfolioCache.get(((OwnedPortfolioId) getPositionsDTOKey))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(new Func1<Throwable, Pair<OwnedPortfolioId, PortfolioDTO>>() {
                        @Override
                        public Pair<OwnedPortfolioId, PortfolioDTO> call(Throwable throwable) {
                            return null;
                        }
                    })
                    .map(new Func1<Pair<OwnedPortfolioId, PortfolioDTO>, PortfolioDTO>()
                    {
                        @Override public PortfolioDTO call(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                        {
                            if(pair!=null){
                                linkWith(pair.second);
                                return pair.second;
                            }else{
                                return null;
                            }
                        }
                    })
                    .doOnError(new Action1<Throwable>()
                    {
                        @Override public void call(Throwable error)
                        {
                            Timber.e("" + getString(R.string.error_fetch_portfolio_info) + " " + error.toString());
                        }
                    });
        }

        return Observable.empty();
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO) {
        this.portfolioDTO = portfolioDTO;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(portfolioSubscription!=null){
            portfolioSubscription.unsubscribe();
        }

        if(getPositionsSubscription!=null)
            getPositionsSubscription.unsubscribe();

        portfolioSubscription = getProfileAndHeaderObservable().subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(portfolioSubscription!=null){
            portfolioSubscription.unsubscribe();
        }
        if(getPositionsSubscription!=null)
            getPositionsSubscription.unsubscribe();
        inflatedView = null;
    }

    @Override  public void onLiveTradingChanged(OffOnViewSwitcherEvent event) {
        super.onLiveTradingChanged(event);

        Log.d("TabbedPos.java", "onLiveTradingChanged called!!! ");

        LiveConstants.hasLiveAccount = true; // todo remove after debug

        userLoginLoader();
    }

    private void userLoginLoader()
    {
        if(LiveConstants.isInLiveMode)
        {
            if(getPositionsSubscription==null||getPositionsSubscription.isUnsubscribed()) {
                getPositionsSubscription = live1BServiceWrapper.getPositions()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (throwable != null) {
                                    if (throwable instanceof RetrofitError) {

                                        RetrofitError error = (RetrofitError) throwable;
                                        Log.d("PLF.java", error.getResponse() + " " + error.toString() + " --URL--> " + error.getResponse().getUrl());
                                        if (error.getResponse() != null && error.getResponse().getStatus() == 302) {

                                            LiveViewProvider.showTradeHubLogin(TabbedPositionsCashFragment.this, throwable);

                                        } else if (error.getResponse() != null && error.getResponse().getStatus() == 404)
                                            Toast.makeText(getContext(), "Error connecting to service: " + error.getResponse() + " --body-- " + error.getBody().toString(), Toast.LENGTH_LONG).show();
                                        else {
                                            Toast.makeText(getContext(), "Error in stock purchase: " + error.getResponse() + " --body-- " + error.getBody().toString(), Toast.LENGTH_LONG).show();
                                            Log.d("PLF.java", "Error: " + error.getResponse() + " " + error.getBody().toString() + " --URL--> " + error.getResponse().getUrl());

                                        }
                                    }
                                }
                            }
                        })
                        //    .subscribe(new BuySellObserver(requisite.securityId, transactionFormDTO, IS_BUY));
                        .subscribe(new Action1<String>() {
                                       @Override
                                       public void call(String getPositions) {
                                           Log.d("PLF.java", "Success getPositions, result: " + getPositions);
                                       }
                                   }

                                , new TimberOnErrorAction1("Error purchasing stocks in live mode."));
            }
        }

    }


    private class TabbedPositionsCashPageAdapter extends FragmentPagerAdapter
    {
        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

        public TabbedPositionsCashPageAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb)
        {
            super(fm);
            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            if(position == 0) // if the position is 0 we are returning the First tab
            {
                if (purchaseApplicableOwnedPortfolioId != null)
                {
                    PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
                }
                PositionListFragment.putGetPositionsDTOKey(args, getPositionsDTOKey);
                PositionListFragment.putShownUser(args, shownUser);

                PositionListFragment.putPositionType(args, STOCK_TYPES[0]);

                if (providerId != null)
                {
                    CompetitionLeaderboardPositionListFragment.putProviderId(args, providerId);
                }

                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, actionBarNavUrl);
                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, actionBarColor);

                if (getPositionsDTOKey instanceof LeaderboardMarkUserId)
                {
                    return Fragment.instantiate(getActivity(), LeaderboardPositionListFragment.class.getName(), args);
                }

                if(livePositionDTOFromBuySell!=null){
                    args.putParcelable(AbstractBuySellPopupDialogFragment.KEY_LIVE_DTO, livePositionDTOFromBuySell);
                }

                if(requestIdFromBuySell!=null){
                    args.putString(AbstractBuySellPopupDialogFragment.KEY_LIVE_REQUEST_ID, requestIdFromBuySell);
                }

                return Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
            }

            if(position == 1)
                return Fragment.instantiate(getActivity(), FundingFragment.class.getName(), args);
            else
            {
                return Fragment.instantiate(getActivity(), HistoryFragment.class.getName(), args);
            }

        }

        @Override public int getCount()
        {
            return NumbOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

    }
}