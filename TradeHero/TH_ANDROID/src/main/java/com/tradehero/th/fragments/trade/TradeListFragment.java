package com.tradehero.th.fragments.trade;

import android.app.Activity;
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
import com.tradehero.th.api.trade.TradeDTOList;
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
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Routable({
        "user/:userId/portfolio/:portfolioId/position/:positionId",
        "user/:userId/portfolio/:portfolioId/position/:positionId/trade/:tradeId"
})
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
    @RouteProperty("tradeId") Integer tradeId;

    @NonNull final PrettyTime prettyTime;
    @NonNull protected PositionDTOKey positionDTOKey;
    @Nullable protected PositionDTO positionDTO;
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

    public TradeListFragment()
    {
        this.prettyTime = new PrettyTime();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        adapter = new TradeListItemAdapter(activity);
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
        fetchAll();
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

    @Override public void onDetach()
    {
        adapter = null;
        super.onDetach();
    }

    protected void fetchAlertList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                alertCompactListCache.getOneSecurityMappedAlerts(currentUserId.toUserBaseKey()))
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
        getActivity().supportInvalidateOptionsMenu();
    }

    protected void fetchAll()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this,
                        positionCache.getOne(positionDTOKey)
                                .subscribeOn(Schedulers.computation())
                                .map(new Func1<Pair<PositionDTOKey, PositionDTO>, PositionDTO>()
                                {
                                    @Override public PositionDTO call(Pair<PositionDTOKey, PositionDTO> positionDTOKeyPositionDTOPair)
                                    {
                                        positionDTO = positionDTOKeyPositionDTOPair.second;
                                        return positionDTOKeyPositionDTOPair.second;
                                    }
                                })
                                .startWith(positionDTO != null ? Observable.just(positionDTO) : Observable.<PositionDTO>empty())
                                .distinctUntilChanged()
                                .flatMap(new Func1<PositionDTO, Observable<List<Object>>>()
                                {
                                    @Override public Observable<List<Object>> call(final PositionDTO pDTO)
                                    {
                                        return Observable.combineLatest(
                                                Observable.just(pDTO.getSecurityIntegerId())
                                                        .distinctUntilChanged()
                                                        .flatMap(new Func1<SecurityIntegerId, Observable<SecurityId>>()
                                                        {
                                                            @Override public Observable<SecurityId> call(SecurityIntegerId securityIntegerId)
                                                            {
                                                                return securityIdCache.getOne(pDTO.getSecurityIntegerId())
                                                                        .subscribeOn(Schedulers.computation())
                                                                        .map(new PairGetSecond<SecurityIntegerId, SecurityId>());
                                                            }
                                                        })
                                                        .startWith(securityId != null ? Observable.just(securityId) : Observable.<SecurityId>empty())
                                                        .distinctUntilChanged()
                                                        .flatMap(new Func1<SecurityId, Observable<SecurityCompactDTO>>()
                                                        {
                                                            @Override
                                                            public Observable<SecurityCompactDTO> call(
                                                                    SecurityId securityId)
                                                            {
                                                                return securityCompactCache.getOne(securityId)
                                                                        .subscribeOn(Schedulers.computation())
                                                                        .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                                                                        .startWith(securityCompactDTO != null ? Observable.just(securityCompactDTO)
                                                                                : Observable.<SecurityCompactDTO>empty());
                                                            }
                                                        }),
                                                Observable.just(pDTO.getOwnedPositionId())
                                                        .distinctUntilChanged()
                                                        .delay(200, TimeUnit.MILLISECONDS) // crappy HACK to prevent the Retrofit thread for trades from being interrupted
                                                        .flatMap(new Func1<OwnedPositionId, Observable<TradeDTOList>>()
                                                        {
                                                            @Override public Observable<TradeDTOList> call(OwnedPositionId ownedPositionId)
                                                            {
                                                                return tradeListCache.get(ownedPositionId)
                                                                        .map(new PairGetSecond<OwnedPositionId, TradeDTOList>());
                                                            }
                                                        }),
                                                new Func2<SecurityCompactDTO, TradeDTOList, List<Object>>()
                                                {
                                                    @Override public List<Object> call(SecurityCompactDTO scDTO, TradeDTOList tradeDTOs)
                                                    {
                                                        securityCompactDTO = scDTO;
                                                        List<Object> objects = TradeListItemAdapter.createObjects(
                                                                getResources(),
                                                                pDTO,
                                                                scDTO,
                                                                tradeId,
                                                                tradeDTOs,
                                                                prettyTime);
                                                        tradeId = null; // It is a one-time-use field
                                                        return objects;
                                                    }
                                                });
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread()))
                        .subscribe(
                                new Action1<List<Object>>()
                                {
                                    @Override public void call(List<Object> o)
                                    {
                                        adapter.setNotifyOnChange(false);
                                        adapter.clear();
                                        adapter.addAll(o);
                                        adapter.notifyDataSetChanged();
                                        adapter.setNotifyOnChange(true);
                                        finishReceivingData();
                                    }
                                },
                                new ToastOnErrorAction()));
    }

    public void finishReceivingData()
    {
        if (securityCompactDTO != null)
        {
            this.securityId = securityCompactDTO.getSecurityId();
            getActivity().supportInvalidateOptionsMenu();
            displayActionBarTitle();
        }
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
