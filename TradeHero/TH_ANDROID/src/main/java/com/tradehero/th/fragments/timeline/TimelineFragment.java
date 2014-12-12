package com.tradehero.th.fragments.timeline;

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
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsProfileFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ProfileEvent;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class TimelineFragment extends BasePurchaseManagerFragment
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

    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject Lazy<HeroAlertDialogUtil> heroAlertDialogUtilLazy;
    @Inject Lazy<HeroAlertDialogRxUtil> heroAlertDialogUtilRxLazy;
    @Inject Analytics analytics;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;
    @Inject protected THRouter thRouter;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @InjectView(R.id.timeline_list_view) StickyListHeadersListView timelineListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshContainer;
    @InjectView(R.id.follow_button) Button mFollowButton;
    @InjectView(R.id.message_button) Button mSendMsgButton;
    @InjectView(R.id.follow_message_container) ViewGroup btnContainer;

    @InjectRoute UserBaseKey shownUserBaseKey;

    @Nullable private Subscription followerSummaryCacheSubscription;
    @Nullable private Subscription userProfileCacheSubscription;
    @Nullable private Subscription portfolioSubscription;
    @Nullable protected Subscription messageThreadHeaderFetchSubscription;
    @NonNull protected SubscriptionList subscriptions;

    protected FollowDialogCombo followDialogCombo;
    protected MessageHeaderDTO messageThreadHeaderDTO;
    @Nullable protected UserProfileDTO shownProfile;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    private MainTimelineAdapter mainTimelineAdapter;
    @Nullable private Subscription freeFollowSubscription;
    private UserProfileView userProfileView;
    private View loadingView;

    public TabType currentTab = TabType.PORTFOLIO_LIST;
    protected boolean mIsOtherProfile = false;
    private boolean cancelRefreshingOnResume;
    private int displayingProfileHeaderLayoutId;
    //TODO need move to pushableTimelineFragment
    private int mFollowType;//0 not follow, 1 free follow, 2 premium follow
    private boolean mIsHero = false;//whether the showUser follow the user

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        subscriptions = new SubscriptionList();
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
        mainTimelineAdapter = new MainTimelineAdapter(getActivity(), shownUserBaseKey,
                R.layout.timeline_item_view,
                R.layout.portfolio_list_item_2_0,
                R.layout.user_profile_stat_view);
        mainTimelineAdapter.setCurrentTabType(currentTab);

        getActivity().getSupportLoaderManager().initLoader(
                mainTimelineAdapter.getTimelineLoaderId(), null,
                mainTimelineAdapter.getLoaderTimelineCallback());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_screen, container, false);
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
        fetchPortfolioList();
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

        if (followerSummaryCacheSubscription == null)
        {
            followerSummaryCacheSubscription = AndroidObservable.bindFragment(this, followerSummaryCache.get(currentUserId.toUserBaseKey()))
                    .subscribe(new FollowerSummaryObserver());
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        mainTimelineAdapter.setProfileClickListener(this::display);
        mainTimelineAdapter.setOnLoadFinishedListener(TimelineFragment.this::onLoadFinished);
        mainTimelineAdapter.getTimelineLoader().loadNext();

        FlagNearEdgeScrollListener nearEndScrollListener = createNearEndScrollListener();
        nearEndScrollListener.lowerEndFlag();
        nearEndScrollListener.activateEnd();
        timelineListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsListViewScrollListener.get(), nearEndScrollListener));
        swipeRefreshContainer.setOnRefreshListener(() -> {
            switch (currentTab)
            {
                case TIMELINE:
                    mainTimelineAdapter.getTimelineLoader().loadNext();
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
        dashboardTabHost.get().setOnTranslate((x, y) -> btnContainer.setTranslationY(y));
    }

    private FlagNearEdgeScrollListener createNearEndScrollListener()
    {
        return new FlagNearEdgeScrollListener()
        {
            @Override public void raiseEndFlag()
            {
                super.raiseEndFlag();
                mainTimelineAdapter.getTimelineLoader().loadPrevious();
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
        dashboardTabHost.get().setOnTranslate(null);
        mainTimelineAdapter.setProfileClickListener(null);
        mainTimelineAdapter.setOnLoadFinishedListener(null);
        timelineListView.setOnScrollListener(null);
        swipeRefreshContainer.setOnRefreshListener(null);
        super.onPause();
    }

    @Override public void onStop()
    {
        unsubscribe(freeFollowSubscription);
        freeFollowSubscription = null;
        unsubscribe(messageThreadHeaderFetchSubscription);
        messageThreadHeaderFetchSubscription = null;
        detachFollowDialogCombo();

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(followerSummaryCacheSubscription);
        followerSummaryCacheSubscription = null;
        unsubscribe(userProfileCacheSubscription);
        userProfileCacheSubscription = null;
        unsubscribe(portfolioSubscription);
        portfolioSubscription = null;
        subscriptions.unsubscribe();
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
        messageThreadHeaderFetchSubscription = null;
        super.onDestroy();
    }

    protected void detachFollowDialogCombo()
    {
        FollowDialogCombo followDialogComboCopy = followDialogCombo;
        if (followDialogComboCopy != null)
        {
            followDialogComboCopy.followDialogView.setFollowRequestedListener(null);
        }
        followDialogCombo = null;
    }

    protected void fetchMessageThreadHeader()
    {
        unsubscribe(messageThreadHeaderFetchSubscription);
        messageThreadHeaderFetchSubscription = AndroidObservable.bindFragment(
                this,
                messageThreadHeaderCache.get(shownUserBaseKey))
                .subscribe(new TimelineMessageThreadHeaderCacheObserver());
    }

    //<editor-fold desc="Display methods">
    private void fetchPortfolioList()
    {
        if (portfolioSubscription == null)
        {
            portfolioSubscription = AndroidObservable.bindFragment(this, displayablePortfolioFetchAssistant.get(shownUserBaseKey))
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
                            reportAnalytics(displayablePortfolioDTOs);
                        }
                    });
        }
    }

    private void reportAnalytics(DisplayablePortfolioDTOList displayablePortfolioDTOs)
    {
        Collection<String> collection = new ArrayList<>();
        for (int i = 0; i < displayablePortfolioDTOs.size(); i++)
        {
            if (displayablePortfolioDTOs.get(i).portfolioDTO.providerId != null)
            {
                collection.add(String.valueOf(displayablePortfolioDTOs.get(i).portfolioDTO.providerId));
            }
        }
        if (collection.size() > 0)
        {
            analytics.fireProfileEvent(new ProfileEvent(AnalyticsConstants.CompetitionJoined, collection));
        }
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

    protected void linkWithMessageThread(MessageHeaderDTO messageHeaderDTO, boolean andDisplay)
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
                setActionBarTitle(userBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
            }
        }
        else
        {
            setActionBarTitle(R.string.loading_loading);
        }
    }

    protected void fetchUserProfile()
    {
        if (userProfileCacheSubscription == null)
        {
            userProfileCacheSubscription = AndroidObservable.bindFragment(this, userProfileCache.get().get(shownUserBaseKey))
                    .subscribe(new TimelineFragmentUserProfileCacheObserver());
        }
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
            if (displayablePortfolioDTO.portfolioDTO != null)
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

    /**
     *
     * @param ownedPortfolioId
     * @param portfolioDTO
     */
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
            navigator.get().pushFragment(PositionListFragment.class, args);
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
        mSendMsgButton.setOnClickListener(v -> {
            if (!mIsHero && (mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER
                    || mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG))
            {
                detachFollowDialogCombo();
                subscriptions.add(heroAlertDialogUtilRxLazy.get()
                        .showFollowDialog(
                                getActivity(),
                                shownProfile,
                                UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG)
                        .flatMap(this::handleFollowRequest)
                        .subscribe(new EmptyObserver<>()));
            }
            else
            {
                pushPrivateMessageFragment();
            }
        });
    }

    @NonNull protected Observable<UserProfileDTO> handleFollowRequest(@NonNull FollowRequest request)
    {
        if (request.isPremium)
        {
            //noinspection unchecked
            return userInteractorRx.purchaseAndPremiumFollowAndClear(request.heroId)
                    .materialize().dematerialize()
                    .map(shouldNoHappen -> new UserProfileDTO());
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
            subscriptions.add(
                    new ChoiceFollowUserAssistantWithDialog(
                            getActivity(),
                            shownProfile,
                            getApplicablePortfolioId()).launchChoiceRx()
                            .finallyDo(() -> heroAlertDialogUtilLazy.get().dismissProgressDialog())
                            .subscribe(
                                    pair -> {
                                        if (!mIsOtherProfile)
                                        {
                                            linkWith(pair.second, true);
                                        }
                                        updateBottomButton();
                                        String actionName = pair.first.isPremium
                                                ? AnalyticsConstants.PremiumFollow_Success
                                                : AnalyticsConstants.FreeFollow_Success;
                                        analytics.addEvent(new ScreenFlowEvent(actionName, AnalyticsConstants.Profile));
                                    },
                                    error -> THToast.show(new THException(error))
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
            ReplyPrivateMessageFragment.putDiscussionKey(args, discussionKeyFactory.create(messageThreadHeaderDTO));
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
                userProfileCache.get().getValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                UserBaseKey purchaserKey = applicablePortfolioId.getUserBaseKey();
                if (purchaserKey != null)
                {
                    UserProfileDTO purchaserProfile = userProfileCache.get().getValue(purchaserKey);
                    if (purchaserProfile != null)
                    {
                        return purchaserProfile.getFollowType(shownUserBaseKey);
                    }
                }
            }
            else
            {
                return userProfileDTO.getFollowType(shownUserBaseKey);
            }
        }
        return 0;
    }

    protected Observable<UserProfileDTO> freeFollow(@NonNull UserBaseKey heroId)
    {
        heroAlertDialogUtilLazy.get().showProgressDialog(getActivity(), getString(R.string.following_this_hero));
        return userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(() -> heroAlertDialogUtilLazy.get().dismissProgressDialog());
    }

    protected class TimelineMessageThreadHeaderCacheObserver implements Observer<Pair<UserBaseKey, MessageHeaderDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, MessageHeaderDTO> pair)
        {
            linkWithMessageThread(pair.second, true);
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
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            HeroManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        navigator.get().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            //FollowerManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
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

    @Override public void onEditProfileClicked()
    {
        navigator.get().pushFragment(SettingsProfileFragment.class);
    }
    //</editor-fold>
}
