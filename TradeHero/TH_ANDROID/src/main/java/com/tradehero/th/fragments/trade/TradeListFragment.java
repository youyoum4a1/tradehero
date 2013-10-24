package com.tradehero.th.fragments.trade;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.adapters.trade.TradeListItemAdapter;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.OwnedTradeIdList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by julien on 23/10/13
 */
public class TradeListFragment extends DashboardFragment
{
    public static final String TAG = TradeListFragment.class.getSimpleName();

    private OwnedPositionId ownedPositionId;
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<TradeListCache> tradeListCache;

    private ListView tradeListView;
    private TradeListItemAdapter adapter;
    private Bundle desiredArguments;

    private AsyncTask<Void, Void, OwnedTradeIdList> fetchTradesTask;
    private TradeListCache.Listener<OwnedPositionId, OwnedTradeIdList> getTradesListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = null;
        view = (RelativeLayout)inflater.inflate(R.layout.fragment_trade_list, container, false);

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
            if (adapter == null)
            {
                adapter = new TradeListItemAdapter(
                        getActivity(),
                        getActivity().getLayoutInflater());
            }

            tradeListView = (ListView) view.findViewById(R.id.trade_list);
            if (tradeListView != null)
            {
                tradeListView.setAdapter(adapter);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.trade_list_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case android.R.id.home:
                navigator.popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    @Override public void onResume()
    {
        super.onResume();
        if (desiredArguments != null)
        {
            linkWith(new OwnedPositionId(desiredArguments), true);
        }
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;
        fetchTrades();
        if (andDisplay)
        {
            display();
        }
    }

    private void fetchTrades()
    {
        if (ownedPositionId != null && ownedPositionId.isValid())
        {
            if (getTradesListener == null)
            {
                getTradesListener = createGetTradesListener();
            }
            if (fetchTradesTask != null)
            {
                fetchTradesTask.cancel(false);
            }
            fetchTradesTask = tradeListCache.get().getOrFetch(ownedPositionId, getTradesListener);
            fetchTradesTask.execute();
        }
    }

    private DTOCache.Listener<OwnedPositionId, OwnedTradeIdList> createGetTradesListener()
    {
        return new TradeListCache.Listener<OwnedPositionId, OwnedTradeIdList>()
        {
            @Override
            public void onDTOReceived(OwnedPositionId key, OwnedTradeIdList ownedTradeIds)
            {
                if (ownedPositionId != null && ownedPositionId.equals(key))
                {
                    linkWith(ownedTradeIds, true);
                }
            }
        };
    }


    public void linkWith(List<OwnedTradeId> ownedTradeIds, boolean andDisplay)
    {
        if (ownedTradeIds!= null)
        {
            adapter.setItems(ownedTradeIds);
            getView().post(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter.notifyDataSetChanged();
                        }
                    }
            );
        }

        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        // TODO: update header

    }


}
