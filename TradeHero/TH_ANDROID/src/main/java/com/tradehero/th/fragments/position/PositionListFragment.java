package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKeyFactory;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.THRouter;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.widget.list.ExpandingListView;
import dagger.Lazy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Routable("user/:userId/portfolio/:portfolioId")
public class PositionListFragment
        extends BasePurchaseManagerFragment
        implements PositionListener<PositionDTO>,
        PortfolioHeaderView.OnFollowRequestedListener,
        PortfolioHeaderView.OnTimelineRequestedListener,
        WithTutorial
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = PositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = PositionListFragment.class.getName() + ".userBaseKey";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_EXPANDED_LIST_FLAGS = PositionListFragment.class.getName() + ".expandedListFlags";

    @Inject CurrentUserId currentUserId;
    @Inject GetPositionsDTOKeyFactory getPositionsDTOKeyFactory;
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Analytics analytics;
    @Inject PortfolioCache portfolioCache;
    @Inject UserProfileCache userProfileCache;

    @InjectView(R.id.position_list) protected ExpandingListView positionsListView;
    @InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.pull_to_refresh_position_list) PositionListView pullToRefreshListView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.error) View errorView;

    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;

    private PortfolioHeaderView portfolioHeaderView;
    protected GetPositionsDTOKey getPositionsDTOKey;
    protected GetPositionsDTO getPositionsDTO;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    @Nullable protected AbstractPositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;
    @Nullable private boolean[] expandedPositions;

    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> refreshGetPositionsDTOListener;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
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
    }

    @NotNull @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new AbstractPositionListPremiumUserFollowedListener();
    }

    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            firstPositionVisible = savedInstanceState.getInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
            expandedPositions = savedInstanceState.getBooleanArray(BUNDLE_KEY_EXPANDED_LIST_FLAGS);
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

            positionsListView.setAdapter(positionItemAdapter);
            positionsListView.setExpandingListItemListener(new ExpandingListView.ExpandingListItemListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
                {
                    handlePositionItemClicked(adapterView, view, position, id);
                }

                @Override public void onItemExpanded(AdapterView<?> parent, View view, int position, long id)
                {
                }

                @Override public void onItemCollapsed(AdapterView<?> parent, View view, int position, long id)
                {
                }
            });

            initPullToRefreshListView(view);

            // portfolio header
            headerStub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
            int headerLayoutId = headerFactory.get().layoutIdFor(getPositionsDTOKey);
            headerStub.setLayoutResource(headerLayoutId);
            this.portfolioHeaderView = (PortfolioHeaderView) headerStub.inflate();
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
        ExpandingListView.ExpandableItemClickHandler
                expandableItemClickListener = new ExpandingListView.ExpandableItemClickHandler(pullToRefreshListView.getRefreshableView());
        expandableItemClickListener.setExpandingListItemListener(new ExpandingListView.ExpandingListItemListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                handlePositionItemClicked(adapterView, view, position, id);
            }

            @Override public void onItemExpanded(AdapterView<?> parent, View view, int position, long id)
            {
            }

            @Override public void onItemCollapsed(AdapterView<?> parent, View view, int position, long id)
            {
            }
        });

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
    }

    protected void createPositionItemAdapter()
    {
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = new AbstractPositionItemAdapter(
                getActivity(),
                getLayoutResIds());
        positionItemAdapter.setCellListener(this);
    }

    @NotNull protected Map<PositionItemType, Integer> getLayoutResIds()
    {
        Map<PositionItemType, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemType.Header, R.layout.position_item_header);
        layouts.put(PositionItemType.Placeholder, R.layout.position_quick_nothing);
        layouts.put(PositionItemType.Locked, R.layout.position_locked_item);
        layouts.put(PositionItemType.Open, R.layout.position_open_no_period);
        layouts.put(PositionItemType.OpenInPeriod, R.layout.position_open_in_period);
        layouts.put(PositionItemType.Closed, R.layout.position_closed_no_period);
        layouts.put(PositionItemType.ClosedInPeriod, R.layout.position_closed_in_period);
        return layouts;
    }

    private void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            pushSecurityFragment();
        }
        else if (view instanceof PositionLockedView)
        {
            popFollowUser(shownUser);
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

        getDashboardNavigator().pushFragment(TrendingFragment.class, args);
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

        if (positionsListView != null)
        {
            firstPositionVisible = positionsListView.getFirstVisiblePosition();
        }

        // save expanding state
        if (positionItemAdapter != null)
        {
            List<Boolean> expandedStates = positionItemAdapter.getExpandedStatesPerPosition();
            if (expandedStates == null)
            {
                expandedPositions = null;
            }
            else
            {
                expandedPositions = new boolean[expandedStates.size()];
                int position = 0;
                for (Boolean state : expandedStates)
                {
                    expandedPositions[position++] = state;
                }
            }
        }
        super.onPause();
    }

    @Override public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        detachGetPositionsTask();
        detachUserProfileCache();
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        outState.putBooleanArray(BUNDLE_KEY_EXPANDED_LIST_FLAGS, expandedPositions);
    }

    @Override public void onStop()
    {
        detachGetPositionsTask();
        detachRefreshGetPositionsTask();
        detachUserProfileCache();

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (positionsListView != null)
        {
            positionsListView.setOnScrollListener(null);
            positionsListView.setOnTouchListener(null);
            positionsListView.setExpandingListItemListener(null);
        }
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
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
        super.onDestroy();
    }

    /**
     * start
     * @param positionsDTOKey
     * @param andDisplay
     */
    public void linkWith(GetPositionsDTOKey positionsDTOKey, boolean andDisplay)
    {
        this.getPositionsDTOKey = positionsDTOKey;
        this.userProfileDTO = null;

        detachUserProfileCache();
        //fetchPortfolio(false);
        fetchUserProfile();
        fetchSimplePage();
        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCacheListener = createProfileCacheListener();
        userProfileCache.register(shownUser, userProfileCacheListener);
        userProfileCache.getOrFetchAsync(shownUser);
    }

    public boolean isShownOwnedPortfolioIdForOtherPeople(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        return ownedPortfolioId != null && ownedPortfolioId.portfolioId <= 0;
    }

    protected void fetchSimplePage()
    {
        fetchSimplePage(true);
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
        if (userProfileCacheListener != null)
        {
            userProfileCache.unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    protected void rePurposeAdapter()
    {
        if (this.getPositionsDTO != null)
        {
            createPositionItemAdapter();
            positionItemAdapter.setItems(getPositionsDTO.positions);
            restoreExpandingStates();
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

    protected void restoreExpandingStates()
    {
        positionItemAdapter.setExpandedStatesPerPosition(expandedPositions);

        // temporary fix! reason to check if the view is null: sometime dto is received too fast, before onCreateView ....
        if (getView() != null)
        {
            getView().post(
                    new Runnable()
                    {
                        @Override public void run()
                        {
                            AbstractPositionItemAdapter adapterCopy =
                                    positionItemAdapter;
                            ExpandingListView listViewCopy = positionsListView;
                            if (adapterCopy != null && listViewCopy != null)
                            {
                                adapterCopy.notifyDataSetChanged();
                                listViewCopy.setSelection(firstPositionVisible);
                            }
                        }
                    }
            );
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
        if (getPositionsDTO != null && getPositionsDTO.positions != null)
        {
            String title = String.format(getResources().getString(R.string.position_list_action_bar_header),
                    getPositionsDTO.positions.size());
            setActionBarTitle(title);
        }
        else
        {
            setActionBarTitle(R.string.position_list_action_bar_header_unknown);
        }
    }

    private void pushBuySellFragment(@Nullable PositionDTO clickedPositionDTO, boolean isBuy)
    {
        if (clickedPositionDTO != null)
        {
            SecurityId securityId = securityIdCache.get().get(clickedPositionDTO.getSecurityIntegerId());
            if (securityId == null)
            {
                THToast.show(getString(R.string.error_find_security_id_to_int));
            }
            else
            {
                Bundle args = new Bundle();
                args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                if (currentUserId.toUserBaseKey().equals(clickedPositionDTO.getUserBaseKey()))
                {
                    // We only add if this the current user portfolio
                    BuySellFragment.putApplicablePortfolioId(args, clickedPositionDTO.getOwnedPortfolioId());
                }
                args.putBoolean(BuySellFragment.BUNDLE_KEY_IS_BUY, isBuy);

                OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
                if (ownedPortfolioId != null)
                {
                    BuySellFragment.putApplicablePortfolioId(args, ownedPortfolioId);
                }

                getDashboardNavigator().pushFragment(BuySellFragment.class, args);
            }
        }
        else
        {
            Timber.e("Was passed a null clickedPositionDTO", new IllegalArgumentException());
        }
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

        //heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
        //{
        //    @Override public void onClick(DialogInterface dialog, int which)
        //    {
        //        premiumFollowUser(userBaseKey);
        //    }
        //});
    }

    //<editor-fold desc="PortfolioHeaderView.OnTimelineRequestedListener">
    @Override public void onTimelineRequested(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        thRouter.save(args, userBaseKey);
        ((DashboardActivity) getActivity())
                .getDashboardNavigator().pushFragment(PushableTimelineFragment.class, args);
    }
    //</editor-fold>

    //<editor-fold desc="PositionListener">
    @Override public void onTradeHistoryClicked(@NotNull PositionDTO clickedPositionDTO)
    {
        Bundle args = new Bundle();
        // By default tries
        TradeListFragment.putPositionDTOKey(args, clickedPositionDTO.getPositionDTOKey());
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null)
        {
            TradeListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }
        getDashboardNavigator().pushFragment(TradeListFragment.class, args);
    }

    @Override public void onBuyClicked(PositionDTO clickedPositionDTO)
    {
        pushBuySellFragment(clickedPositionDTO, true);
    }

    @Override public void onSellClicked(PositionDTO clickedPositionDTO)
    {
        pushBuySellFragment(clickedPositionDTO, false);
    }

    @Override public void onAddAlertClicked(@NotNull PositionDTO clickedPositionDTO)
    {
        SecurityId securityId = securityIdCache.get().get(clickedPositionDTO.getSecurityIntegerId());
        if (securityId != null && getApplicablePortfolioId() != null)
        {
            Bundle args = new Bundle();
            AlertCreateFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
            AlertCreateFragment.putSecurityId(args, securityId);
            getDashboardNavigator().pushFragment(AlertCreateFragment.class, args);
        }
        else
        {
            Timber.d("SecurityId was lost for clickedPositionDTO %s", clickedPositionDTO);
            THToast.show(R.string.error_find_security_id_to_int);
        }
    }

    @Override public void onStockInfoClicked(@NotNull PositionDTO clickedPositionDTO)
    {
        SecurityId securityId = securityIdCache.get().get(clickedPositionDTO.getSecurityIntegerId());
        if (securityId == null)
        {
            THToast.show(R.string.error_find_security_id_to_int);
            Timber.d("SecurityId is null", new IllegalStateException());
        }
        else
        {
            Bundle args = new Bundle();
            args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            getDashboardNavigator().pushFragment(StockInfoFragment.class, args);
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
            extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            displayHeaderView();
            fetchSimplePage(true);
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.PositionList));
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
}
