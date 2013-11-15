package com.tradehero.th.fragments.trade;

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
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.OwnedTradeIdList;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.ArrayList;
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
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private TradeListOverlayHeaderView header;
    private TradeListHeaderView tableHeader;
    private ListView tradeListView;

    private TradeListItemAdapter adapter;

    private DTOCache.GetOrFetchTask<OwnedTradeIdList> fetchTradesTask;
    private TradeListCache.Listener<OwnedPositionId, OwnedTradeIdList> getTradesListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = null;
        view = (RelativeLayout)inflater.inflate(R.layout.fragment_trade_list, container, false);

        initViews(view, inflater);
        return view;
    }

    private void initViews(View view, LayoutInflater inflater)
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
                tableHeader = (TradeListHeaderView)inflater.inflate(R.layout.trade_list_header, null);
                registerTableHeaderListener();
                tradeListView.addHeaderView(tableHeader);
                tradeListView.setAdapter(adapter);
            }

            header = (TradeListOverlayHeaderView)view.findViewById(R.id.trade_list_header);
            registerOverlayHeaderListener();
        }
    }

    private void registerTableHeaderListener()
    {
        if (this.tableHeader == null) return;

        this.tableHeader.setListener(new TradeListHeaderView.TradeListHeaderClickListener()
        {
            @Override public void onBuyButtonClicked(TradeListHeaderView tradeListHeaderView, OwnedPositionId ownedPositionId)
            {
                pushTradeFragment(ownedPositionId, true);
            }

            @Override public void onSellButtonClicked(TradeListHeaderView tradeListHeaderView, OwnedPositionId ownedPositionId)
            {
                pushTradeFragment(ownedPositionId, false);
            }
        });
    }

    private void registerOverlayHeaderListener()
    {
        if (this.header == null) return;

        this.header.setListener(new TradeListOverlayHeaderView.Listener()
        {
            @Override public void onSecurityClicked(TradeListOverlayHeaderView headerView, OwnedPositionId ownedPositionId) {
                pushTradeFragment(ownedPositionId, true);
            }

            @Override
            public void onUserClicked(TradeListOverlayHeaderView headerView, UserBaseKey userId) {
                openUserProfile(userId);
            }
        });
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

    private void openUserProfile(UserBaseKey userId)
    {
        Bundle b = new Bundle();
        b.putInt(UserBaseKey.BUNDLE_KEY_KEY, userId.key);
        b.putBoolean(Navigator.NAVIGATE_FRAGMENT_NO_CACHE, true);

        if (currentUserBaseKeyHolder.getCurrentUserBaseKey().key != userId.key)
        {
            navigator.pushFragment(TimelineFragment.class, b, true);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.trade_list_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
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
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new OwnedPositionId(args), true);
        }
    }

    @Override public void onDestroyView()
    {
        if (fetchTradesTask != null)
        {
            fetchTradesTask.forgetListener(true);
        }
        fetchTradesTask = null;
        getTradesListener = null;
        adapter = null;
        super.onDestroyView();
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;
        if (!ownedPositionId.getUserBaseKey().equals(currentUserBaseKeyHolder.getCurrentUserBaseKey()))
        {
            this.tradeListView.removeHeaderView(this.tableHeader);
        }
        else
        {
            if (this.tableHeader.getParent() == null)
                this.tradeListView.addHeaderView(this.tableHeader);
        }

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
                fetchTradesTask.forgetListener(true);
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

            @Override public void onErrorThrown(OwnedPositionId key, Throwable error)
            {
                THToast.show(getString(R.string.error_fetch_trade_list_info));
                THLog.e(TAG, "Error fetching the list of trades " + key, error);
            }
        };
    }


    public void linkWith(List<OwnedTradeId> ownedTradeIds, boolean andDisplay)
    {
        if (ownedTradeIds!= null)
        {
            List<TradeListItemAdapter.ExpandableTradeItem> items = new ArrayList<>(ownedTradeIds.size());
            int i = 0;
            for(OwnedTradeId id : ownedTradeIds)
            {
                TradeListItemAdapter.ExpandableTradeItem item = new TradeListItemAdapter.ExpandableTradeItem(id, i == 0);
                item.setExpanded(i == 0);
                items.add(item);
                ++i;
            }
            adapter.setItems(items);
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
        if (this.ownedPositionId != null) {
            if (this.header != null)
                header.bindOwnedPositionId(this.ownedPositionId);

            if (this.tableHeader != null)
                tableHeader.bindOwnedPositionId(this.ownedPositionId);
        }
    }
}
