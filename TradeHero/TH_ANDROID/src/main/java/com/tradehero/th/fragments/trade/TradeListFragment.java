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
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
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
import com.tradehero.th.api.trade.TradeDTOListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditDialogFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.security.SecurityActionDialogFactory;
import com.tradehero.th.fragments.security.SecurityActionListLinear;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.position.PositionCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

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
    @Inject THRouter thRouter;
    @Inject WatchlistPositionCacheRx watchlistPositionCache;
    @Inject Analytics analytics;

    @InjectView(R.id.trade_list) protected ListView tradeListView;

    @RouteProperty("userId") Integer routeUserId;
    @RouteProperty("portfolioId") Integer routePortfolioId;
    @RouteProperty("positionId") Integer routePositionId;

    @NonNull protected PositionDTOKey positionDTOKey;
    @Nullable protected PositionDTO positionDTO;
    @Nullable protected TradeDTOList tradeDTOList;
    @Nullable protected Map<SecurityId, AlertCompactDTO> mappedAlerts;
    @Nullable protected SecurityId securityId;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected Subscription actionDialogSubscription;

    protected TradeListItemAdapter adapter;

    private Dialog securityActionDialog;

    public static void putPositionDTOKey(@NonNull Bundle args, @NonNull PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    @NonNull
    private static PositionDTOKey getPositionDTOKey(@NonNull Bundle args)
    {
        return PositionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
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
            positionDTOKey = getPositionDTOKey(getArguments());
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
        tradeListView.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchAlertList();
        fetchPosition();
        if (positionDTOKey instanceof OwnedPositionId)
        {
            fetchTrades((OwnedPositionId) positionDTOKey);
        }
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
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onStop()
    {
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
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                alertCompactListCache.getSecurityMappedAlerts(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Map<SecurityId, AlertCompactDTO>>()
                        {
                            @Override public void call(Map<SecurityId, AlertCompactDTO> map)
                            {
                                TradeListFragment.this.onAlertMapReceived(map);
                            }
                        },
                        new TimberOnErrorAction("")));
    }

    protected void onAlertMapReceived(@NonNull Map<SecurityId, AlertCompactDTO> securityIdAlertIdMap)
    {
        mappedAlerts = securityIdAlertIdMap;
        getActivity().invalidateOptionsMenu();
    }

    protected void fetchPosition()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                positionCache.get(positionDTOKey)
                        .flatMap(new Func1<Pair<PositionDTOKey, PositionDTO>, Observable<Pair<PositionDTO, SecurityCompactDTO>>>()
                        {
                            @Override public Observable<Pair<PositionDTO, SecurityCompactDTO>> call(
                                    final Pair<PositionDTOKey, PositionDTO> positionPair)
                            {
                                return securityIdCache.getOne(positionPair.second.getSecurityIntegerId())
                                        .flatMap(
                                                new Func1<Pair<SecurityIntegerId, SecurityId>, Observable<Pair<SecurityId, SecurityCompactDTO>>>()
                                                {
                                                    @Override
                                                    public Observable<Pair<SecurityId, SecurityCompactDTO>> call(
                                                            Pair<SecurityIntegerId, SecurityId> securityIntegerIdSecurityIdPair)
                                                    {
                                                        return securityCompactCache.getOne(securityIntegerIdSecurityIdPair.second);
                                                    }
                                                })
                                        .map(new Func1<Pair<SecurityId, SecurityCompactDTO>, Pair<PositionDTO, SecurityCompactDTO>>()
                                        {
                                            @Override public Pair<PositionDTO, SecurityCompactDTO> call(
                                                    Pair<SecurityId, SecurityCompactDTO> securityPair)
                                            {
                                                return Pair.create(positionPair.second, securityPair.second);
                                            }
                                        });
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<PositionDTO, SecurityCompactDTO>>()
                        {
                            @Override public void call(Pair<PositionDTO, SecurityCompactDTO> positionPair)
                            {
                                linkWith(positionPair);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_position_list_info))));
    }

    public void linkWith(Pair<PositionDTO, SecurityCompactDTO> positionPair)
    {
        this.positionDTO = positionPair.first;
        this.securityCompactDTO = positionPair.second;
        this.securityId = positionPair.second.getSecurityId();
        adapter.setShownPositionDTO(positionPair);
        adapter.notifyDataSetChanged();
        if (!(positionDTOKey instanceof OwnedPositionId))
        {
            fetchTrades(positionPair.first.getOwnedPositionId());
        }
        getActivity().invalidateOptionsMenu();
        displayActionBarTitle();
    }

    protected void fetchTrades(@NonNull TradeDTOListKey tradeDTOListKey)
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, tradeListCache.get(tradeDTOListKey))
                .map(new PairGetSecond<TradeDTOListKey, TradeDTOList>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<TradeDTOList>()
                        {
                            @Override public void call(TradeDTOList tradeList)
                            {
                                linkWith(tradeList);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_trade_list_info),
                                "Error fetching the list of trades")));
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
            Pair<Dialog, SecurityActionListLinear> pair = SecurityActionDialogFactory.createSecurityActionDialog(getActivity(), securityCompactDTO);
            securityActionDialog = pair.first;
            actionDialogSubscription = pair.second.getMenuActionObservable()
                    .subscribe(
                            new Action1<SecurityActionListLinear.MenuAction>()
                            {
                                @Override public void call(SecurityActionListLinear.MenuAction menuAction)
                                {
                                    TradeListFragment.this.handleMenuAction(menuAction);
                                }
                            },
                            new TimberOnErrorAction(""));
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
        if (mappedAlerts != null)
        {
            AlertCompactDTO alertDTO = mappedAlerts.get(securityCompactDTO.getSecurityId());

            BaseAlertEditDialogFragment dialog = null;
            if (alertDTO != null)
            {
                AlertId alertId = alertDTO.getAlertId(currentUserId.toUserBaseKey());
                dialog = AlertEditDialogFragment.newInstance(alertId);
            }
            else if (securityId != null)
            {
                dialog = AlertCreateDialogFragment.newInstance(securityId);
            }
            if (dialog != null)
            {
                dialog.show(getFragmentManager(), BaseAlertEditDialogFragment.class.getName());
            }
            else
            {
                THToast.show(R.string.error_incomplete_info_message);
            }
        }
        else
        {
            THToast.show(R.string.error_incomplete_info_message);
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
            navigator.get().pushFragment(SecurityCompactDTOUtil.fragmentFor(securityCompactDTO), args);
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
