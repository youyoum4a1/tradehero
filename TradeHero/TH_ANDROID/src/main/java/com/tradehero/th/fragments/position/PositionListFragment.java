package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
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
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Routable("user/:userId/portfolio/:portfolioId")
public class PositionListFragment
        extends BasePurchaseManagerFragment
        implements PortfolioHeaderView.OnFollowRequestedListener,
        PortfolioHeaderView.OnTimelineRequestedListener,
        WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = PositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = PositionListFragment.class.getName() + ".userBaseKey";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";

    @Inject CurrentUserId currentUserId;
    @Inject GetPositionsDTOKeyFactory getPositionsDTOKeyFactory;
    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject Analytics analytics;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;
    @Inject UserProfileCache userProfileCache;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject BroadcastUtils broadcastUtils;

    //@InjectView(R.id.position_list) protected ListView positionsListView;
    @InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.pull_to_refresh_position_list) PullToRefreshListView pullToRefreshListView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.error) View errorView;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    private PortfolioHeaderView portfolioHeaderView;
    @NotNull protected GetPositionsDTOKey getPositionsDTOKey;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    protected GetPositionsDTO getPositionsDTO;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    @Nullable protected PositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;

    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> refreshGetPositionsDTOListener;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Nullable protected DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchListener;
    @Inject THRouter thRouter;

    //<editor-fold desc="Arguments Handling">
    public static void putGetPositionsDTOKey(@NotNull Bundle args, @NotNull GetPositionsDTOKey getPositionsDTOKey)
    {
        args.putBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE, getPositionsDTOKey.getArgs());
    }

    private static GetPositionsDTOKey getGetPositionsDTOKey(@NotNull GetPositionsDTOKeyFactory getPositionsDTOKeyFactory, @NotNull Bundle args)
    {
        return getPositionsDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE));
    }

    public static void putShownUser(@NotNull Bundle args, @NotNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    @NotNull private static UserBaseKey getUserBaseKey(@NotNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE));
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
        if (args.containsKey(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE))
        {
            getPositionsDTOKey = getGetPositionsDTOKey(getPositionsDTOKeyFactory, args);
        }
        else
        {
            getPositionsDTOKey = new OwnedPortfolioId(injectedUserBaseKey.key, injectedPortfolioId.key);
        }

        fetchGetPositionsDTOListener = createGetPositionsCacheListener();
        refreshGetPositionsDTOListener = createGetPositionsRefreshCacheListener();
        userProfileCacheListener = createProfileCacheListener();
        portfolioFetchListener = createPortfolioCacheListener();
    }

    @NotNull @Override protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new AbstractPositionListPremiumUserFollowedListener();
    }

    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            firstPositionVisible = savedInstanceState.getInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        }

        View view = inflater.inflate(R.layout.fragment_positions_list, container, false);

        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override protected void initViews(@Nullable View view)
    {
        if (view != null)
        {
            if (positionItemAdapter == null)
            {
                createPositionItemAdapter();
            }

            //positionsListView.setAdapter(positionItemAdapter);
            pullToRefreshListView.setAdapter(positionItemAdapter);
            initPullToRefreshListView(view);

            // portfolio header
            headerStub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
            int headerLayoutId = headerFactory.get().layoutIdFor(getPositionsDTOKey);
            headerStub.setLayoutResource(headerLayoutId);
            portfolioHeaderView = (PortfolioHeaderView) headerStub.inflate();
            pullToRefreshListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());

        }
        showLoadingView(true);
    }

    protected boolean checkLoadingSuccess()
    {
        return (userProfileDTO != null) && (getPositionsDTO != null);
    }

    protected void showResultIfNecessary()
    {
        boolean loaded = checkLoadingSuccess();
        Timber.d("checkLoadingSuccess %b", loaded);
        showLoadingView(!loaded);
        if (loaded && pullToRefreshListView != null)
        {
            pullToRefreshListView.onRefreshComplete();
        }
    }

    protected void showLoadingView(boolean shown)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setVisibility(shown ? View.GONE : View.VISIBLE);
        }
        if (errorView != null)
        {
            errorView.setVisibility(View.GONE);
        }
        if (portfolioHeaderView != null && portfolioHeaderView instanceof View)
        {
            ((View) portfolioHeaderView).setVisibility(shown ? View.GONE : View.VISIBLE);
        }
    }

    protected void showErrorView()
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setVisibility(View.GONE);
        }
        if (errorView != null)
        {
            errorView.setVisibility(View.VISIBLE);
        }
        if (portfolioHeaderView != null && portfolioHeaderView instanceof View)
        {
            ((View) portfolioHeaderView).setVisibility(View.GONE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.onRefreshComplete();
        }
    }

    private void initPullToRefreshListView(View view)
    {
        //TODO make it better
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                refreshSimplePage();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
            }
        });
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                handlePositionItemClicked(adapterView, view, i, l);
            }
        });
    }

    protected void createPositionItemAdapter()
    {
        positionItemAdapter = new PositionItemAdapter(
                getActivity(),
                getLayoutResIds());
    }

    @NotNull protected Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_IN_PERIOD, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED_IN_PERIOD, R.layout.position_top_view);
        return layouts;
    }

    protected void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            pushSecurityFragment();
        }
        else if (view instanceof PositionLockedView)
        {
            popFollowUser(shownUser);
        }
        else
        {
            Bundle args = new Bundle();
            // By default tries
            TradeListFragment.putPositionDTOKey(args, ((PositionDTO) parent.getItemAtPosition(position)).getPositionDTOKey());
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
            TrendingFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        navigator.get().pushFragment(TrendingFragment.class, args);
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.position_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle();
    }

    @Override public void onResume()
    {
        super.onResume();

        linkWith(getPositionsDTOKey, true);
        if (portfolioHeaderView != null)
        {
            portfolioHeaderView.setFollowRequestedListener(this);
            portfolioHeaderView.setTimelineRequestedListener(this);
        }
    }

    @Override public void onPause()
    {
        if (portfolioHeaderView != null)
        {
            portfolioHeaderView.setFollowRequestedListener(null);
            portfolioHeaderView.setTimelineRequestedListener(null);
        }

        if (pullToRefreshListView.getRefreshableView() != null)
        {
            firstPositionVisible = pullToRefreshListView.getRefreshableView().getFirstVisiblePosition();
        }
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        detachGetPositionsTask();
        detachUserProfileCache();
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
    }

    @Override public void onStop()
    {
        detachGetPositionsTask();
        detachRefreshGetPositionsTask();
        detachUserProfileCache();
        detachPortfolioCache();

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setOnScrollListener(null);
            pullToRefreshListView.setOnTouchListener(null);
        }
        positionItemAdapter = null;

        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setOnRefreshListener((PullToRefreshBase.OnRefreshListener<ListView>) null);
        }

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        fetchGetPositionsDTOListener = null;
        refreshGetPositionsDTOListener = null;
        userProfileCacheListener = null;
        portfolioFetchListener = null;
        super.onDestroy();
    }

    /**
     * start
     * @param positionsDTOKey
     * @param andDisplay
     */
    public void linkWith(@NotNull GetPositionsDTOKey positionsDTOKey, boolean andDisplay)
    {
        this.getPositionsDTOKey = positionsDTOKey;
        userProfileDTO = null;

        fetchUserProfile();
        fetchSimplePage();
        fetchPortfolio();
        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(shownUser, userProfileCacheListener);
        userProfileCache.getOrFetchAsync(shownUser);
    }

    public boolean isShownOwnedPortfolioIdForOtherPeople(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        return ownedPortfolioId != null && ownedPortfolioId.portfolioId <= 0;
    }

    protected void fetchSimplePage()
    {
        fetchSimplePage(false);
    }

    protected void fetchSimplePage(boolean force)
    {
        if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
        {
            detachGetPositionsTask();
            getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
            getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, force);
        }
    }

    protected void refreshSimplePage()
    {
        detachRefreshGetPositionsTask();
        getPositionsCache.get().register(getPositionsDTOKey, refreshGetPositionsDTOListener);
        getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, true);
    }

    protected void fetchPortfolio()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            if (currentUserId.toUserBaseKey().equals(((OwnedPortfolioId) getPositionsDTOKey).getUserBaseKey()))
            {
                PortfolioCompactDTO cached = portfolioCompactCache.get(((OwnedPortfolioId) getPositionsDTOKey).getPortfolioIdKey());
                if (cached == null)
                {
                    detachPortfolioCache();
                    portfolioCache.register((OwnedPortfolioId) getPositionsDTOKey, portfolioFetchListener);
                    portfolioCache.get((OwnedPortfolioId) getPositionsDTOKey);
                }
                else
                {
                    linkWith(cached);
                }
            }
            // We do not need to fetch for other players
        }
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected void detachGetPositionsTask()
    {
        getPositionsCache.get().unregister(fetchGetPositionsDTOListener);
    }

    protected void detachRefreshGetPositionsTask()
    {
        getPositionsCache.get().unregister(refreshGetPositionsDTOListener);
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void detachPortfolioCache()
    {
        portfolioCache.unregister(portfolioFetchListener);
    }

    protected void rePurposeAdapter()
    {
        if (this.getPositionsDTO != null)
        {
            createPositionItemAdapter();
            positionItemAdapter.addAll(getPositionsDTO.positions);
            positionItemAdapter.notifyDataSetChanged();
            pullToRefreshListView.setAdapter(positionItemAdapter);
            //if (positionsListView != null)
            //{
            //    positionsListView.setAdapter(positionItemAdapter);
            //}
        }
    }

    public void linkWith(GetPositionsDTO getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        //reAttemptFetchPortfolio();
        rePurposeAdapter();

        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            displayHeaderView();
        }
    }

    public void display()
    {
        displayHeaderView();
        displayActionBarTitle();
    }

    private void displayHeaderView()
    {
        if (portfolioHeaderView != null && userProfileDTO != null)
        {
            Timber.d("displayHeaderView %s", portfolioHeaderView.getClass().getSimpleName());
            portfolioHeaderView.linkWith(userProfileDTO);
        }
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            portfolioHeaderView.linkWith(portfolioCache.get((OwnedPortfolioId) getPositionsDTOKey));
        }
    }

    public void displayActionBarTitle()
    {
        String title = null;
        String subtitle = null;
        if (portfolioCompactDTO != null)
        {
            title = portfolioCompactDTO.title;
        }

        if (getPositionsDTO != null && getPositionsDTO.positions != null)
        {
            subtitle = String.format(getResources().getString(R.string.position_list_action_bar_header),
                    getPositionsDTO.positions.size());
        }
        else
        {
            subtitle = null;
        }

        if (title == null && subtitle != null)
        {
            title = subtitle;
            subtitle = null;
        }
        else if (title == null)
        {
            title = getString(R.string.position_list_action_bar_header_unknown);
        }

        setActionBarTitle(title);
        setActionBarSubtitle(subtitle);
    }

    //<editor-fold desc="PortfolioHeaderView.OnFollowRequestedListener">
    @Override public void onFollowRequested(final UserBaseKey userBaseKey)
    {
        premiumFollowUser(userBaseKey);
        //popFollowUser(userBaseKey);
    }

    @Override public void onUserFollowed(UserBaseKey userBaseKey)
    {
        //
        pullToRefreshListView.setRefreshing();
        refreshSimplePage();
    }

    //</editor-fold>

    protected void popFollowUser(final UserBaseKey userBaseKey)
    {
        //TODO need to improve
        if (portfolioHeaderView instanceof OtherUserPortfolioHeaderView)
        {
            ((OtherUserPortfolioHeaderView) portfolioHeaderView).showFollowDialog();
        }
        //else do nothing
    }

    //<editor-fold desc="PortfolioHeaderView.OnTimelineRequestedListener">
    @Override public void onTimelineRequested(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        thRouter.save(args, userBaseKey);
        if (currentUserId.toUserBaseKey().equals(userBaseKey))
        {
            navigator.get().pushFragment(MeTimelineFragment.class, args);
        }
        else
        {
            navigator.get().pushFragment(PushableTimelineFragment.class, args);
        }
    }
    //</editor-fold>

    @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    protected class GetPositionsListener
            implements DTOCacheNew.HurriedListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value, true);
            showResultIfNecessary();
        }

        @Override public void onDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value, true);
            showResultIfNecessary();
        }

        @Override public void onErrorThrown(
                @NotNull GetPositionsDTOKey key,
                @NotNull Throwable error)
        {
            //displayProgress(false);
            THToast.show(getString(R.string.error_fetch_position_list_info));
            showErrorView();
            Timber.d(error, "Error fetching the positionList info %s", key);
        }
    }

    @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsRefreshCacheListener()
    {
        return new RefreshPositionsListener();
    }

    protected class RefreshPositionsListener extends GetPositionsListener
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            // Do nothing
        }

        @Override public void onErrorThrown(
                @NotNull GetPositionsDTOKey key,
                @NotNull Throwable error)
        {
            //super.onErrorThrown(key, error);
            boolean loaded = checkLoadingSuccess();
            if (!loaded)
            {
                showErrorView();
            }
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }

    @NotNull protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createProfileCacheListener()
    {
        return new AbstractPositionListProfileCacheListener();
    }

    protected class AbstractPositionListPremiumUserFollowedListener
            implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(@NotNull UserBaseKey userFollowed, @NotNull UserProfileDTO currentUserProfileDTO)
        {
            displayHeaderView();
            fetchSimplePage(true);
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.PositionList));
        }

        @Override public void onUserFollowFailed(@NotNull UserBaseKey userFollowed, @NotNull Throwable error)
        {
            // do nothing for now
        }
    }

    protected class AbstractPositionListProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(
                @NotNull UserBaseKey key,
                @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
            showResultIfNecessary();
            showPrettyReviewAndInvite();
        }

        @Override public void onErrorThrown(
                @NotNull UserBaseKey key,
                @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_user_profile);
            //TODO not just toast
            showErrorView();
        }
    }

    private void showPrettyReviewAndInvite()
    {
        if (shownUser != null)
        {
            if (shownUser.getUserId().intValue() != currentUserId.get().intValue())
            {
                return;
            }
        }
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            PortfolioDTO cachedPortfolio = portfolioCache.get((OwnedPortfolioId) getPositionsDTOKey);
            if (cachedPortfolio != null)
            {
                Double profit = cachedPortfolio.roiSinceInception;
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
        }
    }

    protected DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioCacheListener()
    {
        return new PortfolioCacheListener();
    }

    protected class PortfolioCacheListener implements DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO>
    {
        @Override public void onDTOReceived(@NotNull OwnedPortfolioId key, @NotNull PortfolioDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull OwnedPortfolioId key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_portfolio_info);
        }
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        portfolioHeaderView.linkWith(portfolioCompactDTO);
        displayActionBarTitle();
    }
}
