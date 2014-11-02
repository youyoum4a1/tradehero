package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.util.Pair;
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
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
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
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
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
    @Inject Lazy<GetPositionsCacheRx> getPositionsCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject Analytics analytics;
    @Inject PortfolioCacheRx portfolioCache;
    @Inject UserProfileCacheRx userProfileCache;
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
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
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

    /**
     * start
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
        AndroidObservable.bindFragment(this, userProfileCache.get(shownUser))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProfileCacheObserver());
    }

    public boolean isShownOwnedPortfolioIdForOtherPeople(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        return ownedPortfolioId != null && ownedPortfolioId.portfolioId <= 0;
    }

    protected void fetchSimplePage()
    {
        if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
        {
            AndroidObservable.bindFragment(this, getPositionsCache.get().get(getPositionsDTOKey))
                    .subscribe(createGetPositionsCacheObserver());
        }
    }

    protected void refreshSimplePage()
    {
        getPositionsCache.get().get(getPositionsDTOKey);
    }

    protected void fetchPortfolio()
    {
        if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            if (currentUserId.toUserBaseKey().equals(((OwnedPortfolioId) getPositionsDTOKey).getUserBaseKey()))
            {
                AndroidObservable.bindFragment(this, portfolioCache.get(((OwnedPortfolioId) getPositionsDTOKey)))
                        .subscribe(createPortfolioCacheObserver());
            }
            // We do not need to fetch for other players
        }
        // We do not care for now about those that are loaded with LeaderboardMarkUserId
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
            AndroidObservable.bindFragment(this, portfolioCache.get((OwnedPortfolioId) getPositionsDTOKey))
                    .subscribe(new Observer<Pair<OwnedPortfolioId, PortfolioDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                        }

                        @Override public void onNext(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                        {
                            portfolioHeaderView.linkWith(pair.second);
                        }
                    });
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

    @NotNull protected Observer<Pair<GetPositionsDTOKey, GetPositionsDTO>> createGetPositionsCacheObserver()
    {
        return new GetPositionsObserver();
    }

    protected class GetPositionsObserver
            implements Observer<Pair<GetPositionsDTOKey, GetPositionsDTO>>
    {
        @Override public void onNext(Pair<GetPositionsDTOKey, GetPositionsDTO> pair)
        {
            linkWith(pair.second, true);
            showResultIfNecessary();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            boolean loaded = checkLoadingSuccess();
            if (!loaded)
            {
                showErrorView();
                THToast.show(getString(R.string.error_fetch_position_list_info));
                Timber.d(e, "Error fetching the positionList info");
            }
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }

    @NotNull protected Observer<Pair<UserBaseKey, UserProfileDTO>> createProfileCacheObserver()
    {
        return new AbstractPositionListProfileCacheObserver();
    }

    protected class AbstractPositionListPremiumUserFollowedListener
            implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(@NotNull UserBaseKey userFollowed, @NotNull UserProfileDTO currentUserProfileDTO)
        {
            displayHeaderView();
            fetchSimplePage();
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.PositionList));
        }

        @Override public void onUserFollowFailed(@NotNull UserBaseKey userFollowed, @NotNull Throwable error)
        {
            // do nothing for now
        }
    }

    protected class AbstractPositionListProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second, true);
            showResultIfNecessary();
            showPrettyReviewAndInvite();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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
            AndroidObservable.bindFragment(this, portfolioCache.get((OwnedPortfolioId) getPositionsDTOKey))
                    .subscribe(new Observer<Pair<OwnedPortfolioId, PortfolioDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                        }

                        @Override public void onNext(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                        {
                            Double profit = pair.second.roiSinceInception;
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
                    });
        }
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

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        portfolioHeaderView.linkWith(portfolioCompactDTO);
        displayActionBarTitle();
    }
}
