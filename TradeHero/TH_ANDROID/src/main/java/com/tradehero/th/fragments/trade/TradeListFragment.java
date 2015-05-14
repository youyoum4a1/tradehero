package com.tradehero.th.fragments.trade;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionDTOKeyFactory;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.SecurityActionListLinear;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Routable("user/:userId/portfolio/:portfolioId/position/:positionId")
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
    @Inject THRouter thRouter;
    @Inject WatchlistPositionCache watchlistPositionCache;

    @InjectView(android.R.id.empty) protected ProgressBar progressBar;
    @InjectView(R.id.trade_list) protected ListView tradeListView;

    @RouteProperty("userId") Integer routeUserId;
    @RouteProperty("portfolioId") Integer routePortfolioId;
    @RouteProperty("positionId") Integer routePositionId;

    protected PositionDTOKey positionDTOKey;
    protected DTOCacheNew.Listener<PositionDTOKey, PositionDTO> fetchPositionListener;
    protected PositionDTO positionDTO;
    protected TradeDTOList tradeDTOList;

    protected TradeListItemAdapter adapter;

    private DTOCacheNew.Listener<OwnedPositionId, TradeDTOList> fetchTradesListener;
    private Dialog securityActionDialog;

    public static void putPositionDTOKey(@NotNull Bundle args, @NotNull PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    @NotNull private static PositionDTOKey getPositionDTOKey(@NotNull Bundle args, @NotNull PositionDTOKeyFactory positionDTOKeyFactory)
    {
        return positionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        if (getArguments() != null && routeUserId != null && routePortfolioId != null && routePositionId != null)
        {
            putPositionDTOKey(getArguments(), new OwnedPositionId(routeUserId, routePortfolioId, routePositionId));
        }

        fetchPositionListener = createPositionCacheListener();
        fetchTradesListener = createTradeListeCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_trade_list, container, false);

        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        ButterKnife.inject(this, view);

        if (view != null)
        {
            createAdapter();

            if (tradeListView != null)
            {
                tradeListView.setAdapter(adapter);
            }
        }
    }
    @Override public void onResume()
    {
        super.onResume();
        linkWith(getPositionDTOKey(getArguments(), positionDTOKeyFactory), true);
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        detachFetchPosition();
        detachFetchTrades();
        detachSecurityActionDialog();
        adapter = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        fetchPositionListener = null;
        fetchTradesListener = null;
        super.onDestroy();
    }

    protected void createAdapter()
    {
        adapter = new TradeListItemAdapter(getActivity(), getActivity().getLayoutInflater());
    }

    protected void rePurposeAdapter()
    {
        if (this.positionDTO != null && this.tradeDTOList != null)
        {
            createAdapter();
            adapter.setShownPositionDTO(positionDTO);
            adapter.setUnderlyingItems(createUnderlyingItems());
            if (tradeListView != null)
            {
                tradeListView.setAdapter(adapter);
            }
        }
    }

    protected void detachSecurityActionDialog()
    {
        if (securityActionDialog != null)
        {
            android.view.Window window = securityActionDialog.getWindow();
            if (window != null)
            {
                View decorView = window.getDecorView();
                if (decorView != null)
                {
                    View innerView = decorView.findViewById(android.R.id.content);
                    if (innerView instanceof SecurityActionListLinear)
                    {
                        ((SecurityActionListLinear) innerView).setMenuClickedListener(null);
                    }
                }
            }
            securityActionDialog.dismiss();
        }
        securityActionDialog = null;
    }

    protected List<PositionTradeDTOKey> createUnderlyingItems()
    {
        List<PositionTradeDTOKey> created = new ArrayList<>();
        for (TradeDTO tradeDTO : tradeDTOList)
        {
            created.add(new PositionTradeDTOKey(positionDTOKey, tradeDTO));
        }
        return created;
    }

    protected void detachFetchPosition()
    {
        positionCache.get().unregister(fetchPositionListener);
    }

    protected void detachFetchTrades()
    {
        tradeListCache.get().unregister(fetchTradesListener);
    }

    public void linkWith(@NotNull PositionDTOKey newPositionDTOKey, boolean andDisplay)
    {
        this.positionDTOKey = newPositionDTOKey;
        fetchPosition();

        if (andDisplay)
        {
            display();
        }
    }

    protected void fetchPosition()
    {
        detachFetchPosition();
        positionCache.get().register(positionDTOKey, fetchPositionListener);
        positionCache.get().getOrFetchAsync(positionDTOKey);
    }

    protected DTOCacheNew.Listener<PositionDTOKey, PositionDTO> createPositionCacheListener()
    {
        return new TradeListFragmentPositionCacheListener();
    }

    protected class TradeListFragmentPositionCacheListener implements DTOCacheNew.Listener<PositionDTOKey, PositionDTO>
    {
        @Override public void onDTOReceived(@NotNull PositionDTOKey key, @NotNull PositionDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull PositionDTOKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_position_list_info);
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        rePurposeAdapter();
        fetchTrades();
        if (andDisplay)
        {
        }
    }

    protected void fetchTrades()
    {
        if (positionDTO != null)
        {
            detachFetchTrades();
            OwnedPositionId key = positionDTO.getOwnedPositionId();
            tradeListCache.get().register(key, fetchTradesListener);
            tradeListCache.get().getOrFetchAsync(key);
            displayProgress(true);
        }
    }

    protected TradeListCache.Listener<OwnedPositionId, TradeDTOList> createTradeListeCacheListener()
    {
        return new GetTradesListener();
    }

    private class GetTradesListener implements TradeListCache.Listener<OwnedPositionId, TradeDTOList>
    {
        @Override public void onDTOReceived(@NotNull OwnedPositionId key, @NotNull TradeDTOList tradeDTOs)
        {
            displayProgress(false);
            linkWith(tradeDTOs, true);
        }

        @Override public void onErrorThrown(@NotNull OwnedPositionId key, @NotNull Throwable error)
        {
            displayProgress(false);
            THToast.show(R.string.error_fetch_trade_list_info);
            Timber.e("Error fetching the list of trades %s", key, error);
        }
    }

    public void linkWith(TradeDTOList tradeDTOs, boolean andDisplay)
    {
        this.tradeDTOList = tradeDTOs;
        rePurposeAdapter();

        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayActionBarTitle();
    }

    @Nullable protected SecurityId getSecurityId()
    {
        if (positionDTO == null)
        {
            return null;
        }
        return securityIdCache.get().get(new SecurityIntegerId(positionDTO.securityId));
    }

    public void displayActionBarTitle()
    {
        SecurityId securityId = getSecurityId();
        if (securityId == null)
        {
            setActionBarTitle(R.string.trade_list_title);
        }
        else
        {
            SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityId);
            if (securityCompactDTO == null || securityCompactDTO.name == null)
            {
                setActionBarTitle(
                        String.format(getString(R.string.trade_list_title_with_security), securityId.getExchange(),
                                securityId.getSecuritySymbol()));
            }
            else
            {
                setActionBarTitle(securityCompactDTO.name);
                setActionBarSubtitle(String.format(getString(R.string.trade_list_title_with_security), securityId.getExchange(),
                        securityId.getSecuritySymbol()));
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

    protected SecurityActionListLinear.OnActionMenuClickedListener createSecurityActionMenuListener()
    {
        return new TradeListSecurityActionListener();
    }

    protected class TradeListSecurityActionListener implements SecurityActionListLinear.OnActionMenuClickedListener
    {
        @Override public void onCancelClicked()
        {
            dismissShareDialog();
        }

        @Override public void onAddToWatchlistRequested(@NotNull SecurityId securityId)
        {
            dismissShareDialog();
            Bundle args = new Bundle();
            WatchlistEditFragment.putSecurityId(args, securityId);
            if (watchlistPositionCache.get(securityId) != null)
            {
                args.putString(DashboardFragment.BUNDLE_KEY_TITLE, getString(R.string.watchlist_edit_title));
            }
            else
            {
                args.putString(DashboardFragment.BUNDLE_KEY_TITLE, getString(R.string.watchlist_add_title));
            }
            DashboardNavigator navigator = getDashboardNavigator();
            if (navigator != null)
            {
                navigator.pushFragment(WatchlistEditFragment.class, args);
            }
        }

        @Override public void onAddAlertRequested(@NotNull SecurityId securityId)
        {

        }

        @Override public void onBuySellRequested(@NotNull SecurityId securityId)
        {
        }
    }

    protected void dismissShareDialog()
    {
        Dialog securityActionDialogCopy = securityActionDialog;
        if (securityActionDialogCopy != null)
        {
            securityActionDialogCopy.dismiss();
        }
        securityActionDialog = null;
    }
}
