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
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.AbstractGetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.competition.ProviderSecurityListFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
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
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.widget.list.ExpandingListView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

abstract public class AbstractPositionListFragment<
        CacheQueryIdType,
        PositionDTOType extends PositionDTO,
        GetPositionsDTOType extends AbstractGetPositionsDTO<PositionDTOType>>
        extends BasePurchaseManagerFragment
        implements BaseFragment.TabBarVisibilityInformer,
        PositionListener<PositionDTOType>,
        PortfolioHeaderView.OnFollowRequestedListener,
        PortfolioHeaderView.OnTimelineRequestedListener,
        WithTutorial
{
    public static final String BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE = AbstractPositionListFragment.class.getName() + ".showPortfolioId";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = AbstractPositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_EXPANDED_LIST_FLAGS = AbstractPositionListFragment.class.getName() + ".expandedListFlags";

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject UserProfileCache userProfileCache;
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;

    private PortfolioHeaderView portfolioHeaderView;
    @InjectView(R.id.position_list) protected ExpandingListView positionsListView;
    @InjectView(R.id.position_list_header_stub) ViewStub headerStub;
    @InjectView(R.id.pull_to_refresh_position_list) PositionListView pullToRefreshListView ;
    @InjectView(android.R.id.progress) ProgressBar progressBar ;
    @InjectView(R.id.error) View errorView ;

    protected OwnedPortfolioId shownOwnedPortfolioId;
    protected GetPositionsDTOType getPositionsDTO;
    protected PortfolioDTO portfolioDTO;
    protected UserProfileDTO userProfileDTO;

    protected AbstractPositionItemAdapter<PositionDTOType> positionItemAdapter;

    private int firstPositionVisible = 0;
    private boolean[] expandedPositions;

    protected DTOCache.GetOrFetchTask<CacheQueryIdType, GetPositionsDTOType> fetchGetPositionsDTOTask;
    protected DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchUserProfileTask;
    protected DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> fetchPortfolioDTOTask;

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new AbstractPositionListPremiumUserFollowedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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

    @Override protected void initViews(View view)
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
            Bundle args = getArguments();
            if (args != null)
            {
                headerStub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
                int headerLayoutId = headerFactory.get().layoutIdFor(args.getBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
                headerStub.setLayoutResource(headerLayoutId);
                this.portfolioHeaderView = (PortfolioHeaderView) headerStub.inflate();
            }
        }
        showLoadingView(true);
    }

    protected boolean checkLoadingSuccess()
    {
        return (userProfileDTO != null) && (portfolioDTO != null) && (getPositionsDTO != null);
    }

    protected void showResultIfNecessary()
    {
        boolean loaded = checkLoadingSuccess();
        Timber.d("checkLoadingSuccess %b",loaded);
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
        if (portfolioHeaderView != null && portfolioHeaderView instanceof  View)
        {
            ((View) portfolioHeaderView).setVisibility(shown ? View.GONE : View.VISIBLE);
        }
    }

    protected void showErrorView()
    {
        if (progressBar != null)
        {
            progressBar.setVisibility( View.GONE);
        }
        if (pullToRefreshListView != null)
        {
            pullToRefreshListView.setVisibility(View.GONE);
        }
        if (errorView != null)
        {
            errorView.setVisibility(View.VISIBLE);
        }
        if (portfolioHeaderView != null && portfolioHeaderView instanceof  View)
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
        //((ViewGroup) view).removeView(positionsListView);
        //pullToRefreshListView.setRefreshableView(positionsListView);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                // refresh header values
                //fetchPortfolio(true);
                refreshPortfolio();

                // refresh position cache
                //fetchSimplePage(true);
                refreshSimplePage();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

            }
        });
        //pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        //{
        //    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
        //    {
        //        // refresh header values
        //        //fetchPortfolio(true);
        //
        //        // refresh position cache
        //        //fetchSimplePage(true);
        //    }
        //});
        //displayProgress(true);
    }

    abstract protected void createPositionItemAdapter();

    private void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            // Need to handle the case when portfolio is for a competition
            PortfolioDTO shownPortfolio = portfolioCache.get().get(shownOwnedPortfolioId);
            if (shownPortfolio == null || shownPortfolio.providerId == null)
            {
                getDashboardNavigator().goToTab(DashboardTabType.TRENDING);
            }
            else
            {
                Bundle args = new Bundle();
                ProviderSecurityListFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
                args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PROVIDER_ID, shownPortfolio.getProviderId().getArgs());
                getNavigator().pushFragment(ProviderSecurityListFragment.class, args);
            }
        }
        else if (view instanceof PositionLockedView)
        {
            popFollowUser(shownOwnedPortfolioId.getUserBaseKey());
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.position_list_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle();
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle ownedPortfolioIdBundle = args.getBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE);
            if (ownedPortfolioIdBundle != null)
            {
                linkWith(new OwnedPortfolioId(ownedPortfolioIdBundle), true);
            }
        }
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

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        detachGetPositionsTask();
        detachUserProfileTask();
        detachPortfolioTask();
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        outState.putBooleanArray(BUNDLE_KEY_EXPANDED_LIST_FLAGS, expandedPositions);
    }

    @Override public void onDestroyView()
    {
        detachPortfolioTask();
        detachGetPositionsTask();
        detachUserProfileTask();

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

    /**
     * start
     * @param shownOwnedPortfolioId
     * @param andDisplay
     */
    public void linkWith(OwnedPortfolioId shownOwnedPortfolioId, boolean andDisplay)
    {
        this.shownOwnedPortfolioId = shownOwnedPortfolioId;
        this.portfolioDTO = null;
        this.userProfileDTO = null;

        detachPortfolioTask();
        detachUserProfileTask();
        fetchPortfolio(false);
        fetchUserProfile();
        fetchSimplePage();
        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    protected void refreshPortfolio()
    {
        Timber.d("fetchPortfolio");
        detachPortfolioTask();
        if (shownOwnedPortfolioId != null)
        {
            fetchPortfolioDTOTask = createRefreshPortfolioFetchTask(shownOwnedPortfolioId, true);
            fetchPortfolioDTOTask.execute();
        }
    }

    protected void fetchPortfolio(boolean force)
    {
        Timber.d("fetchPortfolio");
        detachPortfolioTask();
        if (shownOwnedPortfolioId != null)
        {
            // TODO this part is a stopgap while the currency of leaderboard positions are not decided
            Boolean isOtherPeople = isShownOwnedPortfolioIdForOtherPeople(shownOwnedPortfolioId);
            if (isOtherPeople != null && !isOtherPeople)
            {
                fetchPortfolioDTOTask = createPortfolioFetchTask(shownOwnedPortfolioId, force);
                fetchPortfolioDTOTask.execute();
                Timber.d("fetchPortfolio fetchPortfolioDTOTask execute");
            }
            else
            {
                // While waiting to get the first positions
                linkWithUSDPortfolio(true);
                Timber.d("fetchPortfolio linkWithUSDPortfolio");
            }
        }
    }

    // This is a hack to get the currency info
    protected void reAttemptFetchPortfolio()
    {
        if (getPositionsDTO != null && getPositionsDTO.positions != null && getPositionsDTO.positions.size() > 0)
        {
            PositionDTOType firstPosition = getPositionsDTO.positions.get(0);
            OwnedPortfolioId positionPortfolioId = firstPosition.getOwnedPortfolioId();
            if (!isShownOwnedPortfolioIdForOtherPeople(positionPortfolioId))
            {
                Timber.e("reAttemptFetchPortfolio");
                this.shownOwnedPortfolioId = positionPortfolioId;
                fetchPortfolio(false);
            }
        }
    }

    protected void fetchUserProfile()
    {
        detachUserProfileTask();
        if (shownOwnedPortfolioId != null && shownOwnedPortfolioId.userId != null)
        {
            fetchUserProfileTask = createUserProfileFetchTask(shownOwnedPortfolioId.getUserBaseKey());
            fetchUserProfileTask.execute();
        }
    }

    public boolean isShownOwnedPortfolioIdForOtherPeople(OwnedPortfolioId ownedPortfolioId)
    {
        return ownedPortfolioId == null ? false : (ownedPortfolioId.portfolioId == null || ownedPortfolioId.portfolioId <= 0);
    }

    abstract protected void fetchSimplePage();
    abstract protected void fetchSimplePage(boolean force);

    protected void refreshSimplePage()
    {
    }

    abstract protected DTOCache.Listener<CacheQueryIdType, GetPositionsDTOType> createGetPositionsCacheListener();
    abstract protected DTOCache.GetOrFetchTask<CacheQueryIdType, GetPositionsDTOType> createGetPositionsCacheFetchTask(boolean force);

    protected void detachGetPositionsTask()
    {
        if (fetchGetPositionsDTOTask != null)
        {
            fetchGetPositionsDTOTask.setListener(null);
        }
        fetchGetPositionsDTOTask = null;
    }

    protected void detachUserProfileTask()
    {
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.setListener(null);
        }
        fetchUserProfileTask = null;
    }

    protected void detachPortfolioTask()
    {
        if (fetchPortfolioDTOTask != null)
        {
            fetchPortfolioDTOTask.setListener(null);
        }
        fetchPortfolioDTOTask = null;
    }

    protected DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> createUserProfileFetchTask(UserBaseKey userBaseKey)
    {
        return userProfileCache.getOrFetch(userBaseKey, false, createProfileCacheListener());
    }

    protected DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> createPortfolioFetchTask(OwnedPortfolioId ownedPortfolioId, boolean force)
    {
        return portfolioCache.get().getOrFetch(ownedPortfolioId, force, createPortfolioCacheListener());
    }

    protected DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> createRefreshPortfolioFetchTask(OwnedPortfolioId ownedPortfolioId, boolean force)
    {
        return portfolioCache.get().getOrFetch(ownedPortfolioId, force, createPortfolioRefreshCacheListener());
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

    public void linkWith(GetPositionsDTOType getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        reAttemptFetchPortfolio();
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
                            AbstractPositionItemAdapter<PositionDTOType> adapterCopy =
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

    public void linkWith(PortfolioDTO portfolioDTO, boolean andDisplay)
    {
        this.portfolioDTO = portfolioDTO;
        rePurposeAdapter();
        if (andDisplay)
        {
            displayHeaderView();
        }
    }

    public void linkWithUSDPortfolio(boolean andDisplay)
    {
        PortfolioDTO portfolioDTO1 = new PortfolioDTO();
        portfolioDTO1.refCcyToUsdRate = 1d;
        portfolioDTO1.currencyDisplay = SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
        portfolioDTO1.currencyISO = SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_ISO;
        linkWith(portfolioDTO1, andDisplay);
    }

    public void display()
    {
        displayHeaderView();
        displayActionBarTitle();
    }

    private void displayHeaderView()
    {
        if (this.portfolioHeaderView != null)
        {
            Timber.d("displayHeaderView %s",portfolioHeaderView.getClass().getSimpleName());
            this.portfolioHeaderView.linkWith(this.portfolioDTO);
            this.portfolioHeaderView.linkWith(this.userProfileDTO);
        }
    }

    public void displayActionBarTitle()
    {
        SherlockFragmentActivity sherlockFragmentActivity = getSherlockActivity();
        if (sherlockFragmentActivity != null)
        {
            ActionBar actionBar = sherlockFragmentActivity.getSupportActionBar();
            if (getPositionsDTO != null && getPositionsDTO.positions != null)
            {
                String title = String.format(getResources().getString(R.string.position_list_action_bar_header),
                        getPositionsDTO.positions.size());
                actionBar.setTitle(title);
            }
            else
            {
                actionBar.setTitle(R.string.position_list_action_bar_header_unknown);
            }
        }
    }

    //public void displayProgress(boolean running)
    //{
    //    Timber.d("displayProgress %b", running);
    //    if (running)
    //    {
    //        pullToRefreshListView.setRefreshing();
    //    }
    //    else
    //    {
    //        pullToRefreshListView.onRefreshComplete();
    //    }
    //}

    private void pushBuySellFragment(PositionDTOType clickedPositionDTO, boolean isBuy)
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
                getNavigator().pushFragment(BuySellFragment.class, args);
            }
        }
        else
        {
            Timber.e("Was passed a null clickedPositionDTO", new IllegalArgumentException());
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

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
        refreshPortfolio();
        refreshSimplePage();
    }

    //</editor-fold>

    protected void popFollowUser(final UserBaseKey userBaseKey)
    {
        //TODO need to improve
        if (portfolioHeaderView instanceof OtherUserPortfolioHeaderView)
        {
            ((OtherUserPortfolioHeaderView)portfolioHeaderView).showFollowDialog();
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
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                PushableTimelineFragment.class, args);
    }
    //</editor-fold>

    //<editor-fold desc="PositionListener">
    @Override public void onTradeHistoryClicked(PositionDTOType clickedPositionDTO)
    {
        Bundle args = new Bundle();
        // By default tries
        args.putBundle(TradeListFragment.BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE, clickedPositionDTO.getOwnedPositionId().getArgs());
        getNavigator().pushFragment(TradeListFragment.class, args);
    }

    @Override public void onBuyClicked(PositionDTOType clickedPositionDTO)
    {
        pushBuySellFragment(clickedPositionDTO, true);
    }

    @Override public void onSellClicked(PositionDTOType clickedPositionDTO)
    {
        pushBuySellFragment(clickedPositionDTO, false);
    }

    @Override public void onAddAlertClicked(PositionDTOType clickedPositionDTO)
    {
        SecurityId securityId = securityIdCache.get().get(clickedPositionDTO.getSecurityIntegerId());
        if (securityId != null && getApplicablePortfolioId() != null)
        {
            Bundle args = new Bundle();
            AlertCreateFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
            args.putBundle(AlertCreateFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            getNavigator().pushFragment(AlertCreateFragment.class, args);
        }
        else
        {
            Timber.d("SecurityId was lost for clickedPositionDTO %s", clickedPositionDTO);
            THToast.show(R.string.error_find_security_id_to_int);
        }
    }

    @Override public void onStockInfoClicked(PositionDTOType clickedPositionDTO)
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
            getNavigator().pushFragment(StockInfoFragment.class, args);
        }
    }
    //</editor-fold>

    abstract protected class AbstractGetPositionsListener<
            CacheQueryIdType,
            PositionDTOType extends PositionDTO,
            GetPositionsDTOType extends AbstractGetPositionsDTO<PositionDTOType>>
            implements DTOCache.Listener<CacheQueryIdType, GetPositionsDTOType>
    {
        @Override public void onErrorThrown(CacheQueryIdType key, Throwable error)
        {
            //displayProgress(false);
            THToast.show(getString(R.string.error_fetch_position_list_info));
            showErrorView();
            Timber.e("Error fetching the positionList info %s", key, error);
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }

    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> createProfileCacheListener()
    {
        return new AbstractPositionListProfileCacheListener();
    }

    protected DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioCacheListener()
    {
        return new AbstractPositionListPortfolioCacheListener();
    }

    protected DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioRefreshCacheListener()
    {
        return new AbstractPositionListPortfolioRefreshCacheListener();
    }

    protected class AbstractPositionListPremiumUserFollowedListener
            extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            displayHeaderView();
            fetchSimplePage(true);
        }
    }

    protected class AbstractPositionListProfileCacheListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
            showResultIfNecessary();
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_user_profile);
            //TODO not just toast
            showErrorView();
        }
    }

    protected class AbstractPositionListPortfolioCacheListener implements DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>
    {
        @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value, boolean fromCache)
        {
            Timber.d("portfolioCacheListener onDTOReceived");
            linkWith(value, true);
            showResultIfNecessary();
        }

        @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
        {
            THToast.show(R.string.error_fetch_portfolio_info);
            showErrorView();
        }
    }

    protected class AbstractPositionListPortfolioRefreshCacheListener implements DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>
    {
        @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value, boolean fromCache)
        {
            if (!fromCache){
                Timber.d("refreshPortfolioCacheListener onDTOReceived");
                linkWith(value, true);
                //TODO not enougth
                showResultIfNecessary();
            }
        }

        @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
        {
            //THToast.show(R.string.error_fetch_portfolio_info);
            //showErrorView();
            boolean loaded = checkLoadingSuccess();
            if (!loaded)
            {
                showErrorView();
            }
        }
    }
}
