package com.tradehero.th.fragments.position;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.activities.HelpActivity;
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
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
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
//TODO need refactor by alex
public class OldPositionListFragment
        extends BasePurchaseManagerFragment
        implements WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = OldPositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = OldPositionListFragment.class.getName() + ".userBaseKey";
    private static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = OldPositionListFragment.class.getName() + ".firstPositionVisible";
    private static final String BUNDLE_KEY_POSITION_TYPE = OldPositionListFragment.class.getName() + ".position.type";
    private static final String BUNDLE_KEY_SHOW_TITLE = OldPositionListFragment.class.getName() + ".showTitle";
    private static final String BUNDLE_KEY_IS_FX_PORTFOLIO = OldPositionListFragment.class.getName() + ".isFXPortfolio";
    private static final String BUNDLE_KEY_SECURITY_ID = OldPositionListFragment.class.getName() + ".securityId";

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

    //@InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.list_flipper) ViewAnimator listViewFlipper;
    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @InjectView(R.id.position_list) ListView positionListView;
    @InjectView(R.id.btn_help) ImageView btnHelp;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    private PortfolioHeaderView portfolioHeaderView;
    @NonNull static protected GetPositionsDTOKey getPositionsDTOKey;
    protected PortfolioDTO portfolioDTO;
    protected List<Object> viewDTOs;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    protected PositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;
    @Inject protected THBillingInteractorRx userInteractorRx;

    @NonNull private TabbedPositionListFragment.TabType positionType;
    private int securityId;
    private boolean isFX;

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

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityIntegerId securityIntegerId)
    {
        args.putInt(BUNDLE_KEY_SECURITY_ID, securityIntegerId.key);
    }

    @NonNull private static SecurityIntegerId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityIntegerId(args.getInt(BUNDLE_KEY_SECURITY_ID, 0));
    }

    public static void putIsFx(@NonNull Bundle args, boolean isFx)
    {
        args.putBoolean(BUNDLE_KEY_IS_FX_PORTFOLIO, isFx);
    }

    private static boolean getIsFx(@NonNull Bundle args)
    {
        return args.getBoolean(BUNDLE_KEY_IS_FX_PORTFOLIO, false);
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
        else if (injectedUserBaseKey != null && injectedPortfolioId != null)
        {
            getPositionsDTOKey = new OwnedPortfolioId(injectedUserBaseKey.key, injectedPortfolioId.key);
        }

        positionType = getPositionType(args);
        isFX = getIsFx(args);
        securityId = getSecurityId(args).key;
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
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.slide_right_in, R.anim.slide_left_out);
                ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
            }
        });
        positionListView.setAdapter(positionItemAdapter);
        swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                OldPositionListFragment.this.refreshSimplePage();
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
                                            OldPositionListFragment.this.getActivity(),
                                            e);
                                    // TODO
                                }
                            }
                    ));
        }
        else if (view instanceof PositionPartialTopView)
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
            if (portfolioDTO.assetClass != null)
            {
                TrendingMainFragment.setLastType(portfolioDTO.assetClass);
            }
            TrendingMainFragment.setLastPosition(1);
        }
        navigator.get().pushFragment(TrendingMainFragment.class, args);
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
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
                            return OldPositionListFragment.this.handleHeaderUserAction(userAction);
                        }
                    })
                    .subscribe(
                            Actions.empty(), // TODO ?
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable e)
                                {
                                    AlertDialogRxUtil.popErrorMessage(
                                            OldPositionListFragment.this.getActivity(),
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
                            fromServer = OldPositionListFragment.this.premiumFollow(request.heroId);
                        }
                        else
                        {
                            fromServer = OldPositionListFragment.this.freeFollow(request.heroId);
                        }
                        return fromServer
                                .doOnNext(new Action1<UserProfileDTO>()
                                {
                                    @Override public void call(UserProfileDTO userProfileDTO)
                                    {
                                        OldPositionListFragment.this.handleSuccessfulFollow(request);
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
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    Timber.e("" + getString(R.string.error_fetch_portfolio_info) + " " + error.toString());
                                }
                            }
                            //new ToastAction<Throwable>(getString(R.string.error_fetch_portfolio_info))
                    ));
        }
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
        showPrettyReviewAndInvite(portfolioDTO);

        preparePortfolioHeaderView(portfolioDTO);
        portfolioHeaderView.linkWith(portfolioDTO);
        if (portfolioDTO != null && portfolioDTO.assetClass == AssetClass.FX) {
            btnHelp.setVisibility(View.VISIBLE);
        } else {
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

    private void preparePortfolioHeaderView(@NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioHeaderView == null)
        {
            // portfolio header
            linkPortfolioHeader();

            positionListView.post(new Runnable()
            {
                @Override public void run()
                {
                    AbsListView listView = positionListView;
                    if (listView != null)
                    {
                        int headerHeight = 0;
                        positionListView.setPadding(
                                positionListView.getPaddingLeft(),
                                headerHeight,
                                positionListView.getPaddingRight(),
                                positionListView.getPaddingBottom());
                        listView.setPadding(listView.getPaddingLeft(),
                                headerHeight,
                                listView.getPaddingRight(),
                                listView.getPaddingBottom());
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
                    .take(1)
                    .subscribe(
                            new Action1<List<Object>>()
                            {
                                @Override public void call(List<Object> dtoList)
                                {
                                    OldPositionListFragment.this.linkWith(dtoList);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    OldPositionListFragment.this.handleGetPositionsError(error);
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
        List<Object> longList = new ArrayList<>();
        List<Object> shortList = new ArrayList<>();
        List<Object> closedList = new ArrayList<>();
        for (Object dto : dtoList)
        {
            if (dto instanceof PositionLockedView.DTO)
            {
                if (!positionType.equals(TabbedPositionListFragment.TabType.CLOSED))
                {
                    filtered.add(dto);
                }
            }
            else if (dto instanceof PositionPartialTopView.DTO && securityId == 0)
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
                    if (isFX || positionType.equals(TabbedPositionListFragment.TabType.SHORT))
                    {
                        filtered.add(dto);
                    }
                }
                else if (positionType.equals(TabbedPositionListFragment.TabType.LONG))
                {
                    filtered.add(dto);
                }
            }
            //for fx/stocks history
            else if (dto instanceof PositionPartialTopView.DTO && securityId != 0)
            {
                Boolean isClosed = ((PositionPartialTopView.DTO) dto).positionDTO.isClosed();
                Integer shares = ((PositionPartialTopView.DTO) dto).positionDTO.shares;
                boolean isShort = shares != null && shares < 0;
                if (((PositionPartialTopView.DTO)dto).securityCompactDTO.id == securityId)
                {
                    if (isClosed != null && isClosed)
                    {
                        closedList.add(dto);
                    }
                    else if (isShort)
                    {
                        shortList.add(dto);
                    }
                    else
                    {
                        longList.add(dto);
                    }
                }
            }
        }
        if (longList.size() > 0)
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    getString(isFX ? R.string.position_list_header_open_long_unsure : R.string.position_list_header_open_unsure),
                    null,
                    null,
                    PositionSectionHeaderItemView.INFO_TYPE_LONG));
            filtered.addAll(longList);
        }
        if (shortList.size() > 0)
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    getString(R.string.position_list_header_open_short),
                    null,
                    null,
                    PositionSectionHeaderItemView.INFO_TYPE_SHORT));
            filtered.addAll(shortList);
        }
        if (closedList.size() > 0)
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    getString(R.string.position_list_header_closed_unsure),
                    null,
                    null,
                    PositionSectionHeaderItemView.INFO_TYPE_CLOSED));
            filtered.addAll(closedList);
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

    private void display()
    {
        displayHeaderView();
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

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }

    static public void setGetPositionsDTOKey(@NonNull GetPositionsDTOKey sgetPositionsDTOKey)
    {
        getPositionsDTOKey = sgetPositionsDTOKey;
    }
}
