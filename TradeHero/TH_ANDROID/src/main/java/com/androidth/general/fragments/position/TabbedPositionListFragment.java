package com.androidth.general.fragments.position;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Bind;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import com.android.common.SlidingTabLayout;
import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.api.live1b.PositionsResponseDTO;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.live.LiveViewFragment;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.service.Live1BServiceWrapper;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.LiveConstants;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.widget.OffOnViewSwitcherEvent;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.portfolio.AssetClass;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioId;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.position.GetPositionsDTOKeyFactory;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.utils.route.THRouter;

import org.json.JSONObject;

import javax.inject.Inject;

@Routable("user/:userId/portfolio/:portfolioId")
public class TabbedPositionListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = TabbedPositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = TabbedPositionListFragment.class.getName() + ".userBaseKey";
    private static final String BUNDLE_KEY_IS_FX = TabbedPositionListFragment.class.getName() + "isFX";
    private static final String BUNDLE_KEY_POSITION_TYPE = TabbedPositionListFragment.class.getName() + "position.type";
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = TabbedPositionListFragment.class.getName() + ".purchaseApplicablePortfolioId";

    private static final String BUNDLE_KEY_PROVIDER_ID = TabbedPositionListFragment.class + ".providerId";
    private static final boolean DEFAULT_IS_FX = false;
    private static final String LEADERBOARD_DEF_TIME_RESTRICTED = "LEADERBOARD_DEF_TIME_RESTRICTED";
    private static final boolean DEFAULT_IS_TIME_RESTRICTED = false;
    private static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";

    @Inject THRouter thRouter;
    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;
    @Bind(R.id.pager) ViewPager tabViewPager;
    @Bind(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;
    @Inject Live1BServiceWrapper live1BServiceWrapper;

    protected GetPositionsDTOKey getPositionsDTOKey;
    protected PortfolioDTO portfolioDTO;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;
    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    boolean isFX;

    ProviderId providerId;

    private int selectedTabIndex;

    protected String actionBarNavUrl, actionBarColor;

    public enum TabType
    {
        LONG(R.string.position_list_header_open_unsure, R.string.position_list_header_open_long_unsure),
        SHORT(R.string.position_list_header_open_short_unsure, R.string.position_list_header_open_short_unsure),
        CLOSED(R.string.position_list_header_closed_unsure, R.string.position_list_header_closed_unsure),;

        @StringRes private final int stockTitle;
        @StringRes private final int fxTitle;

        TabType(@StringRes int stockTitle, @StringRes int fxTitle)
        {
            this.stockTitle = stockTitle;
            this.fxTitle = fxTitle;
        }
    }

    private static TabType[] STOCK_TYPES = new TabType[] {
            TabType.LONG,
            TabType.CLOSED,
    };

    private static TabType[] FX_TYPES = new TabType[] {
            TabType.LONG,
            TabType.SHORT,
            TabType.CLOSED,
    };

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
        return args.getString(BUNDLE_KEY_POSITION_TYPE, TabType.LONG.name());
    }

    private static boolean isFX(@NonNull Bundle args)
    {
        return args.getBoolean(BUNDLE_KEY_IS_FX, DEFAULT_IS_FX);
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

    public static void putLeaderboardTimeRestricted(@NonNull Bundle args, boolean isTimeRestricted)
    {
        args.putBoolean(LEADERBOARD_DEF_TIME_RESTRICTED, isTimeRestricted);
    }

    public static boolean getLeaderBoardTimeRestricted(@NonNull Bundle args)
    {
        return args.getBoolean(LEADERBOARD_DEF_TIME_RESTRICTED, DEFAULT_IS_TIME_RESTRICTED);
    }

    public static void putLeaderboardPeriodStartString(@NonNull Bundle args, @NonNull String periodStartString)
    {
        args.putString(LEADERBOARD_PERIOD_START_STRING, periodStartString);
    }

    @Nullable public static String getLeaderboardPeriodStartString(@NonNull Bundle args)
    {
        return args.getString(LEADERBOARD_PERIOD_START_STRING);
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

    @Override public void onCreate(Bundle savedInstanceState)
    {
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
        if (isFX)
        {
            String type = getPositionType(args);
            try
            {
                selectedTabIndex = TabType.valueOf(type).ordinal();
            } catch (Exception e)
            {
                selectedTabIndex = 0;
            }
        }
        this.purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());

        if(args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR)!=null){
            actionBarColor = args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR);
        }

        if(args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL)!=null){
            actionBarNavUrl = args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tabbed_position_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews()
    {
        tabViewPager.setAdapter(new TabbedPositionPageAdapter(getChildFragmentManager()));
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.general_tab_indicator_color));
        pagerSlidingTabStrip.setDistributeEvenly(true);
        pagerSlidingTabStrip.setViewPager(tabViewPager);
        if (isFX) {
            tabViewPager.setCurrentItem(selectedTabIndex);
        }

        if (this instanceof CompetitionLeaderboardPositionListFragment) {
            //means it is inside the competition
            tabViewPager.addOnPageChangeListener(new CompetitionPositionTabPageListener());
            pagerSlidingTabStrip.setOnPageChangeListener(new CompetitionPositionTabPageListener());
        }

    }

    @Override public void onStart() {
        super.onStart();
        userLoginLoader();
    }

    private class TabbedPositionPageAdapter extends FragmentPagerAdapter
    {
        public TabbedPositionPageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();

            if (purchaseApplicableOwnedPortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, getPositionsDTOKey);
            PositionListFragment.putShownUser(args, shownUser);
            TabType positionType;
            if (isFX)
            {
                positionType = FX_TYPES[position];
            }
            else
            {
                positionType = STOCK_TYPES[position];
            }
            PositionListFragment.putPositionType(args, positionType);

            if (getPositionsDTOKey instanceof LeaderboardMarkUserId)
            {
                LeaderboardPositionListFragment.putLeaderboardTimeRestricted(args, getLeaderBoardTimeRestricted(getArguments()));
                String periodStart = getLeaderboardPeriodStartString(getArguments());
                if (periodStart != null)
                {
                    LeaderboardPositionListFragment.putLeaderboardPeriodStartString(args, periodStart);
                }
            }

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
            return Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            if (isFX)
            {
                return FX_TYPES.length;
            }
            else
            {
                return STOCK_TYPES.length;
            }
        }

        @Override public CharSequence getPageTitle(int position)
        {
            if (isFX)
            {
                return getString(FX_TYPES[position].fxTitle);
            }
            else
            {
                return getString(STOCK_TYPES[position].stockTitle);
            }
        }

    }

    private class CompetitionPositionTabPageListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrollStateChanged(int state) {}

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            Log.v(getTag(), "!!!Position "+position);
            switch (position){
                case 0:
                    GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_PORT_OPEN);
                    GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_OPEN);
                    break;
                case 1:
                    GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_PORT_CLOSE);
                    GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_CLOSE);
                    break;
                default:
                    break;

            }
        }
    }





    @Override  public void onLiveTradingChanged(OffOnViewSwitcherEvent event) {
        super.onLiveTradingChanged(event);

    //    Toast.makeText(getContext(), "TOGGLE PRESSED!", Toast.LENGTH_LONG).show();

        LiveConstants.hasLiveAccount = true; // todo remove after debug

        userLoginLoader();
    }

    private void userLoginLoader()
    {
        if(LiveConstants.isInLiveMode && LiveConstants.hasLiveAccount)
        {
            live1BServiceWrapper.getPositions()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    if (throwable != null) {
                        if (throwable instanceof RetrofitError) {

                            RetrofitError error = (RetrofitError) throwable;
                            Log.d("PLF.java", error.getResponse() + " " + error.toString() + " --URL--> " + error.getResponse().getUrl());
                            if (error.getResponse() != null && error.getResponse().getStatus() == 302) {
                                pushLiveLogin(error);
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

    private void pushLiveLogin(RetrofitError error)
    {
        try {
            if (LiveConstants.hasLiveAccount) {

                JSONObject buySellStockError = new JSONObject(new String(((TypedByteArray) error.getResponse().getBody()).getBytes()));

                Log.d("PLF.java", "pushLiveLogin " + error.getResponse().getBody().toString() + " " + buySellStockError.toString());
                // user has a live account, but not logged in, redirect to the extracted json URL
                Bundle args = getArguments();
                String redirectURL = buySellStockError.get(LiveViewFragment.BUNDLE_KEY_REDIRECT_URL_ID).toString();
                args.putString(LiveViewFragment.BUNDLE_KEY_REDIRECT_URL_ID, redirectURL);
                LiveViewFragment liveViewFragment = new LiveViewFragment();
                liveViewFragment.setArguments(args);
                liveViewFragment.putUrl(args, redirectURL);
                Log.d("PLF.java", "REDIRECT URL->> " + redirectURL );
                try {
                    /////   unsubscribe(buySellSubscription);
                }
                catch(Exception ex)
                {
                    Log.d("printStackTrace", ex.toString());
                }

                navigator.get().pushFragment(LiveViewFragment.class, args);

            } else {
                Intent kycIntent = new Intent(getActivity(), SignUpLiveActivity.class);
                startActivity(kycIntent);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error in redirection: " + e.getStackTrace().toString() , Toast.LENGTH_LONG).show();
            Log.d("pushLiveLoginCatchError", e.toString());
        }

    }

}
