package com.tradehero.th.fragments.position;

import android.app.ProgressDialog;
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
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKeyFactory;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable("user/:userId/portfolio/:portfolioId")
public class PositionListFragment
        extends BasePurchaseManagerFragment
        implements WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = PositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = PositionListFragment.class.getName() + ".userBaseKey";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_POSITION_TYPE = PositionListFragment.class.getName() + ".postion.type";

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

    @InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.list_flipper) ViewAnimator listViewFlipper;
    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @InjectView(R.id.position_list) ListView positionListView;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    private PortfolioHeaderView portfolioHeaderView;
    @NonNull protected GetPositionsDTOKey getPositionsDTOKey;
    protected PortfolioDTO portfolioDTO;
    protected List<Object> viewDTOs;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    protected PositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;
    @Inject protected THBillingInteractorRx userInteractorRx;

    @NonNull private TabbedPositionListFragment.TabType positionType;

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

        positionListView.setAdapter(positionItemAdapter);
        swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                PositionListFragment.this.refreshSimplePage();
            }
        });
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.position_list)
    protected void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            pushSecurityFragment();
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
            TradeListFragment.putPositionDTOKey(args, ((PositionPartialTopView.DTO) parent.getItemAtPosition(position)).positionDTO.getPositionDTOKey());
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

    protected void pushSecurityFragment()
    {
        Bundle args = new Bundle();

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

        if (ownedPortfolioId != null)
        {
            SecurityListRxFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        if (portfolioDTO != null && portfolioDTO.assetClass != null)
        {
            TrendingMainFragment.putAssetClass(args, portfolioDTO.assetClass);
        }
        navigator.get().pushFragment(TrendingMainFragment.class, args);
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle();
    }

    @Override public void onStart()
    {
        super.onStart();
        linkPortfolioHeader();
        fetchUserProfile();
        fetchPortfolio();
        fetchSimplePage();
    }

    @Override public void onResume()
    {
        super.onResume();
        display();
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
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_LONG, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_SHORT, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED, R.layout.position_top_view);
        return layouts;
    }

    protected void linkPortfolioHeader()
    {
        if (portfolioHeaderView != null)
        {
            onStopSubscriptions.add(portfolioHeaderView.getUserActionObservable()
                    .flatMap(new Func1<PortfolioHeaderView.UserAction, Observable<? extends UserProfileDTO>>()
                    {
                        @Override public Observable<? extends UserProfileDTO> call(PortfolioHeaderView.UserAction userAction)
                        {
                            return PositionListFragment.this.handleHeaderUserAction(userAction);
                        }
                    })
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
    }

    @NonNull protected Observable<UserProfileDTO> handleHeaderUserAction(@NonNull PortfolioHeaderView.UserAction userAction)
    {
        if (userAction instanceof PortfolioHeaderView.TimelineUserAction)
        {
            Bundle args = new Bundle();
            thRouter.save(args, userAction.requested.getBaseKey());
            if (currentUserId.toUserBaseKey().equals(userAction.requested.getBaseKey()))
            {
                navigator.get().pushFragment(MeTimelineFragment.class, args);
            }
            else
            {
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

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get(shownUser))
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_user_profile))));
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        displayHeaderView();
        positionItemAdapter.linkWith(userProfileDTO);
    }

    protected void fetchPortfolio()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    portfolioCache.get(((OwnedPortfolioId) getPositionsDTOKey)))
                    .subscribe(
                            new Action1<Pair<OwnedPortfolioId, PortfolioDTO>>()
                            {
                                @Override public void call(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                                {
                                    linkWith(pair.second);
                                }
                            },
                            new ToastAction<Throwable>(getString(R.string.error_fetch_portfolio_info))
                    ));
        }
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
        displayActionBarTitle();
        showPrettyReviewAndInvite(portfolioDTO);

        preparePortfolioHeaderView(portfolioDTO);
        portfolioHeaderView.linkWith(portfolioDTO);
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

    private void preparePortfolioHeaderView(@NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioHeaderView == null)
        {
            // portfolio header
            int headerLayoutId = PortfolioHeaderFactory.layoutIdFor(getPositionsDTOKey, portfolioCompactDTO, currentUserId);
            headerStub.setLayoutResource(headerLayoutId);
            final View inflatedHeader = headerStub.inflate();
            portfolioHeaderView = (PortfolioHeaderView) inflatedHeader;
            linkPortfolioHeader();

            positionListView.post(new Runnable()
            {
                @Override public void run()
                {
                    AbsListView listView = positionListView;
                    if (listView != null)
                    {
                        int headerHeight = inflatedHeader.getMeasuredHeight();
                        positionListView.setPadding(
                                positionListView.getPaddingLeft(),
                                headerHeight,
                                positionListView.getPaddingRight(),
                                positionListView.getPaddingBottom());
                        listView.setPadding(listView.getPaddingLeft(),
                                headerHeight,
                                listView.getPaddingRight(),
                                listView.getPaddingBottom());
                        listView.setOnScrollListener(new MultiScrollListener(
                                dashboardBottomTabsListViewScrollListener.get(),
                                new QuickReturnListViewOnScrollListener(QuickReturnType.HEADER,
                                        inflatedHeader,
                                        -headerHeight,
                                        null, 0)));
                    }
                }
            });
        }
        else
        {
            Timber.d("Not inflating portfolioHeaderView because already not null");
        }
    }

    protected void fetchSimplePage()
    {
        if (getPositionsDTOKey.isValid())
        {
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    getPositionsCache.get(getPositionsDTOKey)
                            .subscribeOn(Schedulers.computation())
                            .flatMap(
                                    new Func1<Pair<GetPositionsDTOKey, GetPositionsDTO>, Observable<List<Object>>>()
                                    {
                                        @Override public Observable<List<Object>> call(
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
                                            return pairObservable.map(
                                                    new Func1<Pair<PositionDTO, SecurityCompactDTO>, Object>()
                                                    {
                                                        @Override public Object call(Pair<PositionDTO, SecurityCompactDTO> pair)
                                                        {
                                                            if (pair.first.isLocked())
                                                            {
                                                                return new PositionLockedView.DTO(getResources(), pair.first);
                                                            }
                                                            return new PositionPartialTopView.DTO(getResources(), pair.first, pair.second);
                                                        }
                                                    })
                                                    .toList();
                                        }
                                    }))
                    .subscribe(
                            new Action1<List<Object>>()
                            {
                                @Override public void call(List<Object> dtoList)
                                {
                                    PositionListFragment.this.linkWith(dtoList);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    PositionListFragment.this.handleGetPositionsError(error);
                                }
                            }));
        }
    }

    public void handleGetPositionsError(Throwable e)
    {
        if (viewDTOs == null)
        {
            listViewFlipper.setDisplayedChild(FLIPPER_INDEX_ERROR);

            THToast.show(getString(R.string.error_fetch_position_list_info));
            Timber.d(e, "Error fetching the positionList info");
        }
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
        positionListView.smoothScrollToPosition(0);
        display();
    }

    @NonNull protected List<Object> filterViewDTOs(@NonNull List<Object> dtoList)
    {
        List<Object> filtered = new ArrayList<>();
        for (Object dto : dtoList)
        {
            if (dto instanceof PositionPartialTopView.DTO)
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
                    if (positionType.equals(TabbedPositionListFragment.TabType.SHORT))
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

        return filtered;
    }

    protected void refreshSimplePage()
    {
        getPositionsCache.invalidate(getPositionsDTOKey);
        getPositionsCache.get(getPositionsDTOKey);
    }

    private void display()
    {
        displayHeaderView();
        displayActionBarTitle();
    }

    private void displayHeaderView()
    {
        if (portfolioHeaderView != null && userProfileDTO != null)
        {
            Timber.d("displayHeaderView %s", portfolioHeaderView.getClass().getSimpleName());
            if (userProfileDTO != null)
            {
                portfolioHeaderView.linkWith(userProfileDTO);
            }

            if (portfolioDTO != null)
            {
                portfolioHeaderView.linkWith(portfolioDTO);
            }
        }
    }

    private void displayActionBarTitle()
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

        setActionBarTitle(title);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }
}
