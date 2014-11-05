package com.tradehero.th.fragments.trade;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
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
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.security.SecurityActionDialogFactory;
import com.tradehero.th.fragments.security.SecurityActionListLinear;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.position.PositionCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

@Routable("user/:userId/portfolio/:portfolioId/position/:positionId")
public class TradeListFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = TradeListFragment.class.getName() + ".positionDTOKey";

    @Inject Lazy<PositionCacheRx> positionCache;
    @Inject Lazy<TradeListCacheRx> tradeListCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCacheRx> securityCompactCache;
    @Inject Lazy<AlertCompactListCacheRx> alertCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject PositionDTOKeyFactory positionDTOKeyFactory;
    @Inject THRouter thRouter;
    @Inject WatchlistPositionCache watchlistPositionCache;
    @Inject Analytics analytics;
    SecurityActionDialogFactory securityActionDialogFactory = new SecurityActionDialogFactory(); // no inject, 65k

    @InjectView(android.R.id.empty) protected ProgressBar progressBar;
    @InjectView(R.id.trade_list) protected ListView tradeListView;

    @RouteProperty("userId") Integer routeUserId;
    @RouteProperty("portfolioId") Integer routePortfolioId;
    @RouteProperty("positionId") Integer routePositionId;

    protected PositionDTOKey positionDTOKey;
    @Nullable protected PositionDTO positionDTO;
    @Nullable protected TradeDTOList tradeDTOList;
    @Nullable private Map<SecurityId, AlertId> mappedAlerts;

    protected TradeListItemAdapter adapter;

    private Dialog securityActionDialog;

    public static void putPositionDTOKey(@NonNull Bundle args, @NonNull PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    @NonNull private static PositionDTOKey getPositionDTOKey(@NonNull Bundle args, @NonNull PositionDTOKeyFactory positionDTOKeyFactory)
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
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_trade_list, container, false);

        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);

        if (view != null)
        {
            createAdapter();

            if (tradeListView != null)
            {
                tradeListView.setAdapter(adapter);
                tradeListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.trade_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_security_action:
                if (mappedAlerts != null)
                {
                    handleActionButtonClicked();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(getPositionDTOKey(getArguments(), positionDTOKeyFactory), true);
        AndroidObservable.bindFragment(
                this,
                alertCompactListCache.get().getSecurityMappedAlerts(currentUserId.toUserBaseKey()))
                .subscribe(new Observer<Map<SecurityId, AlertId>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Map<SecurityId, AlertId> securityIdAlertIdMap)
                    {
                        mappedAlerts = securityIdAlertIdMap;
                    }
                });
    }

    @Override public void onDestroyView()
    {
        setActionBarSubtitle(null);
        detachSecurityActionDialog();
        adapter = null;
        tradeListView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
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

    public void linkWith(@NonNull PositionDTOKey newPositionDTOKey, boolean andDisplay)
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
        AndroidObservable.bindFragment(this, positionCache.get().get(positionDTOKey))
                .subscribe(createPositionCacheObserver());
    }

    protected Observer<Pair<PositionDTOKey, PositionDTO>> createPositionCacheObserver()
    {
        return new TradeListFragmentPositionCacheObserver();
    }

    protected class TradeListFragmentPositionCacheObserver implements Observer<Pair<PositionDTOKey, PositionDTO>>
    {
        @Override public void onNext(Pair<PositionDTOKey, PositionDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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
            displayProgress(true);
            OwnedPositionId key = positionDTO.getOwnedPositionId();
            AndroidObservable.bindFragment(this, tradeListCache.get().get(key))
                    .subscribe(createTradeListeCacheObserver());
        }
    }

    protected Observer<Pair<OwnedPositionId, TradeDTOList>> createTradeListeCacheObserver()
    {
        return new GetTradesObserver();
    }

    private class GetTradesObserver implements Observer<Pair<OwnedPositionId, TradeDTOList>>
    {
        @Override public void onNext(Pair<OwnedPositionId, TradeDTOList> pair)
        {
            displayProgress(false);
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            displayProgress(false);
            THToast.show(R.string.error_fetch_trade_list_info);
            Timber.e("Error fetching the list of trades", e);
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
        return securityIdCache.get().get(new SecurityIntegerId(positionDTO.securityId))
                .toBlocking()
                .firstOrDefault(Pair.create((SecurityIntegerId) null, (SecurityId) null))
                .second;
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
            AndroidObservable.bindFragment(this, securityCompactCache.get().get(securityId))
                    .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                            THToast.show(new THException(e));
                        }

                        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                        {
                            if (pair.second.name == null)
                            {
                                setActionBarTitle(
                                        String.format(getString(R.string.trade_list_title_with_security), securityId.getExchange(),
                                                securityId.getSecuritySymbol()));
                            }
                            else
                            {
                                setActionBarTitle(pair.second.name);
                                setActionBarSubtitle(String.format(getString(R.string.trade_list_title_with_security), securityId.getExchange(),
                                        securityId.getSecuritySymbol()));
                            }
                        }
                    });
        }
    }

    public void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    protected void handleActionButtonClicked()
    {
        SecurityId securityId = getSecurityId();
        if (securityId == null)
        {
            THToast.show(R.string.error_fetch_security_info);
        }
        else
        {
            detachSecurityActionDialog();
            securityActionDialog = securityActionDialogFactory.createSecurityActionDialog(getActivity(), securityId, createSecurityActionMenuListener());
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

        @Override public void onAddToWatchlistRequested(@NonNull SecurityId securityId)
        {
            dismissShareDialog();
            Bundle args = new Bundle();
            WatchlistEditFragment.putSecurityId(args, securityId);
            if (watchlistPositionCache.get(securityId) != null)
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_edit_title));
            }
            else
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_add_title));
            }
            if (navigator != null)
            {
                navigator.get().pushFragment(WatchlistEditFragment.class, args);
            }
        }

        @Override public void onAddAlertRequested(@NonNull SecurityId securityId)
        {
            dismissShareDialog();
            Bundle args = new Bundle();
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                BaseAlertEditFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
            if (mappedAlerts != null)
            {
                AlertId alertId = mappedAlerts.get(securityId);
                if (alertId != null)
                {
                    AlertEditFragment.putAlertId(args, alertId);
                    if (navigator != null)
                    {
                        navigator.get().pushFragment(AlertEditFragment.class, args);
                    }
                }
                else
                {
                    AlertCreateFragment.putSecurityId(args, securityId);
                    if (navigator != null)
                    {
                        navigator.get().pushFragment(AlertCreateFragment.class, args);
                    }
                }
            }
        }

        @Override public void onBuySellRequested(@NonNull SecurityId securityId)
        {
            dismissShareDialog();
            Bundle args = new Bundle();
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                BuySellFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
            BuySellFragment.putSecurityId(args, securityId);
            if (navigator != null)
            {
                navigator.get().pushFragment(BuySellFragment.class, args);
            }
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
