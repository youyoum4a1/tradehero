package com.tradehero.th.fragments.position;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
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
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.broadcast.BroadcastConstants;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.observers.EmptyObserver;
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
    private static final int FLIPPER_INDEX_LOADING = 0;
    private static final int FLIPPER_INDEX_LIST = 1;
    private static final int FLIPPER_INDEX_ERROR = 2;

    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject GetPositionsDTOKeyFactory getPositionsDTOKeyFactory;
    @Inject GetPositionsCacheRx getPositionsCache;
    @Inject PortfolioHeaderFactory headerFactory;
    @Inject Analytics analytics;
    @Inject PortfolioCacheRx portfolioCache;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject BroadcastUtils broadcastUtils;

    //@InjectView(R.id.position_list) protected ListView positionsListView;
    @InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.list_flipper) ViewAnimator listViewFlipper;
    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @InjectView(R.id.position_list) ListView positionListView;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    private PortfolioHeaderView portfolioHeaderView;
    @NonNull protected GetPositionsDTOKey getPositionsDTOKey;
    @Nullable protected Subscription portfolioSubscription;
    protected PortfolioDTO portfolioDTO;
    @Nullable protected Subscription getPositionsSubscription;
    protected GetPositionsDTO getPositionsDTO;
    protected UserBaseKey shownUser;
    @Nullable protected Subscription userProfileSubscription;
    @Nullable protected UserProfileDTO userProfileDTO;

    protected PositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;

    //<editor-fold desc="Arguments Handling">
    public static void putGetPositionsDTOKey(@NonNull Bundle args, @NonNull GetPositionsDTOKey getPositionsDTOKey)
    {
        args.putBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE, getPositionsDTOKey.getArgs());
    }

    private static GetPositionsDTOKey getGetPositionsDTOKey(@NonNull GetPositionsDTOKeyFactory getPositionsDTOKeyFactory, @NonNull Bundle args)
    {
        return getPositionsDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE));
    }

    public static void putShownUser(@NonNull Bundle args, @NonNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    @NonNull private static UserBaseKey getUserBaseKey(@NonNull Bundle args)
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
        swipeToRefreshLayout.setOnRefreshListener(this::refreshSimplePage);
        positionListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());

        // portfolio header
        int headerLayoutId = headerFactory.layoutIdFor(getPositionsDTOKey);
        headerStub.setLayoutResource(headerLayoutId);
        portfolioHeaderView = (PortfolioHeaderView) headerStub.inflate();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.position_list)
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

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.position_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle();
    }

    @Override public void onStart()
    {
        super.onStart();
        portfolioHeaderView.setFollowRequestedListener(this);
        portfolioHeaderView.setTimelineRequestedListener(this);
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

    @Override public void onStop()
    {
        unsubscribe(portfolioSubscription);
        portfolioSubscription = null;
        unsubscribe(userProfileSubscription);
        userProfileSubscription = null;
        unsubscribe(getPositionsSubscription);
        getPositionsSubscription = null;
        portfolioHeaderView.setFollowRequestedListener(null);
        portfolioHeaderView.setTimelineRequestedListener(null);
        super.onStop();
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
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.positionItemAdapter = null;
        super.onDestroy();
    }

    @NonNull @Override protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new AbstractPositionListPremiumUserFollowedListener();
    }

    protected PositionItemAdapter createPositionItemAdapter()
    {
        return new PositionItemAdapter(
                getActivity(),
                getLayoutResIds());
    }

    @NonNull protected Map<Integer, Integer> getLayoutResIds()
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

    protected void fetchUserProfile()
    {
        if (userProfileSubscription == null)
        {
            userProfileSubscription = AndroidObservable.bindFragment(this, userProfileCache.get(shownUser))
                    .subscribe(createProfileCacheObserver());
        }
    }

    @NonNull protected Observer<Pair<UserBaseKey, UserProfileDTO>> createProfileCacheObserver()
    {
        return new AbstractPositionListProfileCacheObserver();
    }

    protected class AbstractPositionListProfileCacheObserver extends EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_user_profile);
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

    public boolean isShownOwnedPortfolioIdForOtherPeople(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        return ownedPortfolioId != null && ownedPortfolioId.portfolioId <= 0;
    }

    protected void fetchPortfolio()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            if (currentUserId.toUserBaseKey().equals(((OwnedPortfolioId) getPositionsDTOKey).getUserBaseKey())
                    && portfolioSubscription == null)
            {
                portfolioSubscription = AndroidObservable.bindFragment(
                        this,
                        portfolioCache.get(((OwnedPortfolioId) getPositionsDTOKey)))
                        .subscribe(createPortfolioCacheObserver());
            }
            // We do not need to fetch for other players
        }
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
    }

    protected Observer<Pair<OwnedPortfolioId, PortfolioDTO>> createPortfolioCacheObserver()
    {
        return new PortfolioCacheObserver();
    }

    protected class PortfolioCacheObserver implements Observer<Pair<OwnedPortfolioId, PortfolioDTO>>
    {
        @Override public void onNext(Pair<OwnedPortfolioId, PortfolioDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_portfolio_info);
        }
    }

    protected void linkWith(@NonNull PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
        portfolioHeaderView.linkWith(portfolioDTO);
        displayActionBarTitle();
        showPrettyReviewAndInvite(portfolioDTO);
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
                broadcastUtils.enqueue(BroadcastConstants.SEND_LOVE_BROADCAST_DATA);
            }
            else if (mShowAskForInviteDialogPreference.isItTime())
            {
                AskForInviteDialogFragment.showInviteDialog(getActivity().getFragmentManager());
            }
        }
    }

    protected void fetchSimplePage()
    {
        if (getPositionsDTOKey.isValid() && getPositionsSubscription == null)
        {
            getPositionsSubscription = AndroidObservable.bindFragment(this, getPositionsCache.get(getPositionsDTOKey))
                    .subscribe(createGetPositionsCacheObserver());
        }
    }

    @NonNull protected Observer<Pair<GetPositionsDTOKey, GetPositionsDTO>> createGetPositionsCacheObserver()
    {
        return new GetPositionsObserver();
    }

    protected class GetPositionsObserver extends EmptyObserver<Pair<GetPositionsDTOKey, GetPositionsDTO>>
    {
        @Override public void onNext(Pair<GetPositionsDTOKey, GetPositionsDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onError(Throwable e)
        {
            if (getPositionsDTO == null)
            {
                listViewFlipper.setDisplayedChild(FLIPPER_INDEX_ERROR);

                THToast.show(getString(R.string.error_fetch_position_list_info));
                Timber.d(e, "Error fetching the positionList info");
            }
        }
    }

    public void linkWith(GetPositionsDTO getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        positionItemAdapter.clear();
        positionItemAdapter.addAll(getPositionsDTO.positions);
        positionItemAdapter.notifyDataSetChanged();
        swipeToRefreshLayout.setRefreshing(false);
        listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);

        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    protected void refreshSimplePage()
    {
        getPositionsCache.invalidate(getPositionsDTOKey);
        getPositionsCache.get(getPositionsDTOKey);
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

    public void displayActionBarTitle()
    {
        String title = null;
        String subtitle = null;
        if (portfolioDTO != null)
        {
            title = portfolioDTO.title;
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
        swipeToRefreshLayout.setRefreshing(true);
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

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }

    protected class AbstractPositionListPremiumUserFollowedListener implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(@NonNull UserBaseKey userFollowed, @NonNull UserProfileDTO currentUserProfileDTO)
        {
            displayHeaderView();
            fetchSimplePage();
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.PositionList));
        }

        @Override public void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error)
        {
            // do nothing for now
        }
    }
}
