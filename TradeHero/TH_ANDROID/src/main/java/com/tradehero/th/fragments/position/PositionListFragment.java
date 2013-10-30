package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.position.PositionItemAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.widget.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.widget.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.widget.position.PositionLongView;
import com.tradehero.th.widget.position.PositionQuickNothingView;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 5:56 PM To change this template use File | Settings | File Templates. */
public class PositionListFragment extends DashboardFragment
    implements BaseFragment.TabBarVisibilityInformer,
        PositionLongView.OnListedPositionInnerLongClickedListener
{
    public static final String TAG = PositionListFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_POSITION_EXPANDED = PositionListFragment.class.getName() + ".positionExpanded";

    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    private PortfolioHeaderView portfolioHeaderView;
    private ListView positionsListView;
    private PositionItemAdapter positionItemAdapter;

    private OwnedPortfolioId ownedPortfolioId;

    private GetPositionsDTO getPositionsDTO;
    @Inject Lazy<GetPositionsCache> getPositionsCache;
    private GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsCacheListener;
    private DTOCache.GetOrFetchTask<GetPositionsDTO> fetchGetPositionsDTOTask;

    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<PositionCache> positionCache;

    private Integer positionForMoreInfo;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //THLog.d(TAG, "onCreateView");

        if (savedInstanceState != null)
        {
            if (savedInstanceState.containsKey(BUNDLE_KEY_POSITION_EXPANDED))
            {
                positionForMoreInfo = savedInstanceState.getInt(BUNDLE_KEY_POSITION_EXPANDED);
            }
            else
            {
                positionForMoreInfo = null;
            }
        }

        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = null;
        view = (RelativeLayout)inflater.inflate(R.layout.fragment_positions_list, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            if (positionItemAdapter == null)
            {
                positionItemAdapter = new PositionItemAdapter(
                        getActivity(),
                        getActivity().getLayoutInflater(),
                        R.layout.position_item_header,
                        R.layout.position_quick,
                        R.layout.position_long,
                        R.layout.position_quick_nothing);
                positionItemAdapter.setParentMoreInfoRequestedListener(this);
                positionItemAdapter.setMoreInfoPositionClicked(positionForMoreInfo);
            }

            positionsListView = (ListView) view.findViewById(R.id.position_list);
            if (positionsListView != null)
            {
                positionsListView.setAdapter(positionItemAdapter);
                positionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        handlePositionItemClicked(adapterView, view, i, l);
                    }
                });
            }

            Bundle args = getArguments();
            if (args != null)
            {
                ViewStub stub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
                int layout = headerFactory.get().layoutIdForArguments(args);
                stub.setLayoutResource(layout);
                this.portfolioHeaderView = (PortfolioHeaderView)stub.inflate();
            }
        }
    }

    private void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionQuickNothingView)
        {
            navigator.popFragment(); // Feels HACKy
            navigator.goToTab(DashboardTabType.TRENDING);
        }
        else
        {
            //THToast.show("No item handler for now");
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

        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (positionForMoreInfo != null)
        {
            outState.putInt(BUNDLE_KEY_POSITION_EXPANDED, positionForMoreInfo);
        }
        else
        {
            outState.remove(BUNDLE_KEY_POSITION_EXPANDED);
        }
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
            positionItemAdapter.setParentMoreInfoRequestedListener(null);
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

    private void fetchSimplePage()
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
            getView().post(
                    new Runnable()
                    {
                        @Override public void run()
                        {
                            positionItemAdapter.notifyDataSetChanged();
                        }
                    }
            );
        }

        if (andDisplay)
        {
            displayActionBarTitle();
            // TODO finer grained
            display();
        }
    }

    public void display()
    {
        if (this.portfolioHeaderView != null)
        {
            this.portfolioHeaderView.bindOwnedPortfolioId(this.ownedPortfolioId);
        }
        displayActionBarTitle();
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

    private void togglePositionMoreInfo()
    {
        if (positionItemAdapter != null)
        {
            positionItemAdapter.togglePositionClicked(positionForMoreInfo);
        }
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
                THLog.e(TAG, "Error fetching the getPositions " + key, error);
            }
        };
    }

    private void pushTradeFragment(OwnedPositionId clickedOwnedPositionId, boolean isBuy)
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
                    Bundle args = securityId.getArgs();
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

    //<editor-fold desc="PositionLongView.OnListedPositionInnerLongClickedListener">
    @Override public void onAddAlertClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        THToast.show("Add Alert at position " + position);
    }

    @Override public void onBuyClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        pushTradeFragment(clickedOwnedPositionId, true);
    }

    @Override public void onSellClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        pushTradeFragment(clickedOwnedPositionId, false);
    }

    @Override public void onStockInfoClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        THToast.show("Stock Info at position " + position);
    }

    @Override public void onMoreInfoClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        // Final decision will be made on touch up of the list view.
        positionForMoreInfo = position;
        togglePositionMoreInfo();
    }

    @Override public void onTradeHistoryClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        navigator.pushFragment(TradeListFragment.class, clickedOwnedPositionId.getArgs());
    }
    //</editor-fold>
}
