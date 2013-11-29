package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.security.AbstractSecurityInfoFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.widget.list.ExpandingListView;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 5:56 PM To change this template use File | Settings | File Templates. */
public class PositionListFragment extends DashboardFragment
    implements BaseFragment.TabBarVisibilityInformer, PositionListener
{
    public static final String TAG = PositionListFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = PositionListFragment.class.getName() + ".firstPositionVisible";
    public static final String BUNDLE_KEY_EXPANDED_LIST_FLAGS = PositionListFragment.class.getName() + ".expandedListFlags";

    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject Lazy<GetPositionsCache> getPositionsCache;

    private PortfolioHeaderView portfolioHeaderView;
    private ExpandingListView positionsListView;

    protected OwnedPortfolioId ownedPortfolioId;
    private GetPositionsDTO getPositionsDTO;

    protected AbstractPositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;
    private boolean[] expandedPositions;

    private DTOCache.GetOrFetchTask<GetPositionsDTO> fetchGetPositionsDTOTask;
    private GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsCacheListener;

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

    private void initViews(View view)
    {
        if (view != null)

        {
            positionsListView = (ExpandingListView) view.findViewById(R.id.position_list);
            positionsListView.setEmptyView(view.findViewById(android.R.id.empty));

            if (positionItemAdapter == null)
            {
                createPositionItemAdapter();
            }
            positionItemAdapter.setCellListener(this);
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
                int headerLayoutId = headerFactory.get().layoutIdForArguments(args);
                stub.setLayoutResource(headerLayoutId);
                this.portfolioHeaderView = (PortfolioHeaderView) stub.inflate();
            }
        }
    }

    protected void createPositionItemAdapter()
    {
        positionItemAdapter = new PositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.position_item_header,
                R.layout.position_locked_item,
                R.layout.position_open_no_period,
                R.layout.position_closed_no_period,
                R.layout.position_quick_nothing);
    }

    private void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionNothingView)
        {
            navigator.popFragment(); // Feels HACKy
            navigator.goToTab(DashboardTabType.TRENDING);
        }
        else if (view instanceof LockedPositionItem)
        {
            THToast.show("show prompt to follow user");
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
        THLog.d(TAG, "onResume");
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new OwnedPortfolioId(args), true);
        }
    }

    @Override public void onPause()
    {
        THLog.d(TAG, "onPause");
        getPositionsCacheListener = null;
        if (fetchGetPositionsDTOTask != null)
        {
            fetchGetPositionsDTOTask.forgetListener(true);
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
        THLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        outState.putBooleanArray(BUNDLE_KEY_EXPANDED_LIST_FLAGS, expandedPositions);
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
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

    protected void fetchSimplePage()
    {
        if (ownedPortfolioId != null && ownedPortfolioId.isValid())
        {
            if (getPositionsCacheListener == null)
            {
                getPositionsCacheListener = createGetPositionsCacheListener();
            }
            if (fetchGetPositionsDTOTask != null)
            {
                fetchGetPositionsDTOTask.forgetListener(true);
            }
            fetchGetPositionsDTOTask = getPositionsCache.get().getOrFetch(ownedPortfolioId, getPositionsCacheListener);
            fetchGetPositionsDTOTask.execute();
        }
    }

    public void linkWith(GetPositionsDTO getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        if (this.getPositionsDTO != null && ownedPortfolioId != null)
        {
            positionItemAdapter.setPositions(getPositionsDTO.positions, ownedPortfolioId.getPortfolioId());
            restoreExpandingStates();
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

    private void handleInfoButtonPressed(MenuItem item)
    {
        THToast.show("No info for now");
    }

    private GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO>()
        {
            @Override public void onDTOReceived(OwnedPortfolioId key, GetPositionsDTO value)
            {
                if (key.equals(ownedPortfolioId))
                {
                    linkWith(value, true);
                }
            }

            @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
            {
                THToast.show(getString(R.string.error_fetch_position_list_info));
                THLog.e(TAG, "Error fetching the getPortfolioId " + key, error);
            }
        };
    }

    private void pushBuySellFragment(OwnedPositionId clickedOwnedPositionId, boolean isBuy)
    {
        if (clickedOwnedPositionId != null)
        {
            PositionDTO positionDTO = positionCache.get().get(clickedOwnedPositionId);
            if (positionDTO == null)
            {
                THToast.show(getString(R.string.error_lost_position_in_cache));
            }
            else
            {
                SecurityId securityId = securityIdCache.get().get(positionDTO.getSecurityIntegerId());
                if (securityId == null)
                {
                    THToast.show(getString(R.string.error_find_security_id_to_int));
                }
                else
                {
                    Bundle args = new Bundle();
                    args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                    args.putBoolean(BuySellFragment.BUNDLE_KEY_IS_BUY, isBuy);
                    navigator.pushFragment(BuySellFragment.class, args);
                }
            }
        }
        else
        {
            THLog.e(TAG, "Was passed a null clickedOwnedPositionId", new IllegalArgumentException());
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="PositionListener">
    @Override public void onTradeHistoryClicked(OwnedPositionId clickedOwnedPositionId)
    {
        navigator.pushFragment(TradeListFragment.class, clickedOwnedPositionId.getArgs());
    }

    @Override public void onBuyClicked(OwnedPositionId clickedOwnedPositionId)
    {
        pushBuySellFragment(clickedOwnedPositionId, true);
    }

    @Override public void onSellClicked(OwnedPositionId clickedOwnedPositionId)
    {
        pushBuySellFragment(clickedOwnedPositionId, false);
    }

    @Override public void onAddAlertClicked(OwnedPositionId clickedOwnedPositionId)
    {
       THToast.show("Alert");
    }

    @Override public void onStockInfoClicked(OwnedPositionId clickedOwnedPositionId)
    {
        PositionDTO positionDTO = positionCache.get().get(clickedOwnedPositionId);
        if (positionDTO == null)
        {
            THToast.show(R.string.error_lost_position_in_cache);
            THLog.e(TAG, "PositionDTO is not found", new IllegalStateException());
        }
        else
        {
            SecurityId securityId = securityIdCache.get().get(positionDTO.getSecurityIntegerId());
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
    }
    //</editor-fold>
}
