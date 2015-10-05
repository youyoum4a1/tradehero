package com.tradehero.th.fragments.position;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.InjectRoute;
import com.tradehero.th.R;
import com.tradehero.th.activities.HelpActivity;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
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
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditDialogFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.trade.StockActionBarRelativeLayout;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.models.security.ProviderTradableSecuritiesHelper;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.ReplaceWithFunc1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.dialog.AlertDialogRx;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiRecyclerScrollListener;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class PositionListFragment
        extends DashboardFragment
        implements WithTutorial
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
    @Inject Analytics analytics;
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
    @Bind(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @Bind(R.id.position_recycler_view) RecyclerView positionRecyclerView;
    @Bind(R.id.btn_help) ImageView btnHelp;
    @Bind(R.id.position_list_header_stub) ViewStub headerStub;

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

    protected PositionItemAdapter positionItemAdapter;
    private int firstPositionVisible = 0;
    private View inflatedView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

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
        this.purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());
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
        ButterKnife.bind(this, view);
        btnHelp.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                HelpActivity.slideInFromRight(getActivity());
            }
        });
        positionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        positionRecyclerView.setHasFixedSize(true);
        positionRecyclerView.setAdapter(positionItemAdapter);
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
        onStopSubscriptions.add(Observable.combineLatest(
                getProfileAndHeaderObservable(),
                getPositionsObservable(),
                new Func2<Pair<UserProfileDTO, PortfolioHeaderView>, List<Pair<PositionDTO, SecurityCompactDTO>>, PortfolioHeaderView>()
                {
                    @Override public PortfolioHeaderView call(
                            @NonNull Pair<UserProfileDTO, PortfolioHeaderView> profileAndHeaderPair,
                            @NonNull List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
                    {
                        //TODO Translate to 0 to restore position
                        //inflatedHeader.animate().translationY(0f).start();
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
                        new TimberOnErrorAction1("Failed to collect all")));

        onStopSubscriptions.add(
                Observable.combineLatest(
                        positionItemAdapter.getUserActionObservable()
                                .observeOn(AndroidSchedulers.mainThread()),
                        portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
                                .observeOn(AndroidSchedulers.mainThread()),
                        new Func2<PositionPartialTopView.CloseUserAction, Pair<UserBaseKey, PortfolioCompactDTOList>, PositionPartialTopView.CloseUserAction>()
                        {
                            @Override public PositionPartialTopView.CloseUserAction call(PositionPartialTopView.CloseUserAction userAction,
                                    Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                            {
                                handleDialogGoToTrade(true,
                                        userAction.securityCompactDTO,
                                        userAction.positionDTO,
                                        PortfolioCompactDTOUtil.getPurchaseApplicablePortfolio(
                                                pair.second,
                                                purchaseApplicableOwnedPortfolioId,
                                                null,
                                                userAction.securityCompactDTO.getSecurityId())
                                                .getOwnedPortfolioId());

                                return userAction;
                            }
                        })
                        .subscribe(
                                new Action1<PositionPartialTopView.CloseUserAction>()
                                {
                                    @Override public void call(PositionPartialTopView.CloseUserAction userAction)
                                    {
                                        // Nothing to do
                                    }
                                },
                                new TimberAndToastOnErrorAction1("Failed to listen to user action")));
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
        return adapter;
    }

    @NonNull private Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_POSITION, R.layout.position_top_view_in_my);
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
            return freeFollow(userAction.requested.getBaseKey());
        }
        else if (userAction instanceof PortfolioHeaderView.UnFollowUserAction)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Positions_Unfollow));
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
        }
        else
        {
            BuySellStockFragment.putRequisite(
                    args,
                    new BuySellStockFragment.Requisite(
                            securityCompactDTO.getSecurityId(),
                            applicableOwnedPortfolioId,
                            andClose && positionDTO.shares != null ? positionDTO.shares : 0));
        }
        navigator.get().pushFragment(
                securityCompactDTO instanceof FxSecurityCompactDTO
                        ? FXMainFragment.class
                        : BuySellStockFragment.class,
                args);
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
                        linkPortfolioHeaderView(shownProfile, portfolioDTO);
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
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
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

        return Observable.just(null);
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
        displayActionBarTitle(portfolioDTO);
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

    private void linkPortfolioHeaderView(UserProfileDTO userProfileDTO, @Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioHeaderView == null || inflatedView == null)
        {
            // portfolio header
            int headerLayoutId = PortfolioHeaderFactory.layoutIdFor(getPositionsDTOKey, portfolioCompactDTO, currentUserId);
            headerStub.setLayoutResource(headerLayoutId);
            inflatedView = headerStub.inflate();
            portfolioHeaderView = (PortfolioHeaderView) inflatedView;
        }

        portfolioHeaderView.linkWith(userProfileDTO);
        portfolioHeaderView.linkWith(portfolioCompactDTO);

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @SuppressLint("NewApi") @Override public void onGlobalLayout()
            {
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
                int headerHeight = inflatedView.getMeasuredHeight();
                Timber.d("Header Height %d", headerHeight);
                positionRecyclerView.setPadding(
                        positionRecyclerView.getPaddingLeft(),
                        headerHeight,
                        positionRecyclerView.getPaddingRight(),
                        positionRecyclerView.getPaddingBottom());

                positionRecyclerView.addOnScrollListener(new MultiRecyclerScrollListener(
                        fragmentElements.get().getRecyclerViewScrollListener(),
                        new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
                                .header(inflatedView)
                                .minHeaderTranslation(-inflatedView.getHeight())
                                .build()
                ));

                // hack to temporary fix flicker on PositionListFragment
                positionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
                {
                    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                    {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == 0)
                        {
                            fragmentElements.get().getMovableBottom().animateShow();
                            fragmentElements.get().getMovableBottom().setBottomBarVisibility(View.VISIBLE);
                        }
                        else
                        {
                            fragmentElements.get().getMovableBottom().setBottomBarVisibility(View.GONE);
                        }
                    }
                });

                // hack to temporary fix flicker on PositionListFragment
                positionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
                {
                    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                    {
                        super.onScrolled(recyclerView, dx, dy);

                        if (recyclerView.getChildAt(0).getTop() >= inflatedView.getHeight() / 2)
                        {
                            headerStub.setVisibility(View.VISIBLE);
                        }
                        else {
                            headerStub.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        inflatedView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

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
                .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
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
                                adapterObjects.add(new PositionPartialTopView.DTO(
                                        getResources(),
                                        currentUserId,
                                        pair.first,
                                        pair.second));
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
        Object nothingDTO = new PositionNothingView.DTO(getResources(), shownUser.equals(currentUserId.toUserBaseKey()));
        List<Object> filterViewDTOs = filterViewDTOs(dtoList, nothingDTO);
        if (!filterViewDTOs.contains(nothingDTO) && positionItemAdapter.indexOf(nothingDTO) != RecyclerView.NO_POSITION)
        {
            positionItemAdapter.remove(nothingDTO);
        }
        else if (filterViewDTOs.contains(nothingDTO))
        {
            positionItemAdapter.removeAll();
        }
        positionItemAdapter.addAll(filterViewDTOs);
        swipeToRefreshLayout.setRefreshing(false);
        listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);
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
