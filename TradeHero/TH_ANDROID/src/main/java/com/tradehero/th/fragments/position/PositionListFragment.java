package com.tradehero.th.fragments.position;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.position.PositionItemAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.FiledPositionId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.widget.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.persistence.security.SecurityIdCache;
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

    private ImageButton btnActionBarBack;
    private TextView actionBarHeader;
    private ImageButton btnActionBarInfo;

    private Bundle desiredArguments;

    private OwnedPortfolioId ownedPortfolioId;
    private GetPositionsDTO getPositionsDTO;
    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    private GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsCacheListener;
    private AsyncTask<Void, Void, GetPositionsDTO> fetchGetPositionsDTOTask;

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
                openPositions.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        int action = motionEvent.getAction();
                        if (action == MotionEvent.ACTION_UP)
                        {
                            togglePositionMoreInfo();
                        }
                        return false; // If we do not return false, onScrollStateChanged will never be notified
                    }
                });
                openPositions.setOnScrollListener(new AbsListView.OnScrollListener()
                {
                    @Override public void onScrollStateChanged(AbsListView absListView, int scrollState)
                    {
                        // When it starts scrolling, it should cancel the more info request
                        if (scrollState != SCROLL_STATE_IDLE)
                        {
                            // Cancel more info request
                            positionForMoreInfo = null;
                        }
                    }

                    @Override public void onScroll(AbsListView absListView, int i, int i2, int i3)
                    {
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
            THToast.show("No item handler for now");
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        createOptionsMenu();
    }

    private void createOptionsMenu()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.topbar_positions_list);

        btnActionBarBack = (ImageButton) actionBar.getCustomView().findViewById(R.id.btn_back);
        if (btnActionBarBack != null)
        {
            btnActionBarBack.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBackButtonPressed(view);
                }
            });
        }

        actionBarHeader = (TextView) actionBar.getCustomView().findViewById(R.id.header_text);

        btnActionBarInfo = (ImageButton) actionBar.getCustomView().findViewById(R.id.btn_info);
        if (btnActionBarInfo != null)
        {
            btnActionBarInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleInfoButtonPressed(view);
                }
            });
        }

        displayHeaderText();
        // TODO add handlers
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
            fetchGetPositionsDTOTask.cancel(false);
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
        if (btnActionBarBack != null)
        {
            btnActionBarBack.setOnClickListener(null);
        }
        if (btnActionBarInfo != null)
        {
            btnActionBarInfo.setOnClickListener(null);
        }
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
                fetchGetPositionsDTOTask.cancel(false);
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
            displayHeaderText();
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
        displayHeaderText();
    }

    public void displayHeaderText()
    {
        if (actionBarHeader != null)
        {
            if (getPositionsDTO != null && getPositionsDTO.positions != null)
            {
                actionBarHeader.setText(String.format(
                        getResources().getString(R.string.position_list_action_bar_header),
                        getPositionsDTO.positions.size()));
            }
            else
            {
                actionBarHeader.setText(R.string.position_list_action_bar_header_unknown);
            }
        }
    }

    private void handleBackButtonPressed(View view)
    {
        navigator.popFragment();
    }

    private void handleInfoButtonPressed(View view)
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

    private void pushTradeFragment(SecurityIntegerId securityIntegerId, boolean isBuy)
    {
        SecurityId securityId = securityIdCache.get().get(securityIntegerId);
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
    @Override public void onAddAlertClicked(int position, FiledPositionId clickedFiledPositionId)
    {
        THToast.show("Add Alert at position " + position);
    }

    @Override public void onBuyClicked(int position, FiledPositionId clickedFiledPositionId)
    {
        THToast.show("Buy at position " + position);
        pushTradeFragment(clickedFiledPositionId.getSecurityIntegerId(), true);
    }

    @Override public void onSellClicked(int position, FiledPositionId clickedFiledPositionId)
    {
        THToast.show("Sell at position " + position);
        pushTradeFragment(clickedFiledPositionId.getSecurityIntegerId(), false);
    }

    @Override public void onStockInfoClicked(int position, FiledPositionId clickedFiledPositionId)
    {
        THToast.show("Stock Info at position " + position);
    }

    @Override public void onMoreInfoClicked(int position, FiledPositionId clickedFiledPositionId)
    {
        // Final decision will be made on touch up of the list view.
        positionForMoreInfo = position;
    }

    @Override public void onTradeHistoryClicked(int position, FiledPositionId clickedFiledPositionId)
    {
        THToast.show("Trade History at position " + position);
    }
    //</editor-fold>
}
