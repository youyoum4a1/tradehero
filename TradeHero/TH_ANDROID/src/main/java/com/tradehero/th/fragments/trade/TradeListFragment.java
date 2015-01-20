package com.tradehero.th.fragments.trade;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
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
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
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
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

@Routable("user/:userId/portfolio/:portfolioId/position/:positionId")
public class TradeListFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = TradeListFragment.class.getName() + ".positionDTOKey";

    @Inject PositionCacheRx positionCache;
    @Inject TradeListCacheRx tradeListCache;
    @Inject SecurityIdCache securityIdCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject PositionDTOKeyFactory positionDTOKeyFactory;
    @Inject THRouter thRouter;
    @Inject WatchlistPositionCacheRx watchlistPositionCache;
    @Inject Analytics analytics;
    @Inject SecurityActionDialogFactory securityActionDialogFactory;
    @Inject SecurityCompactDTOUtil securityCompactDTOUtil;

    @InjectView(R.id.trade_list) protected ListView tradeListView;

    @RouteProperty("userId") Integer routeUserId;
    @RouteProperty("portfolioId") Integer routePortfolioId;
    @RouteProperty("positionId") Integer routePositionId;

    @NonNull protected PositionDTOKey positionDTOKey;
    @Nullable protected Subscription positionSubscription;
    @Nullable protected PositionDTO positionDTO;
    @Nullable protected Subscription tradesSubscription;
    @Nullable protected TradeDTOList tradeDTOList;
    @Nullable protected Subscription alertsSubscription;
    @Nullable protected Map<SecurityId, AlertId> mappedAlerts;
    @Nullable protected Subscription securityIdSubscription;
    @Nullable protected SecurityId securityId;
    @Nullable protected Subscription securityCompactSubscription;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected Subscription actionDialogSubscription;

    protected TradeListItemAdapter adapter;

    private Dialog securityActionDialog;

    public static void putPositionDTOKey(@NonNull Bundle args, @NonNull PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    @NonNull
    private static PositionDTOKey getPositionDTOKey(@NonNull Bundle args, @NonNull PositionDTOKeyFactory positionDTOKeyFactory)
    {
        return positionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        if (routeUserId != null && routePortfolioId != null && routePositionId != null)
        {
            positionDTOKey = new OwnedPositionId(routeUserId, routePortfolioId, routePositionId);
        }
        else
        {
            positionDTOKey = getPositionDTOKey(getArguments(), positionDTOKeyFactory);
        }
        adapter = createAdapter();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_trade_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        tradeListView.setAdapter(adapter);
        tradeListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchAlertList();
        fetchPosition();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.trade_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.btn_security_action).setVisible(shouldActionBeVisible());
        super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_security_action:
                handleActionButtonClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyOptionsMenu()
    {
        unsubscribe(securityCompactSubscription);
        securityCompactSubscription = null;
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onStop()
    {
        unsubscribe(alertsSubscription);
        alertsSubscription = null;
        unsubscribe(positionSubscription);
        positionSubscription = null;
        unsubscribe(tradesSubscription);
        tradesSubscription = null;
        unsubscribe(securityIdSubscription);
        securityIdSubscription = null;
        unsubscribe(securityCompactSubscription);
        securityCompactSubscription = null;
        unsubscribe(actionDialogSubscription);
        actionDialogSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        tradeListView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        adapter = null;
        super.onDestroy();
    }

    protected TradeListItemAdapter createAdapter()
    {
        return new TradeListItemAdapter(getActivity());
    }

    protected void fetchAlertList()
    {
        if (alertsSubscription == null)
        {
            alertsSubscription = AndroidObservable.bindFragment(
                    this,
                    alertCompactListCache.getSecurityMappedAlerts(currentUserId.toUserBaseKey()))
                    .subscribe(
                            this::onAlertMapReceived,
                            error -> Timber.e(error, ""));
        }
    }

    protected void onAlertMapReceived(@NonNull Map<SecurityId, AlertId> securityIdAlertIdMap)
    {
        mappedAlerts = securityIdAlertIdMap;
        getActivity().invalidateOptionsMenu();
    }

    protected void fetchPosition()
    {
        if (positionSubscription == null)
        {
            positionSubscription = AndroidObservable.bindFragment(this, positionCache.get(positionDTOKey))
                    .map(pair -> pair.second)
                    .subscribe(
                            this::linkWith,
                            error -> THToast.show(R.string.error_fetch_position_list_info));
        }
    }

    public void linkWith(PositionDTO positionDTO)
    {
        this.positionDTO = positionDTO;
        adapter.setShownPositionDTO(positionDTO);
        adapter.notifyDataSetChanged();
        fetchTrades();
        softFetchSecurityId();
    }

    protected void fetchTrades()
    {
        if (positionDTO != null && tradesSubscription == null)
        {
            tradesSubscription = AndroidObservable.bindFragment(this, tradeListCache.get(positionDTO.getOwnedPositionId()))
                    .map(pair -> pair.second)
                    .subscribe(
                            this::linkWith,
                            error -> {
                                THToast.show(R.string.error_fetch_trade_list_info);
                                Timber.e("Error fetching the list of trades", error);
                            });
        }
    }

    public void linkWith(TradeDTOList tradeDTOs)
    {
        this.tradeDTOList = tradeDTOs;
        adapter.setUnderlyingItems(createUnderlyingItems(tradeDTOs));
        adapter.notifyDataSetChanged();
    }

    protected List<PositionTradeDTOKey> createUnderlyingItems(@NonNull List<TradeDTO> tradeDTOList)
    {
        List<PositionTradeDTOKey> created = new ArrayList<>();
        for (TradeDTO tradeDTO : tradeDTOList)
        {
            created.add(new PositionTradeDTOKey(positionDTOKey, tradeDTO));
        }
        return created;
    }

    protected void softFetchSecurityId()
    {
        if (positionDTO != null)
        {
            SecurityId cachedSecurityId = securityIdCache.getCachedValue(new SecurityIntegerId(positionDTO.securityId));
            if (cachedSecurityId != null)
            {
                linkWith(cachedSecurityId);
            }
            else if (securityIdSubscription == null)
            {
                securityIdSubscription = AndroidObservable.bindFragment(
                        this,
                        securityIdCache.get(new SecurityIntegerId(positionDTO.securityId)))
                        .map(pair -> pair.second)
                        .subscribe(
                                this::linkWith,
                                error -> {
                                });
            }
        }
    }

    protected void linkWith(@NonNull SecurityId securityId)
    {
        this.securityId = securityId;
        fetchSecurityCompact();
    }

    protected void fetchSecurityCompact()
    {
        if (securityId != null && securityCompactSubscription == null)
        {
            securityCompactSubscription = AndroidObservable.bindFragment(this, securityCompactCache.get(securityId))
                    .map(pair -> pair.second)
                    .subscribe(
                            this::linkWith,
                            error -> THToast.show(new THException(error)));
        }
    }

    protected void linkWith(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        getActivity().invalidateOptionsMenu();
        displayActionBarTitle();
    }

    public void displayActionBarTitle()
    {
        SecurityId securityId = this.securityId;
        if (securityCompactDTO != null)
        {
            securityId = securityCompactDTO.getSecurityId();
        }
        FxPairSecurityId fxPairSecurityId = null;
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            fxPairSecurityId = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
        }

        if (fxPairSecurityId != null)
        {
            setActionBarTitle(String.format("%s/%s", fxPairSecurityId.left, fxPairSecurityId.right));
            setActionBarSubtitle(null);
        }
        else if (securityId != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.name == null)
            {
                setActionBarTitle(
                        String.format(getString(R.string.trade_list_title_with_security), securityId.getExchange(),
                                securityId.getSecuritySymbol()));
                setActionBarSubtitle(null);
            }
            else
            {
                setActionBarTitle(securityCompactDTO.name);
                setActionBarSubtitle(String.format(getString(R.string.trade_list_title_with_security), securityId.getExchange(),
                        securityId.getSecuritySymbol()));
            }
        }
    }

    protected boolean shouldActionBeVisible()
    {
        return mappedAlerts != null && securityCompactDTO != null;
    }

    protected void handleActionButtonClicked()
    {
        if (securityCompactDTO == null || mappedAlerts == null)
        {
            throw new IllegalStateException("We should not allow entering here");
        }
        else
        {
            unsubscribe(actionDialogSubscription);
            Pair<Dialog, SecurityActionListLinear> pair = securityActionDialogFactory.createSecurityActionDialog(getActivity(), securityCompactDTO);
            securityActionDialog = pair.first;
            actionDialogSubscription = pair.second.getMenuActionObservable()
                    .subscribe(
                            this::handleMenuAction,
                            error -> Timber.e(error, ""));
        }
    }

    protected void handleMenuAction(@NonNull SecurityActionListLinear.MenuAction menuAction)
    {
        dismissShareDialog();
        switch (menuAction.actionType)
        {
            case CANCEL:
                break;
            case ADD_TO_WATCHLIST:
                handleAddToWatchlistRequested(menuAction.securityCompactDTO);
                break;
            case ADD_ALERT:
                handleAddAlertRequested(menuAction.securityCompactDTO);
                break;
            case BUY_SELL:
                handleBuySellRequested(menuAction.securityCompactDTO);
                break;
            default:
                throw new IllegalArgumentException("Unhandled SecurityActionListLinear.MenuActionType." + menuAction.actionType);
        }
    }

    protected void handleAddToWatchlistRequested(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
        if (watchlistPositionCache.getCachedValue(securityCompactDTO.getSecurityId()) != null)
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

    protected void handleAddAlertRequested(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            BaseAlertEditFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        if (mappedAlerts != null)
        {
            AlertId alertId = mappedAlerts.get(securityCompactDTO.getSecurityId());
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
                AlertCreateFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
                if (navigator != null)
                {
                    navigator.get().pushFragment(AlertCreateFragment.class, args);
                }
            }
        }
    }

    protected void handleBuySellRequested(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        if (positionDTO != null && positionDTO.getOwnedPortfolioId() != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, positionDTO.getOwnedPortfolioId());
        }
        BuySellFragment.putSecurityId(args, securityCompactDTO.getSecurityId());

        if (navigator != null)
        {
            // TODO add command to go direct to pop-up
            navigator.get().pushFragment(securityCompactDTOUtil.fragmentFor(securityCompactDTO), args);
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
