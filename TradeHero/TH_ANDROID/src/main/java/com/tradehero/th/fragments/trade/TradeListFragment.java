package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionDTOKeyFactory;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.OwnedTradeIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.view.TradeListHeaderView;
import com.tradehero.th.fragments.trade.view.TradeListOverlayHeaderView;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TradeListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = TradeListFragment.class.getName() + ".positionDTOKey";

    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<TradeListCache> tradeListCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject PortfolioCache portfolioCache;
    @Inject CurrentUserId currentUserId;
    @Inject PositionDTOKeyFactory positionDTOKeyFactory;

    @InjectView(android.R.id.empty) protected ProgressBar progressBar;
    @InjectView(R.id.trade_list_header) protected TradeListOverlayHeaderView header;
    @InjectView(R.id.trade_list) protected ListView tradeListView;

    private ActionBar actionBar;

    protected PositionDTOKey positionDTOKey;
    protected PositionDTO positionDTO;
    protected List<OwnedTradeId> ownedTradeIds;

    protected TradeListItemAdapter adapter;
    protected TradeListHeaderView.TradeListHeaderClickListener buttonListener;

    private DTOCache.GetOrFetchTask<OwnedPositionId, OwnedTradeIdList> fetchTradesTask;

    public static void putPositionDTOKey(Bundle args, PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    private static PositionDTOKey getPositionDTOKey(Bundle args, PositionDTOKeyFactory positionDTOKeyFactory)
    {
        return positionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.buttonListener = new TradeListHeaderView.TradeListHeaderClickListener()
        {
            @Override public void onBuyButtonClicked(TradeListHeaderView tradeListHeaderView)
            {
                pushBuySellFragment(true);
            }

            @Override public void onSellButtonClicked(TradeListHeaderView tradeListHeaderView)
            {
                pushBuySellFragment(false);
            }
        };
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_trade_list, container, false);

        ButterKnife.inject(this, view);
        initViews(view, inflater);
        return view;
    }

    private void initViews(View view, LayoutInflater inflater)
    {
        if (view != null)
        {
            createAdapter();
            adapter.setTradeListHeaderClickListener(this.buttonListener);

            if (tradeListView != null)
            {
                tradeListView.setAdapter(adapter);
            }

            registerOverlayHeaderListener();
        }
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        linkWith(getPositionDTOKey(getArguments(), positionDTOKeyFactory), true);
    }

    protected void createAdapter()
    {
        adapter = new TradeListItemAdapter(getActivity(), getActivity().getLayoutInflater());
    }

    protected void rePurposeAdapter()
    {
        if (this.positionDTO != null && this.ownedTradeIds != null)
        {
            createAdapter();
            adapter.setTradeListHeaderClickListener(this.buttonListener);
            adapter.setShownPositionDTO(positionDTO);
            adapter.setUnderlyingItems(createUnderlyingItems());
            if (tradeListView != null)
            {
                tradeListView.setAdapter(adapter);
            }
        }
    }

    protected List<PositionTradeDTOKey> createUnderlyingItems()
    {
        List<PositionTradeDTOKey> created = new ArrayList<>();
        for (OwnedTradeId tradeId : ownedTradeIds)
        {
            created.add(new PositionTradeDTOKey(positionDTOKey, tradeId));
        }
        return created;
    }

    private void registerOverlayHeaderListener()
    {
        if (this.header == null)
        {
            return;
        }

        this.header.setListener(new TradeListOverlayHeaderView.Listener()
        {
            @Override public void onSecurityClicked(TradeListOverlayHeaderView headerView, OwnedPositionId ownedPositionId)
            {
                pushBuySellFragment(true);
            }

            @Override public void onUserClicked(TradeListOverlayHeaderView headerView, UserBaseKey userId)
            {
                openUserProfile(userId);
            }
        });
    }

    private void pushBuySellFragment(boolean isBuy)
    {
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
                Bundle args = new Bundle();
                populateBuySellArgs(args, isBuy, securityId);
                getNavigator().pushFragment(BuySellFragment.class, args);
            }
        }
    }

    protected void populateBuySellArgs(Bundle args, boolean isBuy, SecurityId securityId)
    {
        args.putBoolean(BuySellFragment.BUNDLE_KEY_IS_BUY, isBuy);
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    private void openUserProfile(UserBaseKey userId)
    {
        Bundle b = new Bundle();
        b.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId.key);

        if (!currentUserId.toUserBaseKey().equals(userId.key))
        {
            getNavigator().pushFragment(PushableTimelineFragment.class, b);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyOptionsMenu()
    {
        actionBar = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        detachFetchTradesTask();
        tradeListView = null;
        if (adapter != null)
        {
            adapter.setTradeListHeaderClickListener(null);
        }
        adapter = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        buttonListener = null;
        super.onDestroy();
    }

    protected void detachFetchTradesTask()
    {
        if (fetchTradesTask != null)
        {
            fetchTradesTask.setListener(null);
        }
        fetchTradesTask = null;
    }

    public void linkWith(PositionDTOKey newPositionDTOKey, boolean andDisplay)
    {
        this.positionDTOKey = newPositionDTOKey;
        linkWith(positionCache.get().get(newPositionDTOKey), andDisplay);

        if (andDisplay)
        {
            display();
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        rePurposeAdapter();
        fetchTrades();
        if (andDisplay)
        {
            displayHeader();
        }
    }

    protected void fetchTrades()
    {
        if (positionDTO != null)
        {
            if (fetchTradesTask != null)
            {
                fetchTradesTask.setListener(null);
            }
            fetchTradesTask = tradeListCache.get().getOrFetch(positionDTO.getOwnedPositionId(),
                    createTradeListeCacheListener());
            displayProgress(true);
            fetchTradesTask.execute();
        }
    }

    public void linkWith(List<OwnedTradeId> ownedTradeIds, boolean andDisplay)
    {
        this.ownedTradeIds = ownedTradeIds;
        rePurposeAdapter();

        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayHeader();
        displayActionBarTitle();
    }

    public void displayHeader()
    {
        if (this.header != null)
        {
            if (this.positionDTO != null)
            {
                header.bindOwnedPositionId(this.positionDTO);
            }
        }
    }

    public void displayActionBarTitle()
    {
        ActionBar actionBarCopy = this.actionBar;
        if (actionBarCopy != null)
        {
            if (positionDTO == null || securityIdCache.get().get(new SecurityIntegerId(positionDTO.securityId)) == null)
            {
                actionBarCopy.setTitle(R.string.trade_list_title);
            }
            else
            {
                SecurityId securityId = securityIdCache.get().get(new SecurityIntegerId(positionDTO.securityId));
                if (securityId == null)
                {
                    actionBarCopy.setTitle(R.string.trade_list_title);
                }
                else
                {
                    SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityId);
                    if (securityCompactDTO == null || securityCompactDTO.name == null)
                    {
                        actionBarCopy.setTitle(
                                String.format(getString(R.string.trade_list_title_with_security), securityId.exchange, securityId.securitySymbol));
                    }
                    else
                    {
                        actionBarCopy.setTitle(securityCompactDTO.name);
                    }
                }
            }
        }
    }

    public void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    protected TradeListCache.Listener<OwnedPositionId, OwnedTradeIdList> createTradeListeCacheListener()
    {
        return new GetTradesListener();
    }

    private class GetTradesListener implements TradeListCache.Listener<OwnedPositionId, OwnedTradeIdList>
    {
        @Override public void onDTOReceived(OwnedPositionId key, OwnedTradeIdList ownedTradeIds, boolean fromCache)
        {
            displayProgress(false);
            linkWith(ownedTradeIds, true);
        }

        @Override public void onErrorThrown(OwnedPositionId key, Throwable error)
        {
            displayProgress(false);
            THToast.show(R.string.error_fetch_trade_list_info);
            Timber.e("Error fetching the list of trades %s", key, error);
        }
    }
}
