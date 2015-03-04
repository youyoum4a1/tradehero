package com.tradehero.th.fragments.timeline;

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
import android.widget.Button;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClickSticky;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.InjectRoute;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.timeline.key.TimelineKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.timeline.TimelineCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ReplaceWith;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class TimelineFragment extends DashboardFragment
        implements UserProfileCompactViewHolder.OnProfileClickedListener
{
    private static final String USER_BASE_KEY_BUNDLE_KEY = TimelineFragment.class.getName() + ".userBaseKey";

    public static void putUserBaseKey(Bundle bundle, UserBaseKey userBaseKey)
    {
        bundle.putBundle(USER_BASE_KEY_BUNDLE_KEY, userBaseKey.getArgs());
    }

    @Nullable protected static UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        if (args.containsKey(USER_BASE_KEY_BUNDLE_KEY))
        {
            return new UserBaseKey(args.getBundle(USER_BASE_KEY_BUNDLE_KEY));
        }
        return null;
    }

    public static enum TabType
    {
        TIMELINE, PORTFOLIO_LIST, STATS
    }

    @Inject Analytics analytics;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;
    @Inject protected THRouter thRouter;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @InjectView(R.id.timeline_list_view) StickyListHeadersListView timelineListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshContainer;
    @InjectView(R.id.follow_button) Button mFollowButton;
    @InjectView(R.id.message_button) Button mSendMsgButton;
    @InjectView(R.id.follow_message_container) ViewGroup btnContainer;

    @InjectRoute UserBaseKey shownUserBaseKey;

    protected MessageHeaderDTO messageThreadHeaderDTO;
    @Nullable protected UserProfileDTO shownProfile;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    private MainTimelineAdapter mainTimelineAdapter;
    private UserProfileView userProfileView;
    private View loadingView;
    protected ChoiceFollowUserAssistantWithDialog choiceFollowUserAssistantWithDialog;
    @Nullable FxOnBoardDialogFragment onBoardDialogFragment;

    public TabType currentTab = TabType.PORTFOLIO_LIST;
    protected boolean mIsOtherProfile = false;
    private boolean cancelRefreshingOnResume;
    private int displayingProfileHeaderLayoutId;
    //TODO need move to pushableTimelineFragment
    private int mFollowType;//0 not follow, 1 free follow, 2 premium follow
    private boolean mIsHero = false;//whether the showUser follow the user
    @Inject protected THBillingInteractorRx userInteractorRx;
    @Inject CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        shownUserBaseKey = getUserBaseKey(getArguments());
        if (shownUserBaseKey == null)
        {
            thRouter.inject(this);
        }
        // This is due to THRouter creating
        // noinspection ConstantConditions
        if (shownUserBaseKey == null || shownUserBaseKey.key == null)
        {
            shownUserBaseKey = currentUserId.toUserBaseKey();
        }
        mainTimelineAdapter = new MainTimelineAdapter(getActivity(),
                R.layout.timeline_item_view,
                R.layout.portfolio_list_item,
                R.layout.user_profile_stat_view);
        mainTimelineAdapter.setCurrentTabType(currentTab);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        userProfileView = (UserProfileView) inflater.inflate(R.layout.user_profile_view, null);
        loadingView = new ProgressBar(getActivity());
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        if (userProfileView != null)
        {
            //TODO now only one view, userProfileView useless, need cancel, alex

            userProfileView.setProfileClickedListener(this);
            timelineListView.addHeaderView(userProfileView);
        }

        if (loadingView != null)
        {
            timelineListView.addFooterView(loadingView);
        }
        timelineListView.setAdapter(mainTimelineAdapter);
        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class FollowerSummaryObserver implements Observer<Pair<UserBaseKey, FollowerSummaryDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, FollowerSummaryDTO> pair)
        {
            updateHeroType(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(e.getMessage());
        }
    }

    private void updateHeroType(FollowerSummaryDTO value)
    {
        if (value != null && value.userFollowers.size() > 0)
        {
            for (UserFollowerDTO userFollowerDTO : value.userFollowers)
            {
                if (userFollowerDTO.id == shownUserBaseKey.key)
                {
                    mIsHero = true;
                    return;
                }
            }
        }
        mIsHero = false;
    }

    @Override public void onStart()
    {
        super.onStart();

        fetchUserProfile();
        fetchMessageThreadHeader();
        fetchPortfolioList();

        onStopSubscriptions.add(AppObservable.bindFragment(this, followerSummaryCache.get(currentUserId.toUserBaseKey()))
                .subscribe(new FollowerSummaryObserver()));
    }

    @Override public void onResume()
    {
        super.onResume();
        mainTimelineAdapter.setProfileClickListener(new TimelineProfileClickListener()
        {
            @Override public void onBtnClicked(@NonNull TabType tabType)
            {
                TimelineFragment.this.display(tabType);
            }
        });
        loadLatestTimeline();

        FlagNearEdgeScrollListener nearEndScrollListener = createNearEndScrollListener();
        nearEndScrollListener.lowerEndFlag();
        nearEndScrollListener.activateEnd();
        timelineListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsListViewScrollListener.get(), nearEndScrollListener));
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                switch (currentTab)
                {
                    case TIMELINE:
                        loadLatestTimeline();
                        break;

                    case STATS:
                    case PORTFOLIO_LIST:
                        portfolioCompactListCache.invalidate(shownUserBaseKey);
                        portfolioCompactListCache.get(shownUserBaseKey);
                        userProfileCache.get().invalidate(shownUserBaseKey);
                        userProfileCache.get().get(shownUserBaseKey);
                        break;

                    default:
                        throw new IllegalArgumentException("Unhandled tabType " + currentTab);
                }
            }
        });
        //timelineListView.setOnLastItemVisibleListener(new TimelineLastItemVisibleListener());

        if (userProfileView != null && displayingProfileHeaderLayoutId != 0)
        {
            userProfileView.setDisplayedChildByLayoutId(displayingProfileHeaderLayoutId);
        }

        if (cancelRefreshingOnResume)
        {
            swipeRefreshContainer.setRefreshing(false);
            cancelRefreshingOnResume = false;
        }
        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                btnContainer.setTranslationY(y);
            }
        });
    }

    protected void loadLatestTimeline()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                timelineCache.get(
                        new TimelineKey(TimelineSection.Timeline,
                                shownUserBaseKey,
                                mainTimelineAdapter.getLatestTimelineRange()))
                        .take(1))
                .subscribe(
                        pair -> {
                            mainTimelineAdapter.appendHeadTimeline(pair.second.getEnhancedItems());
                            swipeRefreshContainer.setRefreshing(false);
                        },
                        new ToastOnErrorAction()));
    }

    protected void loadOlderTimeline()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                timelineCache.get(
                        new TimelineKey(TimelineSection.Timeline,
                                shownUserBaseKey,
                                mainTimelineAdapter.getOlderTimelineRange()))
                        .take(1))
                .subscribe(
                        pair -> {
                            mainTimelineAdapter.appendTailTimeline(pair.second.getEnhancedItems());
                            loadingView.setVisibility(View.GONE);
                        },
                        new ToastOnErrorAction()));
    }

    private FlagNearEdgeScrollListener createNearEndScrollListener()
    {
        return new FlagNearEdgeScrollListener()
        {
            @Override public void raiseEndFlag()
            {
                super.raiseEndFlag();
                loadOlderTimeline();
                loadingView.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override public void onPause()
    {
        if (userProfileView != null)
        {
            displayingProfileHeaderLayoutId = userProfileView.getDisplayedChildLayoutId();
        }
        DashboardTabHost tabHost = dashboardTabHost.get();
        if (tabHost != null)
        {
            tabHost.setOnTranslate(null);
        }
        mainTimelineAdapter.setProfileClickListener(null);
        timelineListView.setOnScrollListener(null);
        swipeRefreshContainer.setOnRefreshListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        displayablePortfolioFetchAssistant = null;

        if (userProfileView != null)
        {
            userProfileView.setProfileClickedListener(null);
        }
        this.userProfileView = null;
        this.loadingView = null;

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        mainTimelineAdapter = null;
        super.onDestroy();
    }

    protected void fetchMessageThreadHeader()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                messageThreadHeaderCache.get(shownUserBaseKey))
                .subscribe(new TimelineMessageThreadHeaderCacheObserver()));
    }

    //<editor-fold desc="Display methods">
    private void fetchPortfolioList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, displayablePortfolioFetchAssistant.get(shownUserBaseKey))
                .subscribe(new Observer<DisplayablePortfolioDTOList>()
                {
                    @Override public void onCompleted()
                    {
                        Timber.d("completed");
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "error");
                    }

                    @Override public void onNext(DisplayablePortfolioDTOList displayablePortfolioDTOs)
                    {
                        onLoadFinished();
                        displayPortfolios(displayablePortfolioDTOs);
                    }
                }));
    }

    protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.shownProfile = userProfileDTO;
        mainTimelineAdapter.setUserProfileDTO(userProfileDTO);
        if (andDisplay)
        {
            updateView();
            mFollowButton.setEnabled(true);
            mSendMsgButton.setEnabled(true);
        }
    }

    protected void display(@NonNull TabType tabType)
    {
        currentTab = tabType;
        mainTimelineAdapter.setCurrentTabType(tabType);
    }

    protected void linkWithMessageThread(MessageHeaderDTO messageHeaderDTO)
    {
        this.messageThreadHeaderDTO = messageHeaderDTO;
    }

    protected void updateView()
    {
        if (userProfileView != null)
        {
            userProfileView.display(shownProfile);
        }
        displayActionBarTitle();
    }
    //</editor-fold>

    protected void displayActionBarTitle()
    {
        if (shownProfile != null)
        {
            if (shownProfile.id == currentUserId.get())
            {
                setActionBarTitle(getString(R.string.me));
            }
            else
            {
                setActionBarTitle(UserBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
            }
        }
        else
        {
            setActionBarTitle(R.string.loading_loading);
        }
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, userProfileCache.get().get(shownUserBaseKey))
                .subscribe(new TimelineFragmentUserProfileCacheObserver()));
    }

    /** item of Portfolio tab is clicked */
    @SuppressWarnings("UnusedDeclaration")
    @OnItemClickSticky(R.id.timeline_list_view)
    protected void onMainItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Object item = adapterView.getItemAtPosition(i);
        if (item instanceof DisplayablePortfolioDTO)
        {
            DisplayablePortfolioDTO displayablePortfolioDTO = (DisplayablePortfolioDTO) item;
            if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
            {
                popEnrollFx();
            }
            else if (displayablePortfolioDTO.portfolioDTO != null)
            {
                if (displayablePortfolioDTO.portfolioDTO.isWatchlist)
                {
                    pushWatchlistPositionFragment(displayablePortfolioDTO.ownedPortfolioId);
                }
                else
                {
                    pushPositionListFragment(displayablePortfolioDTO.ownedPortfolioId, displayablePortfolioDTO.portfolioDTO);
                }
            }
        }
        else
        {
            Timber.d("TimelineFragment, unhandled view %s", view);
        }
    }

    private void popEnrollFx()
    {
        if (onBoardDialogFragment == null)
        {
            onBoardDialogFragment = FxOnBoardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
            onBoardDialogFragment.getDismissedObservable()
                    .subscribe(
                            new Action1<DialogInterface>()
                            {
                                @Override public void call(DialogInterface dialog)
                                {
                                    onBoardDialogFragment = null;
                                }
                            },
                            new ToastOnErrorAction()
                    );
            onBoardDialogFragment.getUserActionTypeObservable()
                    .subscribe(
                            new Action1<FxOnBoardDialogFragment.UserActionType>()
                            {
                                @Override public void call(FxOnBoardDialogFragment.UserActionType action)
                                {
                                    if (action.equals(FxOnBoardDialogFragment.UserActionType.ENROLLED))
                                    {
                                        portfolioCompactListCache.get(currentUserId.toUserBaseKey());
                                    }
                                }
                            },
                            new TimberOnErrorAction("")
                    );
        }
    }

    private void pushPositionListFragment(OwnedPortfolioId ownedPortfolioId, @Nullable PortfolioDTO portfolioDTO)
    {
        Bundle args = new Bundle();

        PositionListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        PositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
        PositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());

        if (portfolioDTO != null && portfolioDTO.providerId != null && portfolioDTO.providerId > 0)
        {
            CompetitionLeaderboardPositionListFragment.putProviderId(args, new ProviderId(portfolioDTO.providerId));
            navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
        }
        else
        {
            if (portfolioDTO != null)
            {
                TabbedPositionListFragment.putIsFX(args, portfolioDTO.assetClass);
            }
            navigator.get().pushFragment(TabbedPositionListFragment.class, args);
        }
    }

    /**
     * Go to watchlist
     */
    private void pushWatchlistPositionFragment(OwnedPortfolioId ownedPortfolioId)
    {
        Bundle args = new Bundle();
        WatchlistPositionFragment.putOwnedPortfolioId(args, ownedPortfolioId);
        navigator.get().pushFragment(WatchlistPositionFragment.class, args);
    }

    private void onLoadFinished()
    {
        swipeRefreshContainer.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
        cancelRefreshingOnResume = true;
    }

    protected class TimelineFragmentUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            if (currentTab == TabType.STATS)
            {
                onLoadFinished();
            }
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_user_profile));
        }
    }

    public void displayPortfolios(DisplayablePortfolioDTOList displayablePortfolioDTOs)
    {
        this.mainTimelineAdapter.setDisplayablePortfolioItems(displayablePortfolioDTOs);
    }

    protected void updateBottomButton()
    {
        if (!mIsOtherProfile)
        {
            return;
        }
        mFollowType = getFollowType();
        if (mFollowType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
        {
            mFollowButton.setText(R.string.upgrade_to_premium);
        }
        else if (mFollowType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            mFollowButton.setText(R.string.following_premium);
        }
        mFollowButton.setVisibility(View.VISIBLE);
        mSendMsgButton.setVisibility(View.VISIBLE);
        mSendMsgButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (!mIsHero && (mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER
                        || mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG))
                {
                    onStopSubscriptions.add(HeroAlertDialogRxUtil.showFollowDialog(
                            TimelineFragment.this.getActivity(),
                            shownProfile,
                            UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG)
                            .flatMap(new Func1<FollowRequest, Observable<? extends UserProfileDTO>>()
                            {
                                @Override public Observable<? extends UserProfileDTO> call(FollowRequest request)
                                {
                                    return TimelineFragment.this.handleFollowRequest(request);
                                }
                            })
                            .subscribe(
                                    new EmptyAction1<UserProfileDTO>(),
                                    new EmptyAction1<Throwable>()));
                }
                else
                {
                    TimelineFragment.this.pushPrivateMessageFragment();
                }
            }
        });
    }

    @NonNull protected Observable<UserProfileDTO> handleFollowRequest(@NonNull FollowRequest request)
    {
        if (request.isPremium)
        {
            //noinspection unchecked
            return userInteractorRx.purchaseAndPremiumFollowAndClear(request.heroId)
                    .map(new ReplaceWith<>(new UserProfileDTO()));
        }
        else
        {
            return freeFollow(request.heroId);
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.follow_button)
    protected void handleFollowRequested(View view)
    {
        if (shownProfile == null)
        {
            throw new IllegalArgumentException("We should not have arrived here");
        }
        else
        {
            onStopSubscriptions.add(
                    new ChoiceFollowUserAssistantWithDialog(
                            getActivity(),
                            shownProfile
                            //                            getApplicablePortfolioId()
                    )
                            .launchChoiceRx()
                            .finallyDo(new Action0()
                            {
                                @Override public void call()
                                {
                                    AlertDialogUtil.dismissProgressDialog();
                                }
                            })
                            .subscribe(
                                    new Action1<Pair<FollowRequest, UserProfileDTO>>()
                                    {
                                        @Override public void call(Pair<FollowRequest, UserProfileDTO> pair)
                                        {
                                            if (!mIsOtherProfile)
                                            {
                                                TimelineFragment.this.linkWith(pair.second, true);
                                            }
                                            TimelineFragment.this.updateBottomButton();
                                            String actionName = pair.first.isPremium
                                                    ? AnalyticsConstants.PremiumFollow_Success
                                                    : AnalyticsConstants.FreeFollow_Success;
                                            analytics.addEvent(new ScreenFlowEvent(actionName, AnalyticsConstants.Profile));
                                        }
                                    },
                                    new ToastOnErrorAction()
                            ));
        }
    }

    protected void pushPrivateMessageFragment()
    {
        if (navigator == null)
        {
            return;
        }
        if (messageThreadHeaderDTO != null)
        {
            Bundle args = new Bundle();
            ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, shownUserBaseKey);
            ReplyPrivateMessageFragment.putDiscussionKey(args, DiscussionKeyFactory.create(messageThreadHeaderDTO));
            navigator.get().pushFragment(NewPrivateMessageFragment.class, args);
        }
        else
        {
            Bundle args = new Bundle();
            NewPrivateMessageFragment.putCorrespondentUserBaseKey(args, shownUserBaseKey);
            navigator.get().pushFragment(NewPrivateMessageFragment.class, args);
        }
    }

    /**
     * Null means unsure.
     */
    protected int getFollowType()
    {
        UserProfileDTO userProfileDTO =
                userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            //            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            //            if (applicablePortfolioId != null)
            //            {
            //                UserBaseKey purchaserKey = applicablePortfolioId.getUserBaseKey();
            UserBaseKey purchaserKey = currentUserId.toUserBaseKey();
            if (purchaserKey != null)
            {
                UserProfileDTO purchaserProfile = userProfileCache.get().getCachedValue(purchaserKey);
                if (purchaserProfile != null)
                {
                    return purchaserProfile.getFollowType(shownUserBaseKey);
                }
            }
            //            }
            //            else
            //            {
            //                return userProfileDTO.getFollowType(shownUserBaseKey);
            //            }
        }
        return 0;
    }

    protected Observable<UserProfileDTO> freeFollow(@NonNull UserBaseKey heroId)
    {
        AlertDialogUtil.showProgressDialog(getActivity(), getString(R.string.following_this_hero));
        return userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        AlertDialogUtil.dismissProgressDialog();
                    }
                });
    }

    protected class TimelineMessageThreadHeaderCacheObserver implements Observer<Pair<UserBaseKey, MessageHeaderDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, MessageHeaderDTO> pair)
        {
            linkWithMessageThread(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (!(e instanceof RetrofitError) ||
                    (((RetrofitError) e).getResponse() != null &&
                            ((RetrofitError) e).getResponse().getStatus() != 404))
            {
                THToast.show(R.string.error_fetch_message_thread_header);
                Timber.e(e, "Error while getting message thread");
            }
        }
    }

    //<editor-fold desc="UserProfileCompactViewHolder">
    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        //        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        //        if (applicablePortfolio != null)
        //        {
        //            HeroManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        //        }
        navigator.get().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        //        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        //        if (applicablePortfolio != null)
        //        {
        //            //FollowerManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        //        }
        navigator.get().pushFragment(FollowerManagerFragment.class, bundle);
    }

    protected void pushAchievementFragment()
    {
        Bundle bundle = new Bundle();
        AchievementListFragment.putUserId(bundle, mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(AchievementListFragment.class, bundle);
    }

    @Override public void onHeroClicked()
    {
        pushHeroFragment();
    }

    @Override public void onFollowerClicked()
    {
        pushFollowerFragment();
    }

    @Override public void onAchievementClicked()
    {
        pushAchievementFragment();
    }
    //</editor-fold>
}
