package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.discussion.TimelineDiscussionFragment;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerInfoFetcher;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.message.MessageThreadHeaderCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TimelineFragment extends BasePurchaseManagerFragment
        implements PortfolioRequestListener, UserProfileDetailView.OnHeroClickListener
{
    public static final String BUNDLE_KEY_SHOW_USER_ID = TimelineFragment.class.getName() + ".showUserId";
    private View loadingView;
    private PullToRefreshBase.OnLastItemVisibleListener lastItemVisibleListener;

    public static enum TabType
    {
        TIMELINE, PORTFOLIO_LIST, STATS
    }

    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<CurrentUserId> currentUserIdLazy;
    @Inject MessageThreadHeaderCache messageThreadHeaderCache;
    @Inject DiscussionKeyFactory discussionKeyFactory;

    @InjectView(R.id.timeline_list_view) TimelineListView timelineListView;
    @InjectView(R.id.timeline_screen) BetterViewAnimator timelineScreen;
    @InjectView(R.id.follow_button) Button mFollowButton;
    @InjectView(R.id.message_button) Button mSendMsgButton;

    private UserProfileView userProfileView;
    private MainTimelineAdapter mainTimelineAdapter;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    protected ActionBar actionBar;
    protected UserBaseKey shownUserBaseKey;
    protected UserProfileDTO shownProfile;
    protected OwnedPortfolioIdList portfolioIdList;
    protected UserProfileRetrievedMilestone userProfileRetrievedMilestone;
    protected PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;
    protected DTOCache.GetOrFetchTask<UserBaseKey, MessageHeaderDTO> messageThreadHeaderFetchTask;
    protected MessageHeaderDTO messageThreadHeaderDTO;

    private boolean cancelRefreshingOnResume;
    protected boolean mIsOtherProfile = false;
    private int displayingProfileHeaderLayoutId;
    //TODO need move to pushableTimelineFragment
    private int mFollowType;//0 not follow, 1 free follow, 2 premium follow
    private boolean mIsHero = false;//whether the showUser follow the user
    public TabType currentTab = TabType.TIMELINE;

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new TimelinePremiumUserFollowedListener();
    }

    protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedForMessageListener()
    {
        return new TimelinePremiumUserForMessageFollowedListener();
    }

    protected OnFollowRequestedListener createFollowRequestedListener()
    {
        return new TimelineFollowRequestedListener();
    }

    protected OnFollowRequestedListener createFollowForMessageRequestedListener()
    {
        return new TimelineFollowForMessageRequestedListener();
    }

    protected Callback<UserProfileDTO> createFreeFollowCallback()
    {
        return new FreeFollowCallback();
    }

    protected Callback<UserProfileDTO> createFreeFollowForMessageCallback()
    {
        return new FreeFollowForMessageCallback();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_screen, container, false);
        userProfileView = (UserProfileView) inflater.inflate(R.layout.user_profile_view, null);
        userProfileView.setHeroClickListener(this);

        loadingView = new ProgressBar(getActivity());

        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override public void onHeroClick()
    {
        pushHeroFragment();
    }

    @Override public void onFollowerClick()
    {
        pushFollowerFragment();
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(
                    BasePurchaseManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                    applicablePortfolio.getArgs());
        }
        getNavigator().pushFragment(HeroManagerFragment.class, bundle);
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
            bundle.putBundle(
                    BasePurchaseManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                    applicablePortfolio.getArgs());
        }
        getNavigator().pushFragment(FollowerManagerFragment.class, bundle);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(
                (isTabBarVisible() ? 0 : ActionBar.DISPLAY_HOME_AS_UP)
                        | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setHomeButtonEnabled(true);
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        //switch (item.getItemId())
        //{
            //case R.id.menu_settings:
            //    DashboardNavigator navigator =
            //            ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
            //    navigator.pushFragment(SettingsFragment.class);
            //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override protected void initViews(View view)
    {
        if (userProfileView != null)
        {
            //TODO now only one view, userProfileView useless, need cancel, alex
            userProfileView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    //        userProfileView.getChildAt(userProfileView.getDisplayedChild())
                    //                .setVisibility(View.GONE);
                    //        userProfileView.showNext();
                    //        userProfileView.getChildAt(userProfileView.getDisplayedChild())
                    //                .setVisibility(View.VISIBLE);
                }
            });
            userProfileView.setPortfolioRequestListener(this);
            timelineListView.getRefreshableView().addHeaderView(userProfileView);
        }

        if (loadingView != null)
        {
            timelineListView.addFooterView(loadingView);
        }

        displayablePortfolioFetchAssistant = new DisplayablePortfolioFetchAssistant();
        displayablePortfolioFetchAssistant.setFetchedListener(
                new DisplayablePortfolioFetchAssistant.OnFetchedListener()
                {
                    @Override public void onFetched()
                    {
                        displayPortfolios();
                    }
                });

        lastItemVisibleListener = new TimelineLastItemVisibleListener();
    }

    private class FollowerSummaryListener implements DTOCache.Listener<UserBaseKey, FollowerSummaryDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, FollowerSummaryDTO value, boolean fromCache)
        {
            updateHeroType(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(error.getMessage());
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

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        UserBaseKey newUserBaseKey =
                new UserBaseKey(getArguments().getInt(BUNDLE_KEY_SHOW_USER_ID));
        //create adapter and so on
        linkWith(newUserBaseKey, true);

        getActivity().getSupportLoaderManager().initLoader(
                mainTimelineAdapter.getTimelineLoaderId(), null,
                mainTimelineAdapter.getLoaderTimelineCallback());
    }

    @Override public void onResume()
    {
        super.onResume();
        if (userProfileView != null && displayingProfileHeaderLayoutId != 0)
        {
            userProfileView.setDisplayedChildByLayoutId(displayingProfileHeaderLayoutId);
        }

        if (cancelRefreshingOnResume)
        {
            timelineListView.onRefreshComplete();
            cancelRefreshingOnResume = false;
        }
        displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());

        fetchMessageThreadHeader();

        displayTab();
    }

    @Override public void onPause()
    {
        if (userProfileView != null)
        {
            displayingProfileHeaderLayoutId = userProfileView.getDisplayedChildLayoutId();
        }
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        this.actionBar = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        detachTimelineAdapter();
        detachTimelineListView();
        detachFreeFollowMiddleCallback();
        detachMessageThreadHeaderFetchTask();

        displayablePortfolioFetchAssistant.setFetchedListener(null);
        displayablePortfolioFetchAssistant = null;

        if (userProfileRetrievedMilestone != null)
        {
            userProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        userProfileRetrievedMilestone = null;

        if (userProfileView != null)
        {
            userProfileView.setPortfolioRequestListener(null);
        }
        this.userProfileView = null;
        this.loadingView = null;

        super.onDestroyView();
    }

    protected void detachTimelineAdapter()
    {
        if (mainTimelineAdapter != null)
        {
            mainTimelineAdapter.setProfileClickListener(null);
            mainTimelineAdapter.setOnLoadFinishedListener(null);
        }
        mainTimelineAdapter = null;
    }

    protected void detachTimelineListView()
    {
        if (timelineListView != null)
        {
            timelineListView.setOnRefreshListener((MainTimelineAdapter) null);
            timelineListView.setOnScrollListener(null);
            timelineListView.setOnLastItemVisibleListener(null);
        }
        timelineListView = null;
        lastItemVisibleListener = null;
    }

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    private void detachMessageThreadHeaderFetchTask()
    {
        if (messageThreadHeaderFetchTask != null)
        {
            messageThreadHeaderFetchTask.setListener(null);
        }
        messageThreadHeaderFetchTask = null;
    }

    protected void fetchMessageThreadHeader()
    {
        detachMessageThreadHeaderFetchTask();
        messageThreadHeaderFetchTask = messageThreadHeaderCache.getOrFetch(shownUserBaseKey, createMessageThreadHeaderCacheListener());
        messageThreadHeaderFetchTask.execute();
    }

    //<editor-fold desc="Display methods">
    protected void linkWith(UserBaseKey userBaseKey, final boolean andDisplay)
    {
        this.shownUserBaseKey = userBaseKey;

        prepareTimelineAdapter();

        if (userBaseKey != null)
        {
            createUserProfileRetrievedMilestone();
            userProfileRetrievedMilestone.setOnCompleteListener(
                    userProfileRetrievedMilestoneListener);
            userProfileRetrievedMilestone.launch();

            createPortfolioCompactListRetrievedMilestone();
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(
                    portfolioCompactListRetrievedMilestoneListener);
            portfolioCompactListRetrievedMilestone.launch();
            FollowerManagerInfoFetcher infoFetcher =
                    new FollowerManagerInfoFetcher(new FollowerSummaryListener());
            infoFetcher.fetch(currentUserIdLazy.get().toUserBaseKey());
        }

        if (andDisplay)
        {
            displayTab();
        }
    }

    private AdapterView.OnItemClickListener createTimelineOnClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object item = parent.getItemAtPosition(position);

                if (item instanceof TimelineItemDTOEnhanced)
                {
                    pushDiscussion(((TimelineItemDTOEnhanced) item).getDiscussionKey());
                }
            }
        };
    }

    private void pushDiscussion(TimelineItemDTOKey timelineItemDTOKey)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimelineDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY,
                timelineItemDTOKey.getArgs());
        getNavigator().pushFragment(TimelineDiscussionFragment.class, bundle);
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

    private void linkWith(OwnedPortfolioIdList ownedPortfolioIdList, boolean andDisplay)
    {
        this.portfolioIdList = ownedPortfolioIdList;
        portfolioCache.get().autoFetch(ownedPortfolioIdList, (OwnedPortfolioId) null);

        if (andDisplay)
        {
            // Nothing to do
        }
    }

    protected void linkWith(TabType tabType, boolean andDisplay)
    {
        currentTab = tabType;
        if (andDisplay)
        {
            displayTab();
        }
    }

    protected void linkWithMessageThread(MessageHeaderDTO messageHeaderDTO, boolean andDisplay)
    {
        this.messageThreadHeaderDTO = messageHeaderDTO;
        if (andDisplay)
        {
        }
    }

    protected void updateView()
    {
        if (timelineScreen != null)
        {
            timelineScreen.setDisplayedChildByLayoutId(R.id.timeline_list_view_container);
        }
        if (userProfileView != null)
        {
            userProfileView.display(shownProfile);
        }
        if (this.actionBar != null)
        {
            this.actionBar.setTitle(
                    userBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
        }

        displayActionBarTitle();
    }

    public void displayTab()
    {
        mainTimelineAdapter.setCurrentTabType(currentTab);
    }
    //</editor-fold>

    protected void displayActionBarTitle()
    {
        if (actionBar != null)
        {
            if (shownProfile != null)
            {
                actionBar.setTitle(userBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
            }
            else
            {
                actionBar.setTitle(R.string.loading_loading);
            }
        }
    }

    //<editor-fold desc="Init milestones">
    protected void createUserProfileRetrievedMilestone()
    {
        if (userProfileRetrievedMilestone != null)
        {
            userProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        userProfileRetrievedMilestone = new UserProfileRetrievedMilestone(shownUserBaseKey);
    }

    protected void createPortfolioCompactListRetrievedMilestone()
    {
        if (portfolioCompactListRetrievedMilestone != null)
        {
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(null);
        }
        portfolioCompactListRetrievedMilestone =
                new PortfolioCompactListRetrievedMilestone(shownUserBaseKey);
    }
    //</editor-fold>

    //<editor-fold desc="Initial methods">
    private MainTimelineAdapter createTimelineAdapter()
    {
        return new MainTimelineAdapter(getActivity(), getActivity().getLayoutInflater(),
                shownUserBaseKey, R.layout.timeline_item_view, R.layout.portfolio_list_item_2_0,
                R.layout.user_profile_stat_view);
        //shownUserBaseKey.key, R.layout.timeline_item_view);
        // TODO set the layouts
    }

    private void prepareTimelineAdapter()
    {
        mainTimelineAdapter = createTimelineAdapter();
        mainTimelineAdapter.setProfileClickListener(createTimelineProfileClickListener());
        mainTimelineAdapter.setOnLoadFinishedListener(
                new MainTimelineAdapter.OnLoadFinishedListener()
                {
                    @Override public void onLoadFinished()
                    {
                        TimelineFragment.this.onLoadFinished();
                    }
                });
        timelineListView.setOnRefreshListener(mainTimelineAdapter);
        timelineListView.setOnScrollListener(mainTimelineAdapter);
        timelineListView.setOnLastItemVisibleListener(lastItemVisibleListener);
        //timelineListView.setRefreshing();
        timelineListView.setAdapter(mainTimelineAdapter);
        timelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                onMainItemClick(adapterView, view, i, l);
            }
        });
    }
    //</editor-fold>
    /**item of Portfolio tab is clicked*/
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
                    pushPositionListFragment(displayablePortfolioDTO.ownedPortfolioId);
                }
            }
        }
        else
        {
            Timber.d("TimelineFragment, unhandled view %s", view);
        }
    }

    //<editor-fold desc="PortfolioRequestListener">
    @Override public void onDefaultPortfolioRequested()
    {
        if (portfolioIdList == null || portfolioIdList.size() < 1 || portfolioIdList.get(0) == null)
        {
            // HACK, instead we should test for Default title on PortfolioDTO
            THToast.show("Not enough data, try again");
        }
        else if (shownUserBaseKey == null)
        {
            Timber.e(new NullPointerException("shownUserBaseKey is null"), "");
        }
        else if (portfolioCompactListCache == null)
        {
            Timber.e(new NullPointerException("portfolioCompactListCache is null"), "");
        }
        else if (portfolioCompactListCache.get() == null)
        {
            Timber.e(new NullPointerException("portfolioCompactListCache.get() is null"), "");
        }
        else
        {
            pushPositionListFragment(
                    portfolioCompactListCache.get().getDefaultPortfolio(shownUserBaseKey));
        }
    }

    @Override public void onPortfolioRequested(OwnedPortfolioId ownedPortfolioId)
    {
        pushPositionListFragment(ownedPortfolioId);
    }
    //</editor-fold>

    /**
     *
     * @param ownedPortfolioId
     */
    private void pushPositionListFragment(OwnedPortfolioId ownedPortfolioId)
    {
        Bundle args = new Bundle();
        args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE,
                ownedPortfolioId.getArgs());
        DashboardNavigator navigator =
                ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
        navigator.pushFragment(PositionListFragment.class, args);
    }

    /**
     * Go to watchlist
     * @param ownedPortfolioId
     */
    private void pushWatchlistPositionFragment(OwnedPortfolioId ownedPortfolioId)
    {
        Bundle args = new Bundle();
        WatchlistPositionFragment.putOwnedPortfolioId(args, ownedPortfolioId);
        DashboardNavigator navigator =
                ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
        navigator.pushFragment(WatchlistPositionFragment.class, args);
    }

    private void onLoadFinished()
    {
        if (timelineListView != null)
        {
            timelineListView.onRefreshComplete();
            loadingView.setVisibility(View.GONE);
            cancelRefreshingOnResume = true;
        }
    }

    //<editor-fold desc="Milestone retrieved listeners">
    private Milestone.OnCompleteListener userProfileRetrievedMilestoneListener =
            new Milestone.OnCompleteListener()
            {
                @Override public void onComplete(Milestone milestone)
                {
                    UserProfileDTO cachedUserProfile = userProfileCache.get().get(shownUserBaseKey);
                    if (cachedUserProfile != null)
                    {
                        linkWith(cachedUserProfile, true);
                    }
                }

                @Override public void onFailed(Milestone milestone, Throwable throwable)
                {
                    THToast.show(getString(R.string.error_fetch_user_profile));
                }
            };

    private Milestone.OnCompleteListener portfolioCompactListRetrievedMilestoneListener =
            new Milestone.OnCompleteListener()
            {
                @Override public void onComplete(Milestone milestone)
                {
                    OwnedPortfolioIdList cachedOwnedPortfolioIdList =
                            portfolioCompactListCache.get().get(shownUserBaseKey);
                    if (cachedOwnedPortfolioIdList != null)
                    {
                        linkWith(cachedOwnedPortfolioIdList, true);
                    }
                }

                @Override public void onFailed(Milestone milestone, Throwable throwable)
                {
                    // We do not need to inform the player here
                    Timber.e("Error fetching the list of portfolio for user: %d",
                            shownUserBaseKey.key, throwable);
                }
            };
    //</editor-fold>

    protected List<UserBaseKey> getUserBaseKeys()
    {
        List<UserBaseKey> list = new ArrayList<>();
        list.add(shownUserBaseKey);
        return list;
    }

    public void displayPortfolios()
    {
        this.mainTimelineAdapter.setDisplayablePortfolioItems(getAllPortfolios());
    }

    private List<DisplayablePortfolioDTO> getAllPortfolios()
    {
        if (displayablePortfolioFetchAssistant != null)
        {
            return displayablePortfolioFetchAssistant.getDisplayablePortfolios();
        }
        return null;
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
        mFollowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                alertDialogUtilLazy.get().showFollowDialog(getActivity(), shownProfile,
                        mFollowType, createFollowRequestedListener());
            }
        });
        mSendMsgButton.setVisibility(View.VISIBLE);
        mSendMsgButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (!mIsHero && (mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER
                        || mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG))
                {
                    alertDialogUtilLazy.get().showFollowDialog(getActivity(), shownProfile,
                            UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG,
                            createFollowForMessageRequestedListener());
                }
                else
                {
                    pushPrivateMessageFragment();
                }
            }
        });
    }

    protected void pushPrivateMessageFragment()
    {
        if (messageThreadHeaderDTO != null)
        {
            Bundle args = new Bundle();
            ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, shownUserBaseKey);
            ReplyPrivateMessageFragment.putDiscussionKey(args, discussionKeyFactory.create(messageThreadHeaderDTO));
            getNavigator().pushFragment(NewPrivateMessageFragment.class, args);
        }
        else
        {
            Bundle args = new Bundle();
            NewPrivateMessageFragment.putCorrespondentUserBaseKey(args, shownUserBaseKey);
            getNavigator().pushFragment(NewPrivateMessageFragment.class, args);
        }
    }

    /**
     * Null means unsure.
     */
    protected int getFollowType()
    {
        UserProfileDTO userProfileDTO =
                userProfileCache.get().get(currentUserIdLazy.get().toUserBaseKey());
        if (userProfileDTO != null)
        {
            return userProfileDTO.getFollowType(shownUserBaseKey);
        }
        return 0;
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    protected void freeFollow(UserBaseKey heroId, Callback<UserProfileDTO> followCallback)
    {
        alertDialogUtilLazy.get().showProgressDialog(getActivity(), getString(R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleCallback =
                userServiceWrapperLazy.get().freeFollow(heroId, followCallback);
    }

    protected TimelineProfileClickListener createTimelineProfileClickListener()
    {
        return new TimelineProfileClickListener()
        {
            @Override public void onBtnClicked(TabType tabType)
            {
                linkWith(tabType, true);
            }
        };
    }

    public class FreeFollowCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            userProfileCacheLazy.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            alertDialogUtilLazy.get().dismissProgressDialog();
            updateBottomButton();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    public class FreeFollowForMessageCallback extends FreeFollowCallback
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            super.success(userProfileDTO, response);
            pushPrivateMessageFragment();
        }
    }

    public class TimelineFollowRequestedListener implements OnFollowRequestedListener
    {
        @Override public void freeFollowRequested(UserBaseKey heroId)
        {
            freeFollow(heroId, createFreeFollowCallback());
        }

        @Override public void premiumFollowRequested(UserBaseKey heroId)
        {
            premiumFollowUser(heroId);
        }
    }

    public class TimelineFollowForMessageRequestedListener implements OnFollowRequestedListener
    {
        @Override public void freeFollowRequested(UserBaseKey heroId)
        {
            freeFollow(heroId, createFreeFollowForMessageCallback());
        }

        @Override public void premiumFollowRequested(UserBaseKey heroId)
        {
            premiumFollowUser(heroId, createPremiumUserFollowedForMessageListener());
        }
    }

    protected class TimelinePremiumUserFollowedListener extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            if (!mIsOtherProfile)
            {
                linkWith(currentUserProfileDTO, true);
            }
            updateBottomButton();
        }
    }

    protected class TimelinePremiumUserForMessageFollowedListener extends TimelinePremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            pushPrivateMessageFragment();
        }
    }

    protected DTOCache.Listener<UserBaseKey, MessageHeaderDTO> createMessageThreadHeaderCacheListener()
    {
        return new TimelineMessageThreadHeaderCacheListener();
    }

    protected class TimelineMessageThreadHeaderCacheListener implements DTOCache.Listener<UserBaseKey, MessageHeaderDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, MessageHeaderDTO value, boolean fromCache)
        {
            linkWithMessageThread(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            if (!(error instanceof RetrofitError) ||
                    ((RetrofitError) error).getResponse().getStatus() != 404)
            {
                THToast.show(R.string.error_fetch_message_thread_header);
                Timber.e(error, "Error while getting message thread");
            }
            else
            {
                // There is just no existing thread
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
}
