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
import com.tradehero.th.fragments.trade.TradeFragment;
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
    implements BaseFragment.ArgumentsChangeListener, BaseFragment.TabBarVisibilityInformer,
        PositionLongView.OnListedPositionInnerLongClickedListener
{
    public static final String TAG = PositionListFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_POSITION_EXPANDED = PositionListFragment.class.getName() + ".positionExpanded";

    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    private PortfolioHeaderView portfolioHeaderView;
    private ListView openPositions;
    private PositionItemAdapter positionItemAdapter;

    private Bundle desiredArguments;

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
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = null;
        view = (RelativeLayout)inflater.inflate(R.layout.fragment_positions_list, container, false);

        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

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
            }

            openPositions = (ListView) view.findViewById(R.id.position_list);
            if (openPositions != null)
            {
                openPositions.setAdapter(positionItemAdapter);
                openPositions.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        handlePositionItemClicked(adapterView, view, i, l);
                    }
                });
            }

            if (desiredArguments != null)
            {
                ViewStub stub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
                int layout = headerFactory.get().layoutIdForArguments(desiredArguments);
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
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

            case android.R.id.home:
                navigator.popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (desiredArguments != null)
        {
            if (desiredArguments.containsKey(BUNDLE_KEY_POSITION_EXPANDED))
            {
                positionForMoreInfo = desiredArguments.getInt(BUNDLE_KEY_POSITION_EXPANDED);
            }
            else
            {
                positionForMoreInfo = null;
            }
            if (positionItemAdapter != null)
            {
                positionItemAdapter.setMoreInfoPositionClicked(positionForMoreInfo);
            }
            linkWith(new OwnedPortfolioId(desiredArguments), true);
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

        if (desiredArguments == null)
        {
            desiredArguments = new Bundle();
        }
        if (positionForMoreInfo != null)
        {
            desiredArguments.putInt(BUNDLE_KEY_POSITION_EXPANDED, positionForMoreInfo);
        }
        else
        {
            desiredArguments.remove(BUNDLE_KEY_POSITION_EXPANDED);
        }
        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroyView()
    {
        if (openPositions != null)
        {
            openPositions.setOnScrollListener(null);
            openPositions.setOnTouchListener(null);
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
        };
    }

    private void pushTradeFragment(OwnedPositionId clickedOwnedPositionId, boolean isBuy)
    {
        if (clickedOwnedPositionId != null)
        {
            PositionDTO positionDTO = positionCache.get().get(clickedOwnedPositionId);
            if (positionDTO == null)
            {
                THToast.show("We have lost track of this trading position");
            }
            else
            {
                SecurityId securityId = securityIdCache.get().get(positionDTO.getSecurityIntegerId());
                if (securityId == null)
                {
                    THToast.show("Could not find this security");
                }
                else
                {
                    Bundle args = securityId.getArgs();
                    args.putBoolean(TradeFragment.BUNDLE_KEY_IS_BUY, isBuy);
                    navigator.pushFragment(TradeFragment.class, args);
                }
            }
        }
        else
        {
            THLog.e(TAG, "Was passed a null clickedOwnedPositionId", new IllegalArgumentException());
        }
    }

    //<editor-fold desc="BaseFragment.ArgumentsChangeListener">
    @Override public void onArgumentsChanged(Bundle args)
    {
        desiredArguments = args;
    }
    //</editor-fold>

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
        THToast.show("Buy at position " + position);
        pushTradeFragment(clickedOwnedPositionId, true);
    }

    @Override public void onSellClicked(int position, OwnedPositionId clickedOwnedPositionId)
    {
        THToast.show("Sell at position " + position);
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
