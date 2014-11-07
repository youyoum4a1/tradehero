package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStickyListHeadersListView;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.utils.THToast;
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
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsProfileFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
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
    @Inject Analytics analytics;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;
    @Inject protected THRouter thRouter;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @InjectView(R.id.timeline_list_view) PullToRefreshStickyListHeadersListView timelineListView;
    @InjectView(R.id.follow_button) Button mFollowButton;
    @InjectView(R.id.message_button) Button mSendMsgButton;
    @InjectView(R.id.follow_message_container) ViewGroup btnContainer;

    @InjectRoute UserBaseKey shownUserBaseKey;

    @Nullable private Subscription followerSummaryCacheSubscription;
    @Nullable private Subscription userProfileCacheSubscription;
    @Nullable private Subscription portfolioSubscription;
    @Nullable protected Subscription messageThreadHeaderFetchSubscription;

    protected FollowDialogCombo followDialogCombo;
    protected MessageHeaderDTO messageThreadHeaderDTO;
    protected UserProfileDTO shownProfile;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    private MainTimelineAdapter mainTimelineAdapter;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;
    private UserProfileView userProfileView;
    private View loadingView;
    protected ChoiceFollowUserAssistantWithDialog choiceFollowUserAssistantWithDialog;

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

        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override protected void initViews(View view)
    {
        if (userProfileView != null)
        {
            //TODO now only one view, userProfileView useless, need cancel, alex

            userProfileView.setProfileClickedListener(this);
            timelineListView.getRefreshableView().addHeaderView(userProfileView);
        }

        if (loadingView != null)
        {
            timelineListView.addFooterView(loadingView);
        }
        timelineListView.setAdapter(mainTimelineAdapter);

        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();

        fetchPortfolioList();
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

        unsubscribe(followerSummaryCacheSubscription);
        followerSummaryCacheSubscription = AndroidObservable.bindFragment(this, followerSummaryCache.get(currentUserId.toUserBaseKey()))
                .subscribe(new FollowerSummaryObserver());
    }

    @Override public void onResume()
    {
        super.onResume();
        mainTimelineAdapter.setProfileClickListener(this::display);
        mainTimelineAdapter.setOnLoadFinishedListener(
                new MainTimelineAdapter.OnLoadFinishedListener()
                {
                    @Override public void onLoadFinished()
                    {
                        TimelineFragment.this.onLoadFinished();
                    }

                    @Override public void onBeginRefresh(TabType tabType)
                    {
                        fetchPortfolioList();
                        fetchUserProfile();
                    }
                });
        mainTimelineAdapter.getTimelineLoader().loadNext();

        timelineListView.setOnRefreshListener(mainTimelineAdapter);
        timelineListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        timelineListView.setOnLastItemVisibleListener(new TimelineLastItemVisibleListener());
        timelineListView.setOnItemClickListener(this::onMainItemClick);

        if (userProfileView != null && displayingProfileHeaderLayoutId != 0)
        {
            userProfileView.setDisplayedChildByLayoutId(displayingProfileHeaderLayoutId);
        }

        if (cancelRefreshingOnResume)
        {
            timelineListView.onRefreshComplete();
            cancelRefreshingOnResume = false;
        }
        fetchUserProfile();
        fetchMessageThreadHeader();

        dashboardTabHost.get().setOnTranslate((x, y) -> btnContainer.setTranslationY(y));
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
        timelineListView.setOnRefreshListener((MainTimelineAdapter) null);
        timelineListView.setOnScrollListener(null);
        timelineListView.setOnLastItemVisibleListener(null);
        timelineListView.setOnItemClickListener(null);
        super.onPause();
    }

    @Override public void onStop()
    {
        detachFreeFollowMiddleCallback();
        unsubscribe(messageThreadHeaderFetchSubscription);
        messageThreadHeaderFetchSubscription = null;
        detachFollowDialogCombo();
        detachChoiceFollowAssistant();

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

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    private void detachFollowDialogCombo()
    {
        FollowDialogCombo followDialogComboCopy = followDialogCombo;
        if (followDialogComboCopy != null)
        {
            followDialogComboCopy.followDialogView.setFollowRequestedListener(null);
        }
        followDialogCombo = null;
    }

    private void detachChoiceFollowAssistant()
    {
        ChoiceFollowUserAssistantWithDialog copy = choiceFollowUserAssistantWithDialog;
        if (copy != null)
        {
            copy.onDestroy();
        }
        choiceFollowUserAssistantWithDialog = null;
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
                        }
                    });
        }
    }

    protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.shownProfile = userProfileDTO;
        mainTimelineAdapter.setUserProfileDTO(userProfileDTO);
        if (andDisplay)
        {
            updateView();
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
        unsubscribe(userProfileCacheSubscription);
        userProfileCacheSubscription = AndroidObservable.bindFragment(this, userProfileCache.get().get(shownUserBaseKey))
                .subscribe(new TimelineFragmentUserProfileCacheObserver());
    }

    /** item of Portfolio tab is clicked */
    private void onMainItemClick(AdapterView<?> adapterView, View view, int i, long l)
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
        timelineListView.onRefreshComplete();
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
        mFollowButton.setOnClickListener(v -> handleFollowRequested(shownProfile));
        mSendMsgButton.setVisibility(View.VISIBLE);
        mSendMsgButton.setOnClickListener(v -> {
            if (!mIsHero && (mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER
                    || mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG))
            {
                detachFollowDialogCombo();
                followDialogCombo = heroAlertDialogUtilLazy.get().showFollowDialog(getActivity(), shownProfile,
                        UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG,
                        new TimelineFollowForMessageRequestedListener());
            }
            else
            {
                pushPrivateMessageFragment();
            }
        });
    }

    protected void handleFollowRequested(@NonNull final UserBaseDTO heroDTO)
    {
        detachChoiceFollowAssistant();
        choiceFollowUserAssistantWithDialog = new ChoiceFollowUserAssistantWithDialog(
                getActivity(),
                heroDTO.getBaseKey(),
                createPremiumUserFollowedListener(),
                getApplicablePortfolioId());
        choiceFollowUserAssistantWithDialog.setHeroBaseInfo(heroDTO);
        choiceFollowUserAssistantWithDialog.launchChoice();
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

    protected void freeFollow(@NonNull UserBaseKey heroId, @Nullable Callback<UserProfileDTO> followCallback)
    {
        heroAlertDialogUtilLazy.get().showProgressDialog(getActivity(), getString(R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleCallback =
                userServiceWrapperLazy.get().freeFollow(heroId, followCallback);
    }

    public class FreeUserFollowedCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            heroAlertDialogUtilLazy.get().dismissProgressDialog();
            updateBottomButton();
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Profile));
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            heroAlertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    public class FreeUserFollowedForMessageCallback extends FreeUserFollowedCallback
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            super.success(userProfileDTO, response);
            pushPrivateMessageFragment();
        }
    }

    public class TimelineFollowForMessageRequestedListener implements OnFollowRequestedListener
    {
        @Override public void freeFollowRequested(@NonNull UserBaseKey heroId)
        {
            freeFollow(heroId, new FreeUserFollowedForMessageCallback());
        }

        @Override public void premiumFollowRequested(@NonNull UserBaseKey heroId)
        {
            premiumFollowUser(heroId);
        }
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

    private class TimelineLastItemVisibleListener implements PullToRefreshBase.OnLastItemVisibleListener
    {
        @Override public void onLastItemVisible()
        {
            mainTimelineAdapter.getTimelineLoader().loadPrevious();
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    //<editor-fold desc="BasePurchaseManagerFragment stuffs">
    @Override protected THPurchaseReporter.OnPurchaseReportedListener createPurchaseReportedListener()
    {
        return new TimelinePurchaseReportedListener();
    }

    @Override protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new TimelinePremiumUserFollowedListener();
    }

    protected class TimelinePremiumUserFollowedListener implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(
                @NonNull UserBaseKey userFollowed,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            if (!mIsOtherProfile)
            {
                linkWith(currentUserProfileDTO, true);
            }
            updateBottomButton();
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Profile));
        }

        @Override public void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error)
        {
            // Nothing for now
        }
    }

    protected class TimelinePurchaseReportedListener implements THPurchaseReporter.OnPurchaseReportedListener
    {
        @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO currentUserProfileDTO)
        {
            if (!mIsOtherProfile)
            {
                linkWith(currentUserProfileDTO, true);
            }
            updateBottomButton();
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Profile));
        }

        @Override public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
        {
            // Nothing for now
        }
    }
    //</editor-fold>

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
