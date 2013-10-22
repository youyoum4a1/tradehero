package com.tradehero.th.fragments.position;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.position.PositionItemAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.widget.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.widget.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.widget.position.PositionQuickNothingView;
import dagger.Lazy;

import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 5:56 PM To change this template use File | Settings | File Templates. */
public class PositionListFragment extends DashboardFragment
    implements BaseFragment.ArgumentsChangeListener, BaseFragment.TabBarVisibilityInformer
{
    public static final String TAG = PositionListFragment.class.getSimpleName();

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
    private GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsCacheListener;
    private AsyncTask<Void, Void, GetPositionsDTO> fetchGetPositionsDTOTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
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
            }

            openPositions = (ListView) view.findViewById(R.id.position_list);
            if (openPositions != null)
            {
                openPositions.setAdapter(positionItemAdapter);
                //openPositions.setOnItemClickListener(new AdapterView.OnItemClickListener()
                //{
                //    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                //    {
                //        handlePositionItemClicked(parent, view, position, id);
                //    }
                //});
            }

            if (desiredArguments != null)
            {
                ViewStub stub = (ViewStub) view.findViewById(R.id.position_list_header_stub);
                int layout = headerFactory.get().layoutIdForArguements(desiredArguments);
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
            linkWith(new OwnedPortfolioId(desiredArguments), true);
        }
    }

    @Override public void onPause()
    {
        getPositionsCacheListener = null;
        if (fetchGetPositionsDTOTask != null)
        {
            fetchGetPositionsDTOTask.cancel(false);
        }
        fetchGetPositionsDTOTask = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
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
}
