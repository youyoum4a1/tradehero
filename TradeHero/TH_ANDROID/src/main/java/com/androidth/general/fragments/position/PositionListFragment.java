package com.androidth.general.fragments.position;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.androidth.general.api.live.LiveViewProvider;
import com.androidth.general.api.live1b.ErrorResponseDTO;
import com.androidth.general.api.live1b.LivePositionDTO;
import com.androidth.general.api.live1b.PositionsResponseDTO;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.base.THApp;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.position.live1b.LivePositionListRowView;
import com.androidth.general.fragments.trade.AbstractBuySellPopupDialogFragment;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.Live1BServiceWrapper;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.LiveConstants;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.SDKUtils;
import com.androidth.general.common.utils.THToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tradehero.route.InjectRoute;
import com.androidth.general.R;
import com.androidth.general.activities.HelpActivity;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.portfolio.AssetClass;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioCompactDTOUtil;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioId;
import com.androidth.general.api.position.GetPositionsDTO;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.position.GetPositionsDTOKeyFactory;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.fragments.alert.AlertCreateDialogFragment;
import com.androidth.general.fragments.alert.AlertEditDialogFragment;
import com.androidth.general.fragments.alert.BaseAlertEditDialogFragment;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.dashboard.RootFragmentType;
import com.androidth.general.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.androidth.general.fragments.portfolio.header.PortfolioHeaderFactory;
import com.androidth.general.fragments.portfolio.header.PortfolioHeaderView;
import com.androidth.general.fragments.position.partial.PositionPartialTopView;
import com.androidth.general.fragments.position.view.PositionLockedView;
import com.androidth.general.fragments.position.view.PositionNothingView;
import com.androidth.general.fragments.security.SecurityListRxFragment;
import com.androidth.general.fragments.security.WatchlistEditFragment;
import com.androidth.general.fragments.settings.AskForInviteDialogFragment;
import com.androidth.general.fragments.settings.SendLoveBroadcastSignal;
import com.androidth.general.fragments.timeline.MeTimelineFragment;
import com.androidth.general.fragments.timeline.PushableTimelineFragment;
import com.androidth.general.fragments.trade.BuySellStockFragment;
import com.androidth.general.fragments.trade.FXMainFragment;
import com.androidth.general.fragments.trade.StockActionBarRelativeLayout;
import com.androidth.general.fragments.trade.TradeListFragment;
import com.androidth.general.fragments.trending.TrendingMainFragment;
import com.androidth.general.fragments.tutorial.WithTutorial;
import com.androidth.general.models.position.PositionDTOUtils;
import com.androidth.general.models.security.ProviderTradableSecuritiesHelper;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.position.GetPositionsCacheRx;
import com.androidth.general.persistence.prefs.ShowAskForInviteDialog;
import com.androidth.general.persistence.prefs.ShowAskForReviewDialog;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.persistence.security.SecurityIdCache;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.dialog.AlertDialogRx;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import com.androidth.general.utils.route.THRouter;
import com.androidth.general.widget.MultiRecyclerScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class PositionListFragment extends DashboardFragment implements WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = PositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = PositionListFragment.class.getName() + ".userBaseKey";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_POSITION_TYPE = PositionListFragment.class.getName() + ".postion.type";
    public static final String BUNDLE_KEY_SHOW_TITLE = PositionListFragment.class.getName() + ".showTitle";
    public static final String BUNDLE_KEY_IS_TRENDING_FX_PORTFOLIO = PositionListFragment.class.getName() + ".trendingFXPortfolio";
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            PositionListFragment.class.getName() + ".purchaseApplicablePortfolioId";

    private static final int FLIPPER_INDEX_LOADING = 0;
    private static final int FLIPPER_INDEX_LIST = 1;
    private static final int FLIPPER_INDEX_ERROR = 2;

    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject GetPositionsCacheRx getPositionsCache;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject SecurityIdCache securityIdCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject PortfolioCacheRx portfolioCache;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject BroadcastUtils broadcastUtils;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
  
    @Bind(R.id.list_flipper) ViewAnimator listViewFlipper;
    SwipeRefreshLayout swipeToRefreshLayout;
    RecyclerView positionRecyclerView;

    @Bind(R.id.btn_help) ImageView btnHelp;
    @Bind(R.id.position_list_header_stub) ViewStub headerStub;

    @Inject Live1BServiceWrapper live1BServiceWrapper;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    protected GetPositionsDTOKey getPositionsDTOKey;
    protected UserBaseKey shownUser;
    private TabbedPositionListFragment.TabType positionType;
    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    private PortfolioHeaderView portfolioHeaderView;

    @Nullable protected UserProfileDTO shownUserProfileDTO;
    @Nullable protected PortfolioDTO portfolioDTO;
    protected List<Object> viewDTOs;

    private PositionItemAdapter positionItemAdapter;
    private int firstPositionVisible = 0;
    private View inflatedView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    private String actionBarColor, actionBarNavUrl;

    SignalRManager signalRManager;
    private Object nothingDTO;

    @Inject
    RequestHeaders requestHeaders;

    private LivePositionDTO livePositionDTOFromBuySell;
    private String requestIdFromBuySell;

    //<editor-fold desc="Arguments Handling">
    public static void putGetPositionsDTOKey(@NonNull Bundle args, @NonNull GetPositionsDTOKey getPositionsDTOKey)
    {
        args.putBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE, getPositionsDTOKey.getArgs());
    }

    @Nullable private static GetPositionsDTOKey getGetPositionsDTOKey(@NonNull Bundle args)
    {
        Bundle bundledKey = args.getBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE);
        if (bundledKey != null)
        {
            return GetPositionsDTOKeyFactory.createFrom(bundledKey);
        }
        return null;
    }

    public static void putShownUser(@NonNull Bundle args, @NonNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    @NonNull private static UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        Bundle userBundle = args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE);
        if (userBundle == null)
        {
            throw new NullPointerException("ShownUser needs to be passed on");
        }
        return new UserBaseKey(userBundle);
    }

    public static void putPositionType(@NonNull Bundle args, TabbedPositionListFragment.TabType positionType)
    {
        args.putString(BUNDLE_KEY_POSITION_TYPE, positionType.name());
    }

    public static void putPositionType(@NonNull Bundle args, StocksMainPositionListFragment.TabType positionType)
    {
        args.putString(BUNDLE_KEY_POSITION_TYPE, positionType.name());
    }

    public static void putPositionType(@NonNull Bundle args, FXMainPositionListFragment.TabType positionType)
    {
        args.putString(BUNDLE_KEY_POSITION_TYPE, positionType.name());
    }

    @NonNull private TabbedPositionListFragment.TabType getPositionType(@NonNull Bundle args)
    {
        return TabbedPositionListFragment.TabType.valueOf(args.getString(
                BUNDLE_KEY_POSITION_TYPE,
                TabbedPositionListFragment.TabType.LONG.name()));
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
            shownUser = getUserBaseKey(args);
        }
        else
        {
            shownUser = injectedUserBaseKey;
        }
        GetPositionsDTOKey keyFromArgs = getGetPositionsDTOKey(args);
        if (keyFromArgs != null)
        {
            getPositionsDTOKey = keyFromArgs;
        }
        else
        {
            getPositionsDTOKey = new OwnedPortfolioId(injectedUserBaseKey.key, injectedPortfolioId.key);
        }

        this.positionItemAdapter = createPositionItemAdapter();
        positionType = getPositionType(args);
        this.purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());

        if(args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR)!=null){
            actionBarColor = args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR);
        }

        if(args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL)!=null){
            actionBarNavUrl = args.getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL);
        }

        if(getArguments().containsKey(AbstractBuySellPopupDialogFragment.KEY_LIVE_DTO)){
            livePositionDTOFromBuySell = getArguments().getParcelable(AbstractBuySellPopupDialogFragment.KEY_LIVE_DTO);
            Log.v("Positions", "Has live dto "+livePositionDTOFromBuySell);
        }

        if(getArguments().containsKey(AbstractBuySellPopupDialogFragment.KEY_LIVE_REQUEST_ID)){
            requestIdFromBuySell = getArguments().getString(AbstractBuySellPopupDialogFragment.KEY_LIVE_REQUEST_ID);
            Log.v("Positions", "Has live request id "+requestIdFromBuySell);
        }
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            firstPositionVisible = savedInstanceState.getInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        }
        return inflater.inflate(R.layout.fragment_positions_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        btnHelp.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                HelpActivity.slideInFromRight(getActivity());
                //viewpager
                //Position chutiye maen dekhna hae
                //inside files
                //lot of shit
                //try more breakpoints
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        swipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById((R.id.swipe_to_refresh_layout));
        positionRecyclerView = (RecyclerView) view.findViewById(R.id.position_recycler_view);

        positionRecyclerView.setLayoutManager(layoutManager);
        positionRecyclerView.setHasFixedSize(true);
        positionRecyclerView.setAdapter(positionItemAdapter);
        swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                PositionListFragment.this.refreshSimplePage();
            }
        });

        if(actionBarNavUrl!=null){
            setActionBarCustomImage(getActivity(), actionBarNavUrl, false);
        }
    }

    protected void pushSecuritiesFragment()
    {
        Bundle args = new Bundle();

        if(actionBarColor!=null){
            args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, actionBarColor);
        }

        if (purchaseApplicableOwnedPortfolioId != null)
        {
            SecurityListRxFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
        }

        if (portfolioDTO == null || portfolioDTO.providerId == null)
        {
            if (portfolioDTO != null && portfolioDTO.assetClass != null
                    && portfolioDTO.assetClass.equals(AssetClass.FX))
            {
                thRouter.open(TrendingMainFragment.getTradeFxPath());
            }
            else
            {
                navigator.get().goToTab(RootFragmentType.TRENDING);
            }
        }
        else
        {
            ProviderTradableSecuritiesHelper.pushTradableSecuritiesList(
                    navigator.get(),
                    args,
                    purchaseApplicableOwnedPortfolioId,
                    portfolioDTO,
                    new ProviderId(portfolioDTO.providerId));
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle(portfolioDTO);
    }

    @Override public void onStart()
    {
        super.onStart();

        nothingDTO = new PositionNothingView.DTO(null, shownUser.equals(currentUserId.toUserBaseKey()));

//        onStopSubscriptions.add(Observable.combineLatest(
//                getProfileAndHeaderObservable(),
//                getPositionsObservable(),
//                new Func2<Pair<UserProfileDTO, PortfolioHeaderView>, List<Pair<PositionDTO, SecurityCompactDTO>>, PortfolioHeaderView>()
//                {
//                    @Override public PortfolioHeaderView call(
//                            @NonNull Pair<UserProfileDTO, PortfolioHeaderView> profileAndHeaderPair,
//                            @NonNull List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
//                    {
//                        //TODO Translate to 0 to restore position
//                        //inflatedHeader.animate().translationY(0f).start();
//                        Log.v("Positions", "Getting positions done");
//                        return profileAndHeaderPair.second;
//                    }
//                })
//                .distinctUntilChanged()
//                .flatMap(new Func1<PortfolioHeaderView, Observable<UserProfileDTO>>()
//                {
//                    @Override public Observable<UserProfileDTO> call(PortfolioHeaderView portfolioHeaderView)
//                    {
//                        Log.v("Positions", "Getting positions done and mapped");
//                        return portfolioHeaderView.getUserActionObservable()
//                                .flatMap(new Func1<PortfolioHeaderView.UserAction, Observable<? extends UserProfileDTO>>()
//                                {
//                                    @Override public Observable<? extends UserProfileDTO> call(PortfolioHeaderView.UserAction userAction)
//                                    {
//                                        return handleHeaderUserAction(userAction);
//                                    }
//                                })
//                                .doOnError(new Action1<Throwable>()
//                                {
//                                    @Override public void call(Throwable e)
//                                    {
//                                        try{
//                                            AlertDialogRxUtil.popErrorMessage(
//                                                    PositionListFragment.this.getActivity(),
//                                                    e);
//                                        }catch (Exception e1){
//                                            e1.printStackTrace();
//                                        }
//
//                                        // TODO
//                                    }
//                                });
//                    }
//                })
//                .doOnError(new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.v("Positions", "Error"+throwable.getLocalizedMessage());
//                    }
//                })
//                .subscribe(
//                        new Action1<UserProfileDTO>()
//                        {
//                            @Override public void call(UserProfileDTO newProfile)
//                            {
//                                // Nothing to do
//                            }
//                        },
//                        new TimberOnErrorAction1("Failed to collect all")));
//
//        onStopSubscriptions.add(
//                Observable.combineLatest(
//                        positionItemAdapter.getUserActionObservable()
//                                .observeOn(AndroidSchedulers.mainThread()),
//                        portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
//                                .observeOn(AndroidSchedulers.mainThread()),
//                        new Func2<PositionPartialTopView.CloseUserAction, Pair<UserBaseKey, PortfolioCompactDTOList>, PositionPartialTopView.CloseUserAction>()
//                        {
//                            @Override public PositionPartialTopView.CloseUserAction call(PositionPartialTopView.CloseUserAction userAction,
//                                    Pair<UserBaseKey, PortfolioCompactDTOList> pair)
//                            {
//                                handleDialogGoToTrade(true,
//                                        userAction.securityCompactDTO,
//                                        userAction.positionDTO,
//                                        PortfolioCompactDTOUtil.getPurchaseApplicablePortfolio(
//                                                pair.second,
//                                                purchaseApplicableOwnedPortfolioId,
//                                                null,
//                                                userAction.securityCompactDTO.getSecurityId())
//                                                .getOwnedPortfolioId());
//
//                                return userAction;
//                            }
//                        })
//                        .doOnError(new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                                Log.v("Positions", "Error 1"+throwable.getLocalizedMessage());
//                            }
//                        })
//                        .subscribe(
//                                new Action1<PositionPartialTopView.CloseUserAction>()
//                                {
//                                    @Override public void call(PositionPartialTopView.CloseUserAction userAction)
//                                    {
//                                        // Nothing to do
//                                    }
//                                },
//                                new TimberAndToastOnErrorAction1("Failed to listen to user action")));






        if(LiveConstants.isInLiveMode) {
            connectOrderManagementSignalR(getActivity());
            if(livePositionDTOFromBuySell!=null){
                List<Object> liveDto = new ArrayList<>();
                liveDto.add(new LivePositionListRowView.LiveDTO(livePositionDTOFromBuySell));
                Log.v("Positions", "Adding "+livePositionDTOFromBuySell);
                linkWith(liveDto);
            }
        }
    }

    @Override public void onPause()
    {
        //firstPositionVisible = positionRecyclerView.getFirstVisiblePosition();
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
    }

    @SuppressLint("NewApi")
    @Override public void onDestroyView()
    {
        disconnectSignalR(portfolioDTO);

        positionRecyclerView.clearOnScrollListeners();
        positionRecyclerView.setOnTouchListener(null);
        swipeToRefreshLayout.setOnRefreshListener(null);
        removeGlobalLayoutListener();
        portfolioHeaderView = null;
        inflatedView = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.positionItemAdapter = null;
        super.onDestroy();
    }


    protected PositionItemAdapter createPositionItemAdapter()
    {
        PositionItemAdapter adapter = new PositionItemAdapter(
                getActivity(),
                getLayoutResIds(),
                currentUserId
        );
        adapter.setOnItemClickedListener(
                new TypedRecyclerAdapter.OnItemClickedListener<Object>()
                {
                    @Override public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<Object> viewHolder, Object object)
                    {
                        handlePositionItemClicked(viewHolder.itemView, position, object);
                    }
                });
        adapter.setOnItemLongClickedListener(
                new TypedRecyclerAdapter.OnItemLongClickedListener<Object>()
                {
                    @Override public boolean onItemLongClicked(int position, TypedRecyclerAdapter.TypedViewHolder<Object> viewHolder, Object object)
                    {
                        return handlePositionItemLongClicked(viewHolder.itemView, position, object);
                    }
                }
        );
        Log.v("Positions", "Creating position item adapter "+adapter);
        return adapter;
    }

    @NonNull private Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_POSITION, R.layout.position_top_view_in_my);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LIVE_POSITION, R.layout.live_position_row_view);
        return layouts;
    }

    @NonNull protected Observable<UserProfileDTO> handleHeaderUserAction(@NonNull PortfolioHeaderView.UserAction userAction)
    {
        if (userAction instanceof PortfolioHeaderView.TimelineUserAction)
        {
            Bundle args = new Bundle();
            UserBaseKey userToSee = userAction.requested.getBaseKey();
            if (currentUserId.toUserBaseKey().equals(userToSee))
            {
                navigator.get().pushFragment(MeTimelineFragment.class, args);
            }
            else
            {
                PushableTimelineFragment.putUserBaseKey(args, userToSee);
                navigator.get().pushFragment(PushableTimelineFragment.class, args);
            }
        }
        else if (userAction instanceof PortfolioHeaderView.FollowUserAction)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Positions_Follow));
            return freeFollow(userAction.requested.getBaseKey());
        }
        else if (userAction instanceof PortfolioHeaderView.UnFollowUserAction)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Positions_Unfollow));
            return unfollow(userAction.requested.getBaseKey());
        }
        throw new IllegalArgumentException("Unhandled PortfolioHeaderView.UserAction " + userAction);
    }

    protected void handlePositionItemClicked(View view, int position, Object object)
    {
        if (view instanceof PositionNothingView)
        {
            if (object instanceof PositionNothingView.DTO && ((PositionNothingView.DTO) object).isCurrentUser)
            {
                pushSecuritiesFragment();
            }
        }
        else if (view instanceof PositionLockedView && shownUserProfileDTO != null)
        {
            //No more locked?
        }
        else
        {
            Bundle args = new Bundle();
            // By default tries
            TradeListFragment.putPositionDTOKey(args,
                    ((PositionPartialTopView.DTO) object).positionDTO.getPositionDTOKey());
            if (purchaseApplicableOwnedPortfolioId != null)
            {
                TradeListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            }
            if (navigator != null)
            {
                if(actionBarColor!=null){
                    args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, actionBarColor);
                }
                navigator.get().pushFragment(TradeListFragment.class, args);
            }
        }
    }

    @NonNull protected Observable<UserProfileDTO> freeFollow(@NonNull final UserBaseKey heroId)
    {
        FollowUserAssistant assistant = new FollowUserAssistant(getActivity(), heroId);
        return assistant.ensureCacheValue()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<FollowUserAssistant>()
                {
                    @Override public void call(FollowUserAssistant followUserAssistant)
                    {
                        followUserAssistant.followingInCache();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<FollowUserAssistant>()
                {
                    @Override public void call(FollowUserAssistant followUserAssistant)
                    {
                        updateHeaderViewFollowButton();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                    {
                        return followUserAssistant.followingInServer();
                    }
                });
    }

    protected void updateHeaderViewFollowButton()
    {
        if (portfolioHeaderView instanceof OtherUserPortfolioHeaderView)
        {
            ((OtherUserPortfolioHeaderView) portfolioHeaderView).configureFollowItemsVisibility();
        }
    }

    @NonNull protected Observable<UserProfileDTO> unfollow(@NonNull final UserBaseKey heroId)
    {
        FollowUserAssistant assistant = new FollowUserAssistant(getActivity(), heroId);
        return assistant.showUnFollowConfirmation(shownUserProfileDTO.displayName)
                .map(new ReplaceWithFunc1<OnDialogClickEvent, FollowUserAssistant>(assistant))
                .flatMap(new Func1<FollowUserAssistant, Observable<FollowUserAssistant>>()
                {
                    @Override public Observable<FollowUserAssistant> call(FollowUserAssistant followUserAssistant)
                    {
                        return followUserAssistant.ensureCacheValue();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<FollowUserAssistant>()
                {
                    @Override public void call(FollowUserAssistant followUserAssistant)
                    {
                        followUserAssistant.unFollowFromCache();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<FollowUserAssistant>()
                {
                    @Override public void call(FollowUserAssistant followUserAssistant)
                    {
                        updateHeaderViewFollowButton();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                    {
                        return followUserAssistant.unFollowFromServer();
                    }
                })
                ;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected boolean handlePositionItemLongClicked(View view, int position, Object item)
    {
        if (item instanceof PositionPartialTopView.DTO)
        {
            final PositionPartialTopView.DTO dto = (PositionPartialTopView.DTO) item;
            onStopSubscriptions.add(Observable.zip(
                    userWatchlistPositionCache.getOne(currentUserId.toUserBaseKey())
                            .subscribeOn(Schedulers.computation())
                            .map(new PairGetSecond<UserBaseKey, WatchlistPositionDTOList>()),
                    alertCompactListCache.getOneSecurityMappedAlerts(currentUserId.toUserBaseKey()),
                    new Func2<WatchlistPositionDTOList, Map<SecurityId, AlertCompactDTO>, StockActionBarRelativeLayout.Requisite>()
                    {
                        @Override public StockActionBarRelativeLayout.Requisite call(WatchlistPositionDTOList watchlistPositionDTOs,
                                Map<SecurityId, AlertCompactDTO> securityIdAlertCompactDTOMap)
                        {
                            return new StockActionBarRelativeLayout.Requisite(
                                    dto.securityCompactDTO.getSecurityId(),
                                    dto.securityCompactDTO,
                                    watchlistPositionDTOs,
                                    securityIdAlertCompactDTOMap);
                        }
                    })
                    .retry()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<StockActionBarRelativeLayout.Requisite, Observable<StockActionBarRelativeLayout.UserAction>>()
                    {
                        @Override
                        public Observable<StockActionBarRelativeLayout.UserAction> call(final StockActionBarRelativeLayout.Requisite requisite)
                        {
                            final StockActionBarRelativeLayout actionView =
                                    (StockActionBarRelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.position_simple_action, null);
                            actionView.display(requisite);
                            Boolean isClosed = dto.positionDTO.isClosed();
                            final BehaviorSubject<AlertDialog> alertDialogSubject =
                                    BehaviorSubject.create(); // We do this to be able to dismiss the dialog
                            return Observable.zip(
                                    alertDialogSubject.flatMap(
                                            new Func1<AlertDialog, Observable<StockActionBarRelativeLayout.UserAction>>()
                                            {
                                                @Override
                                                public Observable<StockActionBarRelativeLayout.UserAction> call(final AlertDialog alertDialog)
                                                {
                                                    return actionView.getUserActionObservable()
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .doOnNext(
                                                                    new Action1<StockActionBarRelativeLayout.UserAction>()
                                                                    {
                                                                        @Override public void call(
                                                                                StockActionBarRelativeLayout.UserAction userAction)
                                                                        {
                                                                            handleDialogUserAction(userAction);
                                                                            alertDialog.dismiss();
                                                                        }
                                                                    });
                                                }
                                            })
                                    ,
                                    AlertDialogRx.build(getActivity())
                                            .setView(actionView)
                                            .setCancelable(true)
                                            .setCanceledOnTouchOutside(true)
                                            .setPositiveButton(
                                                    (isClosed != null && isClosed) || !currentUserId.toUserBaseKey().equals(shownUser)
                                                            ? null
                                                            : getString(R.string.position_close_position_action))
                                            .setNegativeButton(R.string.timeline_trade)
                                            .setNeutralButton(R.string.cancel)
                                            .setAlertDialogObserver(alertDialogSubject)
                                            .build()
                                            .zipWith(
                                                    portfolioCompactListCache.getOne(currentUserId.toUserBaseKey()),
                                                    new Func2<OnDialogClickEvent, Pair<UserBaseKey, PortfolioCompactDTOList>, OnDialogClickEvent>()
                                                    {
                                                        @Override
                                                        public OnDialogClickEvent call(OnDialogClickEvent onDialogClickEvent,
                                                                Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                                                        {
                                                            if (!onDialogClickEvent.isNeutral())
                                                            {
                                                                handleDialogGoToTrade(
                                                                        onDialogClickEvent.isPositive(),
                                                                        dto.securityCompactDTO,
                                                                        dto.positionDTO,
                                                                        PortfolioCompactDTOUtil.getPurchaseApplicablePortfolio(
                                                                                pair.second,
                                                                                purchaseApplicableOwnedPortfolioId,
                                                                                null, // TODO better
                                                                                dto.securityCompactDTO.getSecurityId())
                                                                                .getOwnedPortfolioId());
                                                            }
                                                            return onDialogClickEvent;
                                                        }
                                                    }),
                                    new Func2<StockActionBarRelativeLayout.UserAction, OnDialogClickEvent, StockActionBarRelativeLayout.UserAction>()
                                    {
                                        @Override
                                        public StockActionBarRelativeLayout.UserAction call(StockActionBarRelativeLayout.UserAction userAction,
                                                OnDialogClickEvent onDialogClickEvent)
                                        {
                                            return userAction;
                                        }
                                    });
                        }
                    })
                    .subscribe(
                            new Action1<StockActionBarRelativeLayout.UserAction>()
                            {
                                @Override public void call(StockActionBarRelativeLayout.UserAction userAction)
                                {
                                    Timber.d("Received");
                                }
                            },
                            new TimberAndToastOnErrorAction1("Failed")));

            return true;
        }

        return false;
    }

    public void handleDialogUserAction(@NonNull StockActionBarRelativeLayout.UserAction userAction)
    {
        if (userAction instanceof StockActionBarRelativeLayout.UpdateAlertUserAction)
        {
            AlertEditDialogFragment.newInstance(((StockActionBarRelativeLayout.UpdateAlertUserAction) userAction).alertCompactDTO
                    .getAlertId(currentUserId.toUserBaseKey()))
                    .show(getFragmentManager(), AlertEditDialogFragment.class.getName());
        }
        else if (userAction instanceof StockActionBarRelativeLayout.CreateAlertUserAction)
        {
            AlertCreateDialogFragment.newInstance(userAction.securityId)
                    .show(getFragmentManager(), BaseAlertEditDialogFragment.class.getName());
        }
        else if (userAction instanceof StockActionBarRelativeLayout.WatchlistUserAction)
        {
            Bundle args = new Bundle();
            WatchlistEditFragment.putSecurityId(args, userAction.securityId);
            if (((StockActionBarRelativeLayout.WatchlistUserAction) userAction).add)
            {
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_add_title));
            }
            else
            {
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_edit_title));
            }
            if (navigator != null)
            {
                navigator.get().pushFragment(WatchlistEditFragment.class, args);
            }
        }
        else
        {
            throw new IllegalArgumentException("Unhandled userAction: " + userAction);
        }
    }

    public void handleDialogGoToTrade(boolean andClose,
            @NonNull SecurityCompactDTO securityCompactDTO,
            @NonNull PositionDTO positionDTO,
            @NonNull OwnedPortfolioId applicableOwnedPortfolioId)
    {
        Bundle args = new Bundle();
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            FXMainFragment.putRequisite(
                    args,
                    new FXMainFragment.Requisite(
                            securityCompactDTO.getSecurityId(),
                            applicableOwnedPortfolioId,
                            andClose && positionDTO.shares != null ? positionDTO.shares : 0));
            navigator.get().pushFragment(FXMainFragment.class,args);
        }
        else
        {
            BuySellStockFragment.putRequisite(
                    args,
                    new BuySellStockFragment.Requisite(
                            securityCompactDTO.getSecurityId(),
                            applicableOwnedPortfolioId,
                            andClose && positionDTO.shares != null ? positionDTO.shares : 0));
            navigator.get().pushFragment(BuySellStockFragment.class,args);
        }

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

//                        if(portfolioDTO!=null){
//                            linkPortfolioHeaderView(shownProfile, portfolioDTO);
//                        }

                        return Pair.create(shownProfile, portfolioHeaderView);
                    }
                });
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
                        positionItemAdapter.linkWith(shownProfile);
                        return shownProfile;
                    }
                })
                .doOnError(new ToastOnErrorAction1(getString(R.string.error_fetch_user_profile)));
    }

    @NonNull protected Observable<PortfolioDTO> getPortfolioObservable()
    {
        Log.v("Positions", "getting portfolio: "+getPositionsDTOKey);
        if (getPositionsDTOKey != null && getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            return portfolioCache.get(((OwnedPortfolioId) getPositionsDTOKey))
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Pair<OwnedPortfolioId, PortfolioDTO>, PortfolioDTO>()
                    {
                        @Override public PortfolioDTO call(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                        {
                            linkWith(pair.second);
                            return pair.second;
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
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
//        if(portfolioDTO.providerId!=null && portfolioDTO.providerId>0){
//            setActionBarColorSelf(actionBarNavUrl, actionBarColor);
//        }else{
//            displayActionBarTitle(portfolioDTO);
//        }

        showPrettyReviewAndInvite(portfolioDTO);
        if (portfolioDTO.assetClass == AssetClass.FX)
        {
            btnHelp.setVisibility(View.VISIBLE);
        }
        else
        {
            btnHelp.setVisibility(View.INVISIBLE);
        }
    }

    private void showPrettyReviewAndInvite(@NonNull PortfolioCompactDTO compactDTO)
    {
        if (shownUser != null)
        {
            if (shownUser.getUserId().intValue() != currentUserId.get().intValue())
            {
                return;
            }
        }
        Double profit = compactDTO.roiSinceInception;
        if (profit != null && profit > 0)
        {
            if (mShowAskForReviewDialogPreference.isItTime())
            {
                broadcastUtils.enqueue(new SendLoveBroadcastSignal());
            }
            else if (mShowAskForInviteDialogPreference.isItTime())
            {
                AskForInviteDialogFragment.showInviteDialog(getActivity().getSupportFragmentManager());
            }
        }
    }

//    private void linkPortfolioHeaderView(UserProfileDTO userProfileDTO, @Nullable PortfolioCompactDTO portfolioCompactDTO)
//    {
//        if (portfolioHeaderView == null || inflatedView == null)
//        {
//            // portfolio header
//            int headerLayoutId = PortfolioHeaderFactory.layoutIdFor(getPositionsDTOKey, portfolioCompactDTO, currentUserId);
//            headerStub.setLayoutResource(headerLayoutId);
//            inflatedView = headerStub.inflate();
//            portfolioHeaderView = (PortfolioHeaderView) inflatedView;
//
////            connectPortfolioSignalR(portfolioCompactDTO);
//
//            if(portfolioCompactDTO.getPortfolioId()!=null){
//                connectPortfolioSignalR(portfolioCompactDTO);
//            }
//        }
//
//        portfolioHeaderView.linkWith(userProfileDTO);
//        portfolioHeaderView.linkWith(portfolioCompactDTO);
//
//        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
//        {
//            @SuppressLint("NewApi") @Override public void onGlobalLayout()
//            {
//                if(inflatedView!=null){
//                    ViewTreeObserver observer = inflatedView.getViewTreeObserver();
//                    if (observer != null)
//                    {
//                        if (SDKUtils.isJellyBeanOrHigher())
//                        {
//                            observer.removeOnGlobalLayoutListener(this);
//                        }
//                        else
//                        {
//                            observer.removeGlobalOnLayoutListener(this);
//                        }
//                    }
//                }else{
//                    Log.d(getTag(), "Inflated view is null");
//                    return;
//                }
//
//                int headerHeight = inflatedView.getMeasuredHeight();
//                Timber.d("Header Height %d", headerHeight);
//                positionRecyclerView.setPadding(
//                        positionRecyclerView.getPaddingLeft(),
//                        headerHeight,
//                        positionRecyclerView.getPaddingRight(),
//                        positionRecyclerView.getPaddingBottom());
//
//                positionRecyclerView.addOnScrollListener(new MultiRecyclerScrollListener(
//                        fragmentElements.get().getRecyclerViewScrollListener(),
//                        new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
//                                .header(inflatedView)
//                                .minHeaderTranslation(-inflatedView.getHeight())
//                                .build()
//                ));
//
//                // hack to temporary fix flicker on PositionListFragment
//                positionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
//                {
//                    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
//                    {
//                        super.onScrollStateChanged(recyclerView, newState);
//
//                        if (newState == 0)
//                        {
//                            fragmentElements.get().getMovableBottom().animateShow();
//                            fragmentElements.get().getMovableBottom().setBottomBarVisibility(View.VISIBLE);
//                        }
//                        else
//                        {
//                            fragmentElements.get().getMovableBottom().setBottomBarVisibility(View.GONE);
//                        }
//                    }
//                });
//
//                // hack to temporary fix flicker on PositionListFragment
//                positionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
//                {
//                    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
//                    {
//                        super.onScrolled(recyclerView, dx, dy);
//
//                        if (recyclerView.getChildAt(0).getTop() >= inflatedView.getHeight() / 2)
//                        {
//                            headerStub.setVisibility(View.VISIBLE);
//                        }
//                        else {
//                            headerStub.setVisibility(View.GONE);
//                        }
//                    }
//                });
//            }
//        };
//        inflatedView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
//    }

    @SuppressLint("NewApi")
    private void removeGlobalLayoutListener()
    {
        if (globalLayoutListener != null && inflatedView != null)
        {
            if (SDKUtils.isJellyBeanOrHigher())
            {
                inflatedView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            }
            else
            {
                inflatedView.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
            }
        }
        globalLayoutListener = null;
    }

    @NonNull private Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> getPositionsObservable()
    {
        if(LiveConstants.isInLiveMode){
            return Observable.empty();
        }
        return getPositionsCache.fetch(getPositionsDTOKey)
                .flatMap(new Func1<GetPositionsDTO, Observable<List<Pair<PositionDTO, SecurityCompactDTO>>>>() {
                    @Override
                    public Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> call(GetPositionsDTO getPositionsDTO) {
                        Log.v("Positions", "Got positions "+getPositionsDTO.securities.size());
                        Observable<Pair<PositionDTO, SecurityCompactDTO>> pairObservable;
                        if (getPositionsDTO.positions != null)
                        {
                            pairObservable = PositionDTOUtils.getSecuritiesSoft(
                                    Observable.from(getPositionsDTO.positions),
                                    securityIdCache,
                                    securityCompactCache);
                        }
                        else
                        {
                            pairObservable = Observable.empty();
                        }
                        return pairObservable.toList();
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try{
                            if (viewDTOs == null)
                            {
                                listViewFlipper.setDisplayedChild(FLIPPER_INDEX_ERROR);

                                THToast.show(getString(R.string.error_fetch_position_list_info));
                                Timber.d(throwable, "Error fetching the positionList info");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                })
//                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .doOnNext(new Action1<List<Pair<PositionDTO, SecurityCompactDTO>>>() {
                    @Override
                    public void call(List<Pair<PositionDTO, SecurityCompactDTO>> pairs) {

                    }
                })
                .flatMap(new Func1<List<Pair<PositionDTO, SecurityCompactDTO>>, Observable<List<Pair<PositionDTO, SecurityCompactDTO>>>>()
                {
                    @Override public Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> call(
                            final List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
                    {
                        List<Object> adapterObjects = new ArrayList<>();
                        Log.v("Positions", "Position1 list result: "+pairs.size());
                        try{
                            for (Pair<PositionDTO, SecurityCompactDTO> pair : pairs)
                            {
                                if (pair.first.isLocked())
                                {
                                    adapterObjects.add(new PositionLockedView.DTO(getResources(), pair.first));
                                }
                                else
                                {
                                    adapterObjects.add(new PositionPartialTopView.DTO(
                                            getResources(),
                                            currentUserId,
                                            pair.first,
                                            pair.second));
                                }
                            }
                        }catch (Exception e){
                            Log.v("Positions", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        Log.v("Positions", "Position2 list result: "+adapterObjects.size() +":"+adapterObjects.isEmpty());
                        if(adapterObjects.isEmpty()){
                            Log.v("Positions", "Position adding default");
                            if(pairs.size()>0){
                                adapterObjects.add(new PositionLockedView.DTO(getResources(), pairs.get(0).first));
                                Log.v("Positions", "Position adding default done");
                            }
                        }
                        Log.v("Positions", "Position adding default done returning");
                        return Observable.just(adapterObjects)
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Func1<List<Object>, List<Pair<PositionDTO, SecurityCompactDTO>>>()
                                {
                                    @Override public List<Pair<PositionDTO, SecurityCompactDTO>> call(List<Object> dtoList)
                                    {
                                        Log.v("Positions", "linking: "+dtoList.size());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try{
                                                    linkWith(dtoList);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        return pairs;
                                    }
                                }).doOnError(new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Log.v("Positions", "linking map error");
                                    }
                                });
                    }
                });


//        return getPositionsCache.get(getPositionsDTOKey)
//                .subscribeOn(Schedulers.computation())
//                .flatMap(new Func1<Pair<GetPositionsDTOKey, GetPositionsDTO>, Observable<List<Pair<PositionDTO, SecurityCompactDTO>>>>()
//                {
//                    @Override public Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> call(
//                            Pair<GetPositionsDTOKey, GetPositionsDTO> getPositionsPair)
//                    {
//                        Log.v("Positions", "Got positions "+getPositionsPair.second.positions.size());
//                        Observable<Pair<PositionDTO, SecurityCompactDTO>> pairObservable;
//                        if (getPositionsPair.second.positions != null)
//                        {
//                            pairObservable = PositionDTOUtils.getSecuritiesSoft(
//                                    Observable.from(getPositionsPair.second.positions),
//                                    securityIdCache,
//                                    securityCompactCache);
//                        }
//                        else
//                        {
//                            pairObservable = Observable.empty();
//                        }
//                        return pairObservable.toList();
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnError(new Action1<Throwable>()
//                {
//                    @Override public void call(Throwable throwable)
//                    {
//                        if (viewDTOs == null)
//                        {
//                            listViewFlipper.setDisplayedChild(FLIPPER_INDEX_ERROR);
//
//                            THToast.show(getString(R.string.error_fetch_position_list_info));
//                            Timber.d(throwable, "Error fetching the positionList info");
//                        }
//                    }
//                })
//                .observeOn(Schedulers.computation())
//                .distinctUntilChanged()
//                .doOnNext(new Action1<List<Pair<PositionDTO, SecurityCompactDTO>>>() {
//                    @Override
//                    public void call(List<Pair<PositionDTO, SecurityCompactDTO>> pairs) {
//                        Log.v("Positions", "Position list result: "+pairs.size());
////                        List<Object> adapterObjects = new ArrayList<>();
////                        if(pairs!=null && pairs.size()>0){
////                            if(pairs.get(0).second==null){
////                                adapterObjects.add(new PositionLockedView.DTO(getResources(), pairs.get(0).first));
////                                getActivity().runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        linkWith(adapterObjects);
////                                    }
////                                });
////
////                                return;
////                            }
////                        }
//                    }
//                })
//                .flatMap(new Func1<List<Pair<PositionDTO, SecurityCompactDTO>>, Observable<List<Pair<PositionDTO, SecurityCompactDTO>>>>()
//                {
//                    @Override public Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> call(
//                            final List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
//                    {
//                        List<Object> adapterObjects = new ArrayList<>();
//                        Log.v("Positions", "Position1 list result: "+pairs.size());
//                        for (Pair<PositionDTO, SecurityCompactDTO> pair : pairs)
//                        {
//                            Log.v("Positions", "Pair: "+pair.first.isLocked()+": "+pair.first.securityId + ": "+pair.second.name);
//                            if (pair.first.isLocked())
//                            {
//                                adapterObjects.add(new PositionLockedView.DTO(getResources(), pair.first));
//                            }
//                            else
//                            {
//                                adapterObjects.add(new PositionPartialTopView.DTO(
//                                        getResources(),
//                                        currentUserId,
//                                        pair.first,
//                                        pair.second));
//                            }
//                        }
//
//                        return Observable.just(adapterObjects)
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .map(new Func1<List<Object>, List<Pair<PositionDTO, SecurityCompactDTO>>>()
//                                {
//                                    @Override public List<Pair<PositionDTO, SecurityCompactDTO>> call(List<Object> dtoList)
//                                    {
//                                        Log.v("Positions", "linking: "+dtoList.size());
//                                        linkWith(dtoList);
//                                        return pairs;
//                                    }
//                                });
//                    }
//                });
    }

    public void linkWith(@NonNull List<Object> dtoList)
    {
        this.viewDTOs = dtoList;

//        Object nothingDTO = new PositionNothingView.DTO(null, shownUser.equals(currentUserId.toUserBaseKey()));
        List<Object> filterViewDTOs = filterViewDTOs(viewDTOs, nothingDTO);

        if (!filterViewDTOs.contains(nothingDTO) && this.positionItemAdapter.indexOf(nothingDTO) != RecyclerView.NO_POSITION)
        {
            positionItemAdapter.remove(nothingDTO);
        }
        else if (filterViewDTOs.contains(nothingDTO))
        {
            positionItemAdapter.removeAll();
        }

        try{
            positionItemAdapter.addAll(filterViewDTOs);

            Log.v("Positions", "After linking: "+this.viewDTOs.size());
            Log.v("Positions", "After linking adapter: "+positionItemAdapter.getItemCount());
            swipeToRefreshLayout.setRefreshing(false);
            positionItemAdapter.notifyDataSetChanged();
            listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @NonNull protected List<Object> filterViewDTOs(@NonNull List<Object> dtoList, Object nothingDTO)
    {
        List<Object> filtered = new ArrayList<>();

        for (Object dto : dtoList)
        {
            if (dto instanceof PositionLockedView.DTO)
            {
                if (!positionType.equals(TabbedPositionListFragment.TabType.CLOSED))
                {
                    filtered.add(dto);
                }
            }
            else if (dto instanceof PositionPartialTopView.DTO)
            {
                Boolean isClosed = ((PositionPartialTopView.DTO) dto).positionDTO.isClosed();
                Integer shares = ((PositionPartialTopView.DTO) dto).positionDTO.shares;
                boolean isShort = shares != null && shares < 0;

                if (isClosed != null && isClosed)
                {
                    if (positionType.equals(TabbedPositionListFragment.TabType.CLOSED))
                    {
                        filtered.add(dto);
                    }
                }
                else if (isShort)
                {
                    if (getArguments().getBoolean(BUNDLE_KEY_IS_TRENDING_FX_PORTFOLIO, false) || positionType.equals(
                            TabbedPositionListFragment.TabType.SHORT))
                    {
                        filtered.add(dto);
                    }
                }
                else if (positionType.equals(TabbedPositionListFragment.TabType.LONG))
                {
                    filtered.add(dto);
                }

            }else if(dto instanceof LivePositionListRowView.LiveDTO){
                filtered.add(dto);//!!!!
            }
        }

        if (filtered.size() == 0)
        {
            filtered.add(nothingDTO);
        }

        return filtered;
    }

    protected void refreshSimplePage()
    {
        getPositionsCache.invalidate(getPositionsDTOKey);
        getPositionsCache.get(getPositionsDTOKey);
    }

    private void displayActionBarTitle(@Nullable PortfolioDTO portfolioDTO)
    {
        String title = null;

        if (portfolioDTO != null)
        {
            if (portfolioDTO.title.equals(getString(R.string.my_stocks_con)))
            {
                title = getString(R.string.trending_tab_stocks_main);
            }
            else if (portfolioDTO.title.equals(getString(R.string.my_fx_con)))
            {
                title = getString(R.string.my_fx);
            }
            else
            {
                title = portfolioDTO.title;
            }
        }

        if (title == null)
        {
            title = getString(R.string.position_list_action_bar_header_unknown);
        }

        if(actionBarColor!=null && !actionBarColor.equals("") && actionBarNavUrl!=null && !actionBarNavUrl.equals("")){

            setActionBarColorSelf(actionBarNavUrl, actionBarColor);

        }else if (getArguments().getBoolean(BUNDLE_KEY_SHOW_TITLE, true))
        {
            setActionBarTitle(title);
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }



//    private void connectPortfolioSignalR(PortfolioCompactDTO portfolioCompactDTO){
//        if(signalRManager!=null){
//            return;
//        }
//        signalRManager = new SignalRManager(requestHeaders, currentUserId, LiveNetworkConstants.PORTFOLIO_HUB_NAME);
//
//        Log.d(".java", "connectPortfolioSignalR: requestHeaders " + requestHeaders + " currentUserId " + currentUserId );
//
//        signalRManager.getCurrentProxy().on(LiveNetworkConstants.PROXY_METHOD_UPDATE_PROFILE, new SubscriptionHandler1<Object>() {
//            @Override
//            public void run(Object updatedPortfolio) {
//                //2016-09-08T02:07:19
//                Log.d(".java", "connectPortfolioSignalR: run updatedPortfolio " + updatedPortfolio.toString());
//                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_STANDARD).create();
//                try{
//                    JsonObject jsonObject = gson.toJsonTree(updatedPortfolio).getAsJsonObject();
//                    PortfolioDTO portfolioDTO = gson.fromJson(jsonObject, PortfolioDTO.class);
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try{
//                                portfolioHeaderView.linkWith(portfolioDTO);
//                            }catch (Exception e){
//                                //might not be in the view already
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                }catch (Exception e){
//                    //parsing might be wrong, esp the date
//                    e.printStackTrace();
//                }
//            }
//        }, Object.class);
//
//        signalRManager.getCurrentProxy().on(LiveNetworkConstants.PROXY_METHOD_UPDATE_POSITIONS, new SubscriptionHandler1<List>() {
//
//            @Override
//            public void run(List list) {
//                Log.d("PLF.java", "connectPortfolioSignalR UpdatePosition: " + list.toString());
//                for(int i=0; i<list.size(); i++){
//                    try{
//                        String s = list.get(i).toString();
//
//                        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_STANDARD).create();
//
//                        JsonObject jsonObject = gson.toJsonTree(list.get(i)).getAsJsonObject();
//                        PositionDTO positionDTOFromJSON = gson.fromJson(jsonObject, PositionDTO.class);
//
//                        for(int j=0; j< viewDTOs.size(); j++) {
//                            PositionPartialTopView.DTO dto = ((PositionPartialTopView.DTO)viewDTOs.get(j));
//
//                            final int index = j;
//
//                            if (positionDTOFromJSON.id == dto.positionDTO.id){
//                                ((PositionPartialTopView.DTO)viewDTOs.get(i)).setPositionDTO(positionDTOFromJSON);
//
//                                try{
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            positionItemAdapter.notifyItemChanged(index);
//                                        }
//                                    });
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                    }catch (Exception e){
//                        Log.v("SignalR", "ERROR --"+i);
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, List.class);
//
//        signalRManager.startConnection("SubscribeToPortfolioUpdate", Integer.toString(portfolioCompactDTO.getPortfolioId().key));
//    }

    private void disconnectSignalR(PortfolioCompactDTO portfolioCompactDTO){
//        if(portfolioCompactDTO!=null && signalRManager!=null){
//            signalRManager.startConnection("UnsubscribeFromPortfolioUpdate", Integer.toString(portfolioCompactDTO.getPortfolioId().key));
//
//            signalRManager.getCurrentConnection().disconnect();
//        }
        if(portfolioCompactDTO!=null && signalRManager!=null)
            signalRManager.getCurrentConnection().disconnect();
    }

    private void disconnectSignalR(){
        if(signalRManager!=null){
            signalRManager.getCurrentConnection().disconnect();
        }
    }

    private void connectOrderManagementSignalR(Activity activity)
    {
        if(signalRManager!=null){
            return;
        }
        signalRManager = new SignalRManager(requestHeaders, currentUserId, LiveNetworkConstants.ORDER_MANAGEMENT_HUB_NAME);
        Log.d("Positions", "connectOrderManagementSignalR: listening on PositionsResponse...." );
        signalRManager.getCurrentProxy().on(LiveNetworkConstants.PROXY_METHOD_OM_POSITION_RESPONSE, new SubscriptionHandler1<Object>() {
            @Override
            public void run(Object positionsResponseDTO) {

                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_STANDARD).create();
                try {
                    JsonObject jsonObject = gson.toJsonTree(positionsResponseDTO).getAsJsonObject();
                    PositionsResponseDTO responseDTO = gson.fromJson(jsonObject, PositionsResponseDTO.class);

                    if(responseDTO!=null) {
                        Log.d("Positions", "positionsResponseDTO = " + responseDTO.toString());
                        List<Object> adapterObjects = new ArrayList<>();
                        for(LivePositionDTO dto: responseDTO.Positions){

                            adapterObjects.add(new LivePositionListRowView.LiveDTO(dto));
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    linkWith(adapterObjects);
                                }catch (Exception e){
                                    //might not be in the view
                                    e.printStackTrace();
                                }

                            }
                        });

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }, Object.class);

        signalRManager.getCurrentProxy().on(LiveNetworkConstants.PROXY_METHOD_OM_ERROR_RESPONSE, new SubscriptionHandler1<ErrorResponseDTO>() {
            @Override
            public void run(ErrorResponseDTO errorResponseDTO) {
                if(errorResponseDTO!=null){
                    int errorCode = (int)errorResponseDTO.ErrorCode;
                    switch (errorCode){
                        case 1:
                        case 3:
                            LiveViewProvider.showTradeHubLogin(getActivity());
                            break;
                    }

                }
            }

        }, ErrorResponseDTO.class);




        signalRManager.startConnection();
    }

//    private void GetLivePositions()
//    {
//        if(LiveConstants.isInLiveMode)
//        {
//            connectOrderManagementSignalR();
//            live1BServiceWrapper.getPositions()
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnError(new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            if (throwable != null) {
//                                if (throwable instanceof RetrofitError) {
//
//                                    RetrofitError error = (RetrofitError) throwable;
//                                    Log.d("PLF.java", error.getResponse() + " " + error.toString() + " --URL--> " + error.getResponse().getUrl());
//                                    if (error.getResponse() != null && error.getResponse().getStatus() == 302)
//                                    {
//                                        //pushLiveLogin(error);
//                                    }
//                                    else if (error.getResponse() != null && error.getResponse().getStatus() == 404)
//                                        Toast.makeText(getContext(), "Error connecting to service: " + error.getResponse() + " --body-- " + error.getBody().toString(), Toast.LENGTH_LONG).show();
//                                    else {
//                                        Toast.makeText(getContext(), "Error in stock purchase: " + error.getResponse() + " --body-- " + error.getBody().toString(), Toast.LENGTH_LONG).show();
//                                        Log.d("PLF.java", "Error: " + error.getResponse() + " " + error.getBody().toString() + " --URL--> " + error.getResponse().getUrl());
//
//                                    }
//                                }
//                            }
//                        }
//                    })
//                    //    .subscribe(new BuySellObserver(requisite.securityId, transactionFormDTO, IS_BUY));
//                    .subscribe(new Action1<String>() {
//                                   @Override
//                                   public void call(String getPositions) {
//                                       Log.d("PLF.java", "Success getPositions, result: " + getPositions);
//                                   }
//                               }
//
//                            , new TimberOnErrorAction1("Error purchasing stocks in live mode."));
//
//        }
//
//    }



//
//    @Override  public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
//    {
//        super.onLiveTradingChanged(event);
//        int color =  getResources().getColor(R.color.general_brand_color);
//        Log.d("PLF.java", "Is In Live Mode ? : " + LiveConstants.isInLiveMode);
//        if(LiveConstants.isInLiveMode) {
//            color = getResources().getColor(R.color.general_red_live);
//            //    GetLivePositions();
//        }
//        setActionBarColor(color);
//    }


}
