package com.tradehero.th.fragments.position;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.InjectRoute;
import com.tradehero.th.R;
import com.tradehero.th.activities.HelpActivity;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKeyFactory;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditDialogFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.trade.StockActionBarRelativeLayout;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.models.security.ProviderTradableSecuritiesHelper;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.dialog.AlertDialogRx;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PositionListFragment
        extends BasePurchaseManagerFragment
        implements WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = PositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = PositionListFragment.class.getName() + ".userBaseKey";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_POSITION_TYPE = PositionListFragment.class.getName() + ".postion.type";
    public static final String BUNDLE_KEY_SHOW_TITLE = PositionListFragment.class.getName() + ".showTitle";
    public static final String BUNDLE_KEY_IS_TRENDING_FX_PORTFOLIO = PositionListFragment.class.getName() + ".trendingFXPortfolio";

    private static final int FLIPPER_INDEX_LOADING = 0;
    private static final int FLIPPER_INDEX_LIST = 1;
    private static final int FLIPPER_INDEX_ERROR = 2;

    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject GetPositionsCacheRx getPositionsCache;
    @Inject Analytics analytics;
    @Inject SecurityIdCache securityIdCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject PortfolioCacheRx portfolioCache;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject BroadcastUtils broadcastUtils;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject THBillingInteractorRx userInteractorRx;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;

    @InjectView(R.id.list_flipper) ViewAnimator listViewFlipper;
    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @InjectView(R.id.position_list) ListView positionListView;
    @InjectView(R.id.btn_help) ImageView btnHelp;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    protected GetPositionsDTOKey getPositionsDTOKey;
    protected UserBaseKey shownUser;
    private TabbedPositionListFragment.TabType positionType;

    private View inflatedHeader;
    private PortfolioHeaderView portfolioHeaderView;

    @Nullable protected UserProfileDTO userProfileDTO;
    @Nullable protected PortfolioDTO portfolioDTO;
    protected List<Object> viewDTOs;

    protected PositionItemAdapter positionItemAdapter;
    private int firstPositionVisible = 0;

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
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE));
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

    //</editor-fold>

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

        positionType = getPositionType(args);
        this.positionItemAdapter = createPositionItemAdapter();
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
        ButterKnife.inject(this, view);
        btnHelp.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                HelpActivity.slideInFromRight(getActivity());
            }
        });
        positionListView.setAdapter(positionItemAdapter);
        swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                PositionListFragment.this.refreshSimplePage();
            }
        });
    }

    protected void pushSecuritiesFragment()
    {
        Bundle args = new Bundle();

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

        if (ownedPortfolioId != null)
        {
            SecurityListRxFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        if (portfolioDTO == null || portfolioDTO.providerId == null)
        {
            if (portfolioDTO != null && portfolioDTO.assetClass != null)
            {
                TrendingMainFragment.putAssetClass(args, portfolioDTO.assetClass);
                if (portfolioDTO.assetClass != null)
                {
                    TrendingMainFragment.setLastType(portfolioDTO.assetClass);
                }
                TrendingMainFragment.setLastPosition(1);
            }
            navigator.get().pushFragment(TrendingMainFragment.class, args);
        }
        else
        {
            ProviderTradableSecuritiesHelper.pushTradableSecuritiesList(
                    navigator.get(),
                    args,
                    ownedPortfolioId,
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
        onStopSubscriptions.add(Observable.combineLatest(
                getProfileAndHeaderObservable(),
                getPositionsObservable(),
                new Func2<Pair<UserProfileDTO, PortfolioHeaderView>, List<Pair<PositionDTO, SecurityCompactDTO>>, PortfolioHeaderView>()
                {
                    @Override public PortfolioHeaderView call(
                            @NonNull Pair<UserProfileDTO, PortfolioHeaderView> profileAndHeaderPair,
                            @NonNull List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
                    {
                        //Translate to 0 to restore position
                        inflatedHeader.animate().translationY(0f).start();
                        return profileAndHeaderPair.second;
                    }
                })
                .distinctUntilChanged()
                .flatMap(new Func1<PortfolioHeaderView, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(PortfolioHeaderView portfolioHeaderView)
                    {
                        return portfolioHeaderView.getUserActionObservable()
                                .flatMap(new Func1<PortfolioHeaderView.UserAction, Observable<? extends UserProfileDTO>>()
                                {
                                    @Override public Observable<? extends UserProfileDTO> call(PortfolioHeaderView.UserAction userAction)
                                    {
                                        return handleHeaderUserAction(userAction);
                                    }
                                })
                                .doOnError(new Action1<Throwable>()
                                {
                                    @Override public void call(Throwable e)
                                    {
                                        AlertDialogRxUtil.popErrorMessage(
                                                PositionListFragment.this.getActivity(),
                                                e);
                                        // TODO
                                    }
                                });
                    }
                })
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO newProfile)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction("Failed to collect all")));
    }

    @Override public void onPause()
    {
        firstPositionVisible = positionListView.getFirstVisiblePosition();
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

    @Override public void onDestroyView()
    {
        positionListView.setOnScrollListener(null);
        positionListView.setOnTouchListener(null);
        swipeToRefreshLayout.setOnRefreshListener(null);
        portfolioHeaderView = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.positionItemAdapter = null;
        super.onDestroy();
    }

    protected PositionItemAdapter createPositionItemAdapter()
    {
        return new PositionItemAdapter(
                getActivity(),
                getLayoutResIds(),
                currentUserId
        );
    }

    @NonNull private Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_LONG, R.layout.position_top_view_in_my);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_SHORT, R.layout.position_top_view_in_my);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED, R.layout.position_top_view_in_my);
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
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Positions_Follow));
            return showFollowDialog(userAction.requested);
        }
        throw new IllegalArgumentException("Unhandled PortfolioHeaderView.UserAction " + userAction);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.position_list)
    protected void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            pushSecuritiesFragment();
        }
        else if (view instanceof PositionLockedView && userProfileDTO != null)
        {
            onStopSubscriptions.add(showFollowDialog(userProfileDTO)
                    .subscribe(
                            Actions.empty(), // TODO ?
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable e)
                                {
                                    AlertDialogRxUtil.popErrorMessage(
                                            PositionListFragment.this.getActivity(),
                                            e);
                                    // TODO
                                }
                            }
                    ));
        }
        else
        {
            Bundle args = new Bundle();
            // By default tries
            TradeListFragment.putPositionDTOKey(args,
                    ((PositionPartialTopView.DTO) parent.getItemAtPosition(position)).positionDTO.getPositionDTOKey());
            OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
            if (ownedPortfolioId != null)
            {
                TradeListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
            }
            if (navigator != null)
            {
                navigator.get().pushFragment(TradeListFragment.class, args);
            }
        }
    }

    @NonNull protected Observable<UserProfileDTO> showFollowDialog(@NonNull UserProfileDTO toBeFollowed)
    {
        return HeroAlertDialogRxUtil.showFollowDialog(
                getActivity(),
                toBeFollowed,
                UserProfileDTOUtil.IS_NOT_FOLLOWER)
                .flatMap(new Func1<FollowRequest, Observable<? extends UserProfileDTO>>()
                {
                    @Override public Observable<? extends UserProfileDTO> call(final FollowRequest request)
                    {
                        Observable<UserProfileDTO> fromServer;
                        if (request.isPremium)
                        {
                            fromServer = PositionListFragment.this.premiumFollow(request.heroId);
                        }
                        else
                        {
                            fromServer = PositionListFragment.this.freeFollow(request.heroId);
                        }
                        return fromServer
                                .doOnNext(new Action1<UserProfileDTO>()
                                {
                                    @Override public void call(UserProfileDTO userProfileDTO)
                                    {
                                        PositionListFragment.this.handleSuccessfulFollow(request);
                                    }
                                });
                    }
                });
    }

    @NonNull protected Observable<UserProfileDTO> premiumFollow(@NonNull UserBaseKey heroId)
    {
        //noinspection unchecked
        return userInteractorRx.purchaseAndPremiumFollowAndClear(heroId)
                .map(new Func1<PurchaseResult, UserProfileDTO>()
                {
                    @Override public UserProfileDTO call(PurchaseResult result)
                    {
                        return userProfileDTO;
                    }
                });
    }

    @NonNull protected Observable<UserProfileDTO> freeFollow(@NonNull UserBaseKey heroId)
    {
        final ProgressDialog progress = ProgressDialogUtil.create(getActivity(), R.string.following_this_hero);
        return userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new DismissDialogAction0(progress));
    }

    protected void handleSuccessfulFollow(@NonNull FollowRequest request)
    {
        analytics.addEvent(new ScreenFlowEvent(
                request.isPremium
                        ? AnalyticsConstants.PremiumFollow_Success
                        : AnalyticsConstants.FreeFollow_Success,
                AnalyticsConstants.PositionList));
        swipeToRefreshLayout.setRefreshing(true);
        refreshSimplePage();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemLongClick(R.id.position_list)
    protected boolean handlePositionItemLongClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
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
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<StockActionBarRelativeLayout.Requisite, Observable<StockActionBarRelativeLayout.UserAction>>()
                    {
                        @Override public Observable<StockActionBarRelativeLayout.UserAction> call(final StockActionBarRelativeLayout.Requisite requisite)
                        {
                            final StockActionBarRelativeLayout actionView =
                                    (StockActionBarRelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.position_simple_action, null);
                            actionView.display(requisite);
                            return Observable.zip(
                                    actionView.getUserActionObservable()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnNext(new Action1<StockActionBarRelativeLayout.UserAction>()
                                            {
                                                @Override public void call(StockActionBarRelativeLayout.UserAction userAction)
                                                {
                                                    handleDialogUserAction(userAction);
                                                }
                                            }),
                                    AlertDialogRx.build(getActivity())
                                            .setView(actionView)
                                            .setCancelable(true)
                                            .setCanceledOnTouchOutside(true)
                                            .setPositiveButton(R.string.position_close_position_action)
                                            .setNegativeButton(R.string.timeline_trade)
                                            .setNeutralButton(R.string.cancel)
                                            .build()
                                            .doOnNext(new Action1<OnDialogClickEvent>()
                                            {
                                                @Override public void call(OnDialogClickEvent onDialogClickEvent)
                                                {
                                                    handleDialogUserAction(onDialogClickEvent,
                                                            dto.securityCompactDTO,
                                                            dto.positionDTO);
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
                    }).subscribe(
                            new Action1<StockActionBarRelativeLayout.UserAction>()
                            {
                                @Override public void call(StockActionBarRelativeLayout.UserAction userAction)
                                {
                                    Timber.d("Received");
                                }
                            },
                            new ToastAndLogOnErrorAction("Failed")));

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
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_add_title));
            }
            else
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
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

    public void handleDialogUserAction(@NonNull OnDialogClickEvent clickEvent,
            @NonNull SecurityCompactDTO securityCompactDTO,
            @NonNull PositionDTO positionDTO)
    {
        Bundle args = new Bundle();
        switch (clickEvent.which)
        {
            case DialogInterface.BUTTON_POSITIVE:
                if (positionDTO.shares != null)
                {
                    BuySellFragment.putCloseAttribute(args, positionDTO.shares);
                }

            case DialogInterface.BUTTON_NEGATIVE:
                BuySellFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
                BuySellFragment.putApplicablePortfolioId(args, positionDTO.getOwnedPortfolioId());
                navigator.get().pushFragment(
                        securityCompactDTO instanceof FxSecurityCompactDTO
                                ? FXMainFragment.class
                                : BuySellStockFragment.class,
                        args);

                break;
        }
    }

    @NonNull protected Observable<Pair<UserProfileDTO, PortfolioHeaderView>> getProfileAndHeaderObservable()
    {
        return Observable.combineLatest(
                getShownUserProfileObservable(),
                getPortfolioObservable(),
                new Func2<UserProfileDTO, Pair<PortfolioDTO, PortfolioHeaderView>, Pair<UserProfileDTO, PortfolioHeaderView>>()
                {
                    @Override public Pair<UserProfileDTO, PortfolioHeaderView> call(
                            @NonNull UserProfileDTO shownProfile,
                            @NonNull Pair<PortfolioDTO, PortfolioHeaderView> portfolioAndHeader)
                    {
                        portfolioAndHeader.second.linkWith(shownProfile);
                        if (portfolioAndHeader.first != null)
                        {
                            portfolioAndHeader.second.linkWith(portfolioAndHeader.first);
                        }
                        return Pair.create(shownProfile, portfolioAndHeader.second);
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
                        userProfileDTO = shownProfile;
                        positionItemAdapter.linkWith(shownProfile);
                        return shownProfile;
                    }
                })
                .doOnError(new ToastAction<Throwable>(getString(R.string.error_fetch_user_profile)));
    }

    @NonNull protected Observable<Pair<PortfolioDTO, PortfolioHeaderView>> getPortfolioObservable()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            return portfolioCache.get(((OwnedPortfolioId) getPositionsDTOKey))
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Pair<OwnedPortfolioId, PortfolioDTO>, Pair<PortfolioDTO, PortfolioHeaderView>>()
                    {
                        @Override public Pair<PortfolioDTO, PortfolioHeaderView> call(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                        {
                            linkWith(pair.second);
                            return Pair.create(
                                    pair.second,
                                    getPortfolioHeaderView(pair.second));
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

        return Observable.just(Pair.<PortfolioDTO, PortfolioHeaderView>create(null, getPortfolioHeaderView(null)));
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
        displayActionBarTitle(portfolioDTO);
        showPrettyReviewAndInvite(portfolioDTO);
        getPortfolioHeaderView(portfolioDTO);
        portfolioHeaderView.linkWith(portfolioDTO);
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
                AskForInviteDialogFragment.showInviteDialog(getActivity().getFragmentManager());
            }
        }
    }

    @NonNull private PortfolioHeaderView getPortfolioHeaderView(@Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioHeaderView == null)
        {
            // portfolio header
            int headerLayoutId = PortfolioHeaderFactory.layoutIdFor(getPositionsDTOKey, portfolioCompactDTO, currentUserId);
            inflatedHeader = LayoutInflater.from(getActivity()).inflate(headerLayoutId, null);
            portfolioHeaderView = (PortfolioHeaderView) inflatedHeader;
            positionListView.addHeaderView(inflatedHeader, null, false);
        }
        else
        {
            Timber.d("Not inflating portfolioHeaderView because already not null");
        }
        return portfolioHeaderView;
    }

    @NonNull protected Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> getPositionsObservable()
    {
        return getPositionsCache.get(getPositionsDTOKey)
                .subscribeOn(Schedulers.computation())
                .flatMap(new Func1<Pair<GetPositionsDTOKey, GetPositionsDTO>, Observable<List<Pair<PositionDTO, SecurityCompactDTO>>>>()
                {
                    @Override public Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> call(
                            Pair<GetPositionsDTOKey, GetPositionsDTO> getPositionsPair)
                    {
                        Observable<Pair<PositionDTO, SecurityCompactDTO>> pairObservable;
                        if (getPositionsPair.second.positions != null)
                        {
                            pairObservable = PositionDTOUtils.getSecuritiesSoft(
                                    Observable.from(getPositionsPair.second.positions),
                                    securityIdCache,
                                    securityCompactCache);
                        }
                        else
                        {
                            pairObservable = Observable.empty();
                        }
                        return pairObservable.toList();
                    }
                })
                .doOnError(new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        if (viewDTOs == null)
                        {
                            listViewFlipper.setDisplayedChild(FLIPPER_INDEX_ERROR);

                            THToast.show(getString(R.string.error_fetch_position_list_info));
                            Timber.d(throwable, "Error fetching the positionList info");
                        }
                    }
                })
                .flatMap(new Func1<List<Pair<PositionDTO, SecurityCompactDTO>>, Observable<List<Pair<PositionDTO, SecurityCompactDTO>>>>()
                {
                    @Override public Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> call(
                            final List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
                    {
                        List<Object> adapterObjects = new ArrayList<>();
                        for (Pair<PositionDTO, SecurityCompactDTO> pair : pairs)
                        {
                            if (pair.first.isLocked())
                            {
                                adapterObjects.add(new PositionLockedView.DTO(getResources(), pair.first));
                            }
                            else
                            {
                                adapterObjects.add(new PositionPartialTopView.DTO(getResources(), pair.first, pair.second));
                            }
                        }
                        return Observable.just(adapterObjects)
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Func1<List<Object>, List<Pair<PositionDTO, SecurityCompactDTO>>>()
                                {
                                    @Override public List<Pair<PositionDTO, SecurityCompactDTO>> call(List<Object> dtoList)
                                    {
                                        linkWith(dtoList);
                                        return pairs;
                                    }
                                });
                    }
                });
    }

    public void linkWith(@NonNull List<Object> dtoList)
    {
        this.viewDTOs = dtoList;
        positionItemAdapter.setNotifyOnChange(false);
        positionItemAdapter.clear();
        positionItemAdapter.addAll(filterViewDTOs(dtoList));
        positionItemAdapter.notifyDataSetChanged();
        positionItemAdapter.setNotifyOnChange(true);
        swipeToRefreshLayout.setRefreshing(false);
        listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);
    }

    @NonNull protected List<Object> filterViewDTOs(@NonNull List<Object> dtoList)
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
            }
        }

        if (filtered.size() == 0)
        {
            filtered.add(new PositionNothingView.DTO(getResources(), shownUser.equals(currentUserId.toUserBaseKey())));
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
            title = portfolioDTO.title;
        }

        if (title == null)
        {
            title = getString(R.string.position_list_action_bar_header_unknown);
        }

        if (getArguments().getBoolean(BUNDLE_KEY_SHOW_TITLE, true))
        {
            setActionBarTitle(title);
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }
}
