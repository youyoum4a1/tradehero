package com.tradehero.th.fragments.timeline;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClickSticky;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.PrivateDiscussionActivity;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.timeline.key.TimelineKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.tradehero.th.fragments.discussion.DiscussionFragmentUtil;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.watchlist.MainWatchlistPositionFragment;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.level.LevelDefListCacheRx;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.timeline.TimelineCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ReplaceWith;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class TimelineFragment extends DashboardFragment
{
    private static final String USER_BASE_KEY_BUNDLE_KEY = TimelineFragment.class.getName() + ".userBaseKey";

    public static void putUserBaseKey(@NonNull Bundle bundle, @NonNull UserBaseKey userBaseKey)
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

    public enum TabType
    {
        TIMELINE, PORTFOLIO_LIST, STATS
    }

    @Inject Analytics analytics;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;
    @Inject LevelDefListCacheRx levelDefListCache;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;
    @Inject protected THRouter thRouter;
    @Inject protected TimelineCacheRx timelineCache;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject protected THBillingInteractorRx userInteractorRx;
    @Inject CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;

    @InjectView(R.id.timeline_list_view) StickyListHeadersListView timelineListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshContainer;
    @InjectView(R.id.follow_button) Button mFollowButton;
    @InjectView(R.id.message_button) Button mSendMsgButton;
    @InjectView(R.id.follow_message_container) ViewGroup btnContainer;

    protected UserBaseKey shownUserBaseKey;

    protected MessageHeaderDTO messageThreadHeaderDTO;
    @Nullable protected UserProfileDTO shownProfile;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    private MainTimelineAdapter mainTimelineAdapter;
    private UserProfileDetailView userProfileView;
    private View loadingView;
    @Nullable FxOnBoardDialogFragment onBoardDialogFragment;

    public TabType currentTab = TabType.PORTFOLIO_LIST;
    protected boolean mIsOtherProfile = false;
    private boolean cancelRefreshingOnResume;
    //TODO need move to pushableTimelineFragment
    private int mFollowType;//0 not follow, 1 free follow, 2 premium follow
    private boolean mIsHero = false;//whether the showUser follow the user

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        shownUserBaseKey = getShownUserBaseKey();
        if (shownUserBaseKey == null)
        {
            throw new IllegalArgumentException("Should not end up with null shownUserBaseKey");
        }
        mainTimelineAdapter = new MainTimelineAdapter(getActivity(),
                R.layout.timeline_item_view,
                R.layout.portfolio_list_item,
                R.layout.user_profile_stat_view,
                currentUserId.toUserBaseKey().equals(shownUserBaseKey));
        mainTimelineAdapter.setCurrentTabType(currentTab);
    }

    @Nullable protected UserBaseKey getShownUserBaseKey()
    {
        return getUserBaseKey(getArguments());
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        userProfileView = (UserProfileDetailView) inflater.inflate(R.layout.user_profile_detail_view, null);
        loadingView = new ProgressBar(getActivity());
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        if (userProfileView != null)
        {
            //TODO now only one view, userProfileView useless, need cancel, alex
            timelineListView.addHeaderView(userProfileView, null, false);
        }

        if (loadingView != null)
        {
            timelineListView.addFooterView(loadingView);
        }
        timelineListView.setAdapter(mainTimelineAdapter);
        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
        registerButtonClicks();
        fetchLevelDefList();
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

        fetchShownUserProfile();
        fetchMessageThreadHeader();
        fetchPortfolioList();

        onStopSubscriptions.add(AppObservable.bindFragment(this, followerSummaryCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FollowerSummaryObserver()));

        registerUserDiscussionActions();
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
        timelineListView.setOnScrollListener(new MultiScrollListener(fragmentElements.get().getListViewScrollListener(), nearEndScrollListener));
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

        if (cancelRefreshingOnResume)
        {
            swipeRefreshContainer.setRefreshing(false);
            cancelRefreshingOnResume = false;
        }
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
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
                getTimelineObservable(new TimelineKey(TimelineSection.Timeline,
                        shownUserBaseKey,
                        mainTimelineAdapter.getLatestTimelineRange())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> processed)
                            {
                                mainTimelineAdapter.appendHeadTimeline(processed);
                                swipeRefreshContainer.setRefreshing(false);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    protected void loadOlderTimeline()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                getTimelineObservable(new TimelineKey(TimelineSection.Timeline,
                        shownUserBaseKey,
                        mainTimelineAdapter.getOlderTimelineRange())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> processed)
                            {
                                mainTimelineAdapter.appendTailTimeline(processed);
                                loadingView.setVisibility(View.GONE);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    @NonNull protected Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> getTimelineObservable(
            @NonNull TimelineKey key)
    {
        return timelineCache.get(key)
                .subscribeOn(Schedulers.computation())
                .take(1)
                .flatMap(new Func1<Pair<TimelineKey, TimelineDTO>, Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                {
                    @Override public Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                            Pair<TimelineKey, TimelineDTO> pair)
                    {
                        return viewDTOFactory.createTimelineItemViewLinearDTOs(pair.second.getEnhancedItems());
                    }
                });
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
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        mainTimelineAdapter.setProfileClickListener(null);
        timelineListView.setOnScrollListener(null);
        swipeRefreshContainer.setOnRefreshListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        displayablePortfolioFetchAssistant = null;
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

    protected void registerButtonClicks()
    {
        onDestroyViewSubscriptions.add(userProfileView.getButtonClickedObservable()
                .subscribe(
                        new Action1<UserProfileCompactViewHolder.ButtonType>()
                        {
                            @Override public void call(UserProfileCompactViewHolder.ButtonType buttonType)
                            {
                                handleButtonClicked(buttonType);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to register to button clicks")));
    }

    protected void fetchMessageThreadHeader()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                messageThreadHeaderCache.get(shownUserBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TimelineMessageThreadHeaderCacheObserver()));
    }

    //<editor-fold desc="Display methods">
    private void fetchPortfolioList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, displayablePortfolioFetchAssistant.get(shownUserBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
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

    protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        this.shownProfile = userProfileDTO;
        mainTimelineAdapter.setUserProfileDTO(userProfileDTO);
        userProfileView.display(shownProfile);
        displayActionBarTitle();
        mFollowButton.setEnabled(true);
        mSendMsgButton.setEnabled(true);
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
                setActionBarTitle(UserBaseDTOUtil.getLongDisplayName(getResources(), shownProfile));
            }
        }
        else
        {
            setActionBarTitle(R.string.loading_loading);
        }
    }

    protected void fetchShownUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, userProfileCache.get().get(shownUserBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TimelineFragmentUserProfileCacheObserver()));
    }

    protected void fetchLevelDefList()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(this, levelDefListCache.getOne(new LevelDefListId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<LevelDefListId, LevelDefDTOList>>()
                        {
                            @Override public void call(Pair<LevelDefListId, LevelDefDTOList> pair)
                            {
                                userProfileView.setLevelDef(pair.second);
                            }
                        },
                        new TimberOnErrorAction("Failed to fetch level definitions")));
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
                    pushWatchlistPositionFragment();
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
                                    swipeRefreshContainer.setRefreshing(true);
                                    portfolioCompactListCache.invalidate(shownUserBaseKey);
                                    portfolioCompactListCache.get(shownUserBaseKey);
                                    userProfileCache.get().invalidate(shownUserBaseKey);
                                    userProfileCache.get().get(shownUserBaseKey);
                                    swipeRefreshContainer.setRefreshing(false);
                                }
                            },
                            new ToastOnErrorAction()
                    );
        }
    }

    private void pushPositionListFragment(OwnedPortfolioId ownedPortfolioId, @Nullable PortfolioDTO portfolioDTO)
    {
        Bundle args = new Bundle();

        TabbedPositionListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        TabbedPositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
        TabbedPositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());

        if (portfolioDTO != null)
        {
            TabbedPositionListFragment.putIsFX(args, portfolioDTO.assetClass);
            if (portfolioDTO.providerId != null && portfolioDTO.providerId > 0)
            {
                TabbedPositionListFragment.putProviderId(args, new ProviderId(portfolioDTO.providerId));
                navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
                return;
            }
        }
        navigator.get().pushFragment(TabbedPositionListFragment.class, args);
    }

    private void pushWatchlistPositionFragment()
    {
        Bundle args = new Bundle();
        MainWatchlistPositionFragment.putShowActionBarTitle(args, true);
        navigator.get().pushFragment(MainWatchlistPositionFragment.class, args);
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
            linkWith(pair.second);
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
    protected void handleFollowRequested()
    {
        onStopSubscriptions.add(userProfileCache.get().getOne(shownUserBaseKey)
                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserProfileDTO, Observable<Pair<FollowRequest, UserProfileDTO>>>()
                {
                    @Override public Observable<Pair<FollowRequest, UserProfileDTO>> call(UserProfileDTO shownProfile)
                    {
                        return new ChoiceFollowUserAssistantWithDialog(
                                getActivity(),
                                shownProfile)
                                .launchChoiceRx();
                    }
                })
                .subscribe(
                        new Action1<Pair<FollowRequest, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<FollowRequest, UserProfileDTO> pair)
                            {
                                if (!mIsOtherProfile)
                                {
                                    TimelineFragment.this.linkWith(pair.second);
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

    protected void pushPrivateMessageFragment()
    {
        if (navigator == null)
        {
            return;
        }

        Bundle args = new Bundle();
        PrivateDiscussionActivity.putCorrespondentUserBaseKey(args, shownUserBaseKey);
        if (messageThreadHeaderDTO != null)
        {
            PrivateDiscussionActivity.putDiscussionKey(args, DiscussionKeyFactory.create(messageThreadHeaderDTO));
        }
        navigator.get().launchActivity(PrivateDiscussionActivity.class, args);
    }

    protected int getFollowType()
    {
        UserProfileDTO userProfileDTO =
                userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            UserBaseKey purchaserKey = currentUserId.toUserBaseKey();
            if (purchaserKey != null)
            {
                UserProfileDTO purchaserProfile = userProfileCache.get().getCachedValue(purchaserKey);
                if (purchaserProfile != null)
                {
                    return purchaserProfile.getFollowType(shownUserBaseKey);
                }
            }
        }
        return 0;
    }

    protected Observable<UserProfileDTO> freeFollow(@NonNull UserBaseKey heroId)
    {
        ProgressDialog progressDialog = ProgressDialogUtil.create(getActivity(), getString(R.string.following_this_hero));
        return userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
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

    protected void registerUserDiscussionActions()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                mainTimelineAdapter.getUserActionObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserDiscussionAction, Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(UserDiscussionAction userDiscussionAction)
                    {
                        return discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                    }
                })
                .retry()
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                Timber.e(new Exception("Not handled " + userDiscussionAction), "");
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "When registering user actions");
                            }
                        }));
    }

    //<editor-fold desc="UserProfileCompactViewHolder">
    protected void handleButtonClicked(@NonNull UserProfileCompactViewHolder.ButtonType buttonType)
    {
        switch (buttonType)
        {
            case HEROES:
                pushHeroFragment();
                break;

            case FOLLOWERS:
                pushFollowerFragment();
                break;

            case ACHIEVEMENTS:
                pushAchievementFragment();
                break;

            default:
                throw new IllegalArgumentException("Unhandled ButtonType." + buttonType);
        }
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(FollowerManagerFragment.class, bundle);
    }

    protected void pushAchievementFragment()
    {
        Bundle bundle = new Bundle();
        AchievementListFragment.putUserId(bundle, mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(AchievementListFragment.class, bundle);
    }
    //</editor-fold>
}
