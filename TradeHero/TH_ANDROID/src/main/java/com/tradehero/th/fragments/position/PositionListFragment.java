package com.tradehero.th.fragments.position;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.position.PositionItemAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.position.FiledPositionCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 5:56 PM To change this template use File | Settings | File Templates. */
public class PositionListFragment extends DashboardFragment
    implements BaseFragment.ArgumentsChangeListener
{
    public static final String TAG = PositionListFragment.class.getSimpleName();

    private ListView openPositions;
    private PositionItemAdapter positionItemAdapter;

    private Bundle desiredArguments;

    private OwnedPortfolioId ownedPortfolioId;
    private GetPositionsDTO getPositionsDTO;
    @Inject Lazy<GetPositionsCache> getPositionsCache;
    private GetPositionsCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsCacheListener;
    private AsyncTask<Void, Void, GetPositionsDTO> fetchGetPositionsDTOTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        view = inflater.inflate(R.layout.fragment_positions_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            if (positionItemAdapter == null)
            {
                positionItemAdapter = new PositionItemAdapter(getActivity(), getActivity().getLayoutInflater());
            }

            openPositions = (ListView) view.findViewById(R.id.position_list);
            if (openPositions != null)
            {
                openPositions.setAdapter(positionItemAdapter);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        createOptionsMenu();
    }

    private void createOptionsMenu()
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.topbar_positions_list);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

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
            positionItemAdapter.setItems(getPositionsDTO.getFiledPositionIds(ownedPortfolioId.getPortfolioId()));
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
            // TODO finer grained
            display();
        }
    }

    public void display()
    {
        // TODO
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
}
