package com.tradehero.th.fragments.position;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
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
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.fragments.competition.ProviderSecurityListFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.widget.list.ExpandingListView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 5:56 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractPositionListFragment<
        CacheQueryIdType,
        PositionDTOType extends PositionDTO,
        GetPositionsDTOType extends AbstractGetPositionsDTO<PositionDTOType>>
        extends BasePurchaseManagerFragment
        implements BaseFragment.TabBarVisibilityInformer,
            PositionListener<PositionDTOType>,
            PortfolioHeaderView.OnFollowRequestedListener,
            PortfolioHeaderView.OnTimelineRequestedListener
{
    public static final String TAG = PositionListFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE = AbstractPositionListFragment.class.getName() + ".showPortfolioId";
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = AbstractPositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_EXPANDED_LIST_FLAGS = AbstractPositionListFragment.class.getName() + ".expandedListFlags";

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;

    private PortfolioHeaderView portfolioHeaderView;
    protected ExpandingListView positionsListView;
    private ProgressBar progressBar;

    protected OwnedPortfolioId ownedPortfolioId;
    protected GetPositionsDTOType getPositionsDTO;

    protected AbstractPositionItemAdapter<PositionDTOType> positionItemAdapter;

    private int firstPositionVisible = 0;
    private boolean[] expandedPositions;

    protected DTOCache.GetOrFetchTask<CacheQueryIdType, GetPositionsDTOType> fetchGetPositionsDTOTask;
    protected DTOCache.Listener<CacheQueryIdType, GetPositionsDTOType> getPositionsCacheListener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null)
        {
            firstPositionVisible = savedInstanceState.getInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
            expandedPositions = savedInstanceState.getBooleanArray(BUNDLE_KEY_EXPANDED_LIST_FLAGS);
        }

        View view = inflater.inflate(R.layout.fragment_positions_list, container, false);

        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        if (view != null)
        {
            progressBar = (ProgressBar) view.findViewById(android.R.id.empty);

            positionsListView = (ExpandingListView) view.findViewById(R.id.position_list);
            positionsListView.setEmptyView(view.findViewById(android.R.id.empty));

            if (positionItemAdapter == null)
            {
                createPositionItemAdapter();
            }
            if (positionsListView != null)
            {
                positionsListView.setAdapter(positionItemAdapter);
                positionsListView.setExpandingListItemListener(new ExpandingListView.ExpandingListItemListener()
                {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
                    {
                        handlePositionItemClicked(adapterView, view, position, id);
                    }
                    @Override public void onItemDidExpand(AdapterView<?> parent, View view, int position, long id)
                    {
                    }
                    @Override public void onItemDidCollapse(AdapterView<?> parent, View view, int position, long id)
                    {
                    }
                });
            }

            // portfolio header
            Bundle args = getArguments();
            if (args != null)
            {
                ViewStub stub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
                int headerLayoutId = headerFactory.get().layoutIdFor(args.getBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
                stub.setLayoutResource(headerLayoutId);
                this.portfolioHeaderView = (PortfolioHeaderView) stub.inflate();
            }
        }
    }

    abstract protected void createPositionItemAdapter();

    @Override abstract protected void createUserInteractor();

    private void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            // Need to handle the case when portfolio is for a competition
            PortfolioDTO shownPortfolio = portfolioCache.get().get(ownedPortfolioId);
            if (shownPortfolio == null || shownPortfolio.providerId == null)
            {
                navigator.goToTab(DashboardTabType.TRENDING);
            }
            else
            {
                Bundle args = new Bundle();
                args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
                args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PROVIDER_ID, shownPortfolio.getProviderId().getArgs());
                navigator.pushFragment(ProviderSecurityListFragment.class, args);
            }
        }
        else if (view instanceof PositionLockedView)
        {
            popFollowUser(ownedPortfolioId.getUserBaseKey());
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

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.position_list_info:
                handleInfoButtonPressed(item);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        getPositionsCacheListener = null;
        if (portfolioHeaderView != null)
        {
            portfolioHeaderView.setFollowRequestedListener(null);
            portfolioHeaderView.setTimelineRequestedListener(null);
        }
        if (fetchGetPositionsDTOTask != null)
        {
            fetchGetPositionsDTOTask.setListener(null);
        }
        fetchGetPositionsDTOTask = null;

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
                for (Boolean state: expandedStates)
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
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        outState.putBooleanArray(BUNDLE_KEY_EXPANDED_LIST_FLAGS, expandedPositions);
    }

    @Override public void onDestroyView()
    {
        if (positionsListView != null)
        {
            positionsListView.setOnScrollListener(null);
            positionsListView.setOnTouchListener(null);
        }
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = null;
        super.onDestroyView();
    }

    public void linkWith(OwnedPortfolioId ownedPortfolioId, boolean andDisplay)
    {
        this.ownedPortfolioId = ownedPortfolioId;
        fetchSimplePage();
        if (andDisplay)
        {
            // TODO finer grained
            display();
        }
    }

    abstract protected void fetchSimplePage();
    abstract protected DTOCache.Listener<CacheQueryIdType, GetPositionsDTOType> createCacheListener();
    abstract protected DTOCache.GetOrFetchTask<CacheQueryIdType, GetPositionsDTOType> createCacheFetchTask();

    public void linkWith(GetPositionsDTOType getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        if (this.getPositionsDTO != null)
        {
            createPositionItemAdapter();
            positionItemAdapter.setItems(getPositionsDTO.positions);
            restoreExpandingStates();
            if (positionsListView != null)
            {
                positionsListView.setAdapter(positionItemAdapter);
            }
        }

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
                            positionItemAdapter.notifyDataSetChanged();
                            positionsListView.setSelection(firstPositionVisible);
                        }
                    }
            );
        }
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
            this.portfolioHeaderView.bindOwnedPortfolioId(this.ownedPortfolioId);
        }
    }

    public void displayActionBarTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
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

    public void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    private void handleInfoButtonPressed(MenuItem item)
    {
        THToast.show("No info for now");
    }

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
                    args.putBundle(BuySellFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                            clickedPositionDTO.getOwnedPositionId().getArgs());
                }
                args.putBoolean(BuySellFragment.BUNDLE_KEY_IS_BUY, isBuy);
                navigator.pushFragment(BuySellFragment.class, args);
            }
        }
        else
        {
            THLog.e(TAG, "Was passed a null clickedPositionDTO", new IllegalArgumentException());
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
        THLog.d(TAG, "onFollowRequested " + userBaseKey);
        popFollowUser(userBaseKey);
    }
    //</editor-fold>

    protected void popFollowUser(final UserBaseKey userBaseKey)
    {
        heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                userInteractor.followHero(userBaseKey);
            }
        });
    }

    //<editor-fold desc="PortfolioHeaderView.OnTimelineRequestedListener">
    @Override public void onTimelineRequested(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);
        ((DashboardActivity) getActivity()).getNavigator().pushFragment(PushableTimelineFragment.class, args);
    }
    //</editor-fold>

    //<editor-fold desc="PositionListener">
    @Override public void onTradeHistoryClicked(PositionDTOType clickedPositionDTO)
    {
        Bundle args = new Bundle();
        // By default tries
        args.putBundle(TradeListFragment.BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE, clickedPositionDTO.getOwnedPositionId().getArgs());
        navigator.pushFragment(TradeListFragment.class, args);
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
        if (securityId != null)
        {
            Bundle args = new Bundle();
            args.putBundle(AlertCreateFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
            args.putBundle(AlertCreateFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            getNavigator().pushFragment(AlertCreateFragment.class, args);
        }
        else
        {
            THLog.d(TAG, "SecurityId was lost for clickedPositionDTO " + clickedPositionDTO);
            THToast.show(R.string.error_find_security_id_to_int);
        }
    }

    @Override public void onStockInfoClicked(PositionDTOType clickedPositionDTO)
    {
        SecurityId securityId = securityIdCache.get().get(clickedPositionDTO.getSecurityIntegerId());
        if (securityId == null)
        {
            THToast.show(R.string.error_find_security_id_to_int);
            THLog.e(TAG, "SecurityId is null", new IllegalStateException());
        }
        else
        {
            Bundle args = new Bundle();
            args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            navigator.pushFragment(StockInfoFragment.class, args);
        }
    }
    //</editor-fold>

    abstract public class AbstractPositionListTHIABUserInteractor extends THIABUserInteractor
    {
        public final String TAG = AbstractPositionListTHIABUserInteractor.class.getName();

        public AbstractPositionListTHIABUserInteractor()
        {
            super();
        }

        @Override protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
        {
            super.handlePurchaseReportSuccess(reportedPurchase, updatedUserProfile);
            displayHeaderView();

        }

        @Override protected void createFollowCallback()
        {
            this.followCallback = new UserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get())
            {
                @Override public void success(UserProfileDTO userProfileDTO, Response response)
                {
                    super.success(userProfileDTO, response);
                    displayHeaderView();
                }
            };
        }
    }

    abstract protected class AbstractGetPositionsListener<
            CacheQueryIdType,
            PositionDTOType extends PositionDTO,
            GetPositionsDTOType extends AbstractGetPositionsDTO<PositionDTOType>>
            implements DTOCache.Listener<CacheQueryIdType, GetPositionsDTOType>
    {
        @Override public void onErrorThrown(CacheQueryIdType key, Throwable error)
        {
            displayProgress(false);
            THToast.show(getString(R.string.error_fetch_position_list_info));
            THLog.e(TAG, "Error fetching the positionList info " + key, error);
        }
    }
}
