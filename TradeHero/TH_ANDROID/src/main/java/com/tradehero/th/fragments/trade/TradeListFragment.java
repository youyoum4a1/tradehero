package com.tradehero.th.fragments.trade;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionDTOKeyFactory;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditDialogFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.models.parcelable.LiveBuySellParcelable;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.PositionCacheRx;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "user/:userId/portfolio/:portfolioId/position/:positionId",
        "user/:userId/portfolio/:portfolioId/position/:positionId/trade/:tradeId"
})
public class TradeListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = TradeListFragment.class.getName() + ".positionDTOKey";
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            TradeListFragment.class.getName() + ".purchaseApplicablePortfolioId";

    @Inject PositionCacheRx positionCache;
    @Inject TradeListCacheRx tradeListCache;
    @Inject SecurityIdCache securityIdCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject Analytics analytics;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;

    @Bind(R.id.trade_list) protected RecyclerView tradeListView;
    @Bind(R.id.btn_trade_now) protected View buttonTrade;
    protected StockActionBarRelativeLayout actionBarLayout;

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
    @Nullable protected WatchlistPositionDTOList watchedList;
    @Nullable protected TradeDTOList tradeDTOs;
    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    protected TradesRecyclerAdapter adapter;

    public static void putPositionDTOKey(@NonNull Bundle args, @NonNull PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    @NonNull
    private static PositionDTOKey getPositionDTOKey(@NonNull Bundle args)
    {
        return PositionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
    }

    public static void putApplicablePortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    @Nullable public static OwnedPortfolioId getApplicablePortfolioId(@NonNull Bundle args)
    {
        Bundle portfolioBundle = args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
        if (portfolioBundle != null)
        {
            return new OwnedPortfolioId(portfolioBundle);
        }
        return null;
    }

    public TradeListFragment()
    {
        this.prettyTime = new PrettyTime();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        adapter = new TradesRecyclerAdapter();
        adapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<Object>()
        {
            @Override public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<Object> viewHolder, Object object)
            {
                if (object instanceof TradeDisplayDTO)
                {
                    ((TradeDisplayDTO) object).togglePrettyDate();
                    adapter.notifyItemChanged(position);
                }
            }
        });
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
        this.purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_trade_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tradeListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tradeListView.addItemDecoration(new TypedRecyclerAdapter.DividerItemDecoration(getActivity()));
        tradeListView.setAdapter(adapter);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchAll();
    }

    @Override public void onResume()
    {
        super.onResume();
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                buttonTrade.setTranslationY(y);
            }
        });
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        actionBarLayout = (StockActionBarRelativeLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.trade_list_custom_actionbar, null);

        onDestroyOptionsMenuSubscriptions.add(actionBarLayout.getUserActionObservable()
                .subscribe(
                        new Action1<StockActionBarRelativeLayout.UserAction>()
                        {
                            @Override public void call(StockActionBarRelativeLayout.UserAction userAction)
                            {
                                if (userAction instanceof StockActionBarRelativeLayout.WatchlistUserAction)
                                {
                                    handleAddToWatchlistRequested((StockActionBarRelativeLayout.WatchlistUserAction) userAction);
                                }
                                else if (userAction instanceof StockActionBarRelativeLayout.UpdateAlertUserAction)
                                {
                                    handleUpdateAlertRequested((StockActionBarRelativeLayout.UpdateAlertUserAction) userAction);
                                }
                                else if (userAction instanceof StockActionBarRelativeLayout.CreateAlertUserAction)
                                {
                                    handleAddAlertRequested((StockActionBarRelativeLayout.CreateAlertUserAction) userAction);
                                }
                                else
                                {
                                    throw new IllegalArgumentException("Unhandled argument UserAction." + userAction);
                                }
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to handle action bar")));
        actionBar.setCustomView(actionBarLayout);
        displayActionBar();
    }

    @Override public void onPause()
    {
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        tradeListView.setOnScrollListener(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        adapter = null;
        super.onDetach();
    }

    protected void fetchAll()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this,
                Observable.combineLatest(
                        userWatchlistPositionCache.getOne(currentUserId.toUserBaseKey())
                                .subscribeOn(Schedulers.computation())
                                .map(new PairGetSecond<UserBaseKey, WatchlistPositionDTOList>())
                                .startWith(watchedList)
                                .observeOn(AndroidSchedulers.mainThread()),
                        alertCompactListCache.getOneSecurityMappedAlerts(currentUserId.toUserBaseKey())
                                .subscribeOn(Schedulers.computation())
                                .startWith(mappedAlerts)
                                .observeOn(AndroidSchedulers.mainThread()),
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
                                .observeOn(AndroidSchedulers.mainThread()),
                        new Func3<WatchlistPositionDTOList, Map<SecurityId, AlertCompactDTO>, PositionDTO, PositionDTO>()
                        {
                            @Override public PositionDTO call(WatchlistPositionDTOList watchlistPositionDTOs,
                                    Map<SecurityId, AlertCompactDTO> alerts,
                                    PositionDTO positionDTO)
                            {
                                watchedList = watchlistPositionDTOs;
                                mappedAlerts = alerts;
                                displayActionBar();
                                return positionDTO;
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
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
                                                    public Observable<SecurityCompactDTO> call(SecurityId securityId)
                                                    {
                                                        TradeListFragment.this.securityId = securityId;
                                                        return securityCompactCache.getOne(securityId)
                                                                .subscribeOn(Schedulers.computation())
                                                                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                                                                .startWith(securityCompactDTO != null ? Observable.just(securityCompactDTO)
                                                                        : Observable.<SecurityCompactDTO>empty());
                                                    }
                                                }),
                                        Observable.just(pDTO.getOwnedPositionId())
                                                .distinctUntilChanged()
                                                .delay(200,
                                                        TimeUnit.MILLISECONDS) // crappy HACK to prevent the Retrofit thread for trades from being interrupted
                                                .flatMap(new Func1<OwnedPositionId, Observable<TradeDTOList>>()
                                                {
                                                    @Override public Observable<TradeDTOList> call(OwnedPositionId ownedPositionId)
                                                    {
                                                        return tradeListCache.get(ownedPositionId)
                                                                .map(new PairGetSecond<OwnedPositionId, TradeDTOList>())
                                                                .startWith(tradeDTOs)
                                                                .distinctUntilChanged();
                                                    }
                                                }),
                                        new Func2<SecurityCompactDTO, TradeDTOList, List<Object>>()
                                        {
                                            @Override public List<Object> call(SecurityCompactDTO scDTO, @Nullable TradeDTOList tradeDTOs)
                                            {
                                                TradeListFragment.this.tradeDTOs = tradeDTOs;
                                                securityCompactDTO = scDTO;
                                                List<Object> objects = TradesRecyclerAdapter.createObjects(
                                                        getResources(),
                                                        pDTO,
                                                        scDTO,
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
                                adapter.addAll(o);
                                displayBuySellContainer();
                                displayActionBar();
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to load all")));
    }

    public void displayBuySellContainer()
    {
        if (securityCompactDTO != null && buttonTrade.getVisibility() == View.GONE)
        {
            Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
            slideIn.setFillAfter(true);
            buttonTrade.setVisibility(View.VISIBLE);
            buttonTrade.startAnimation(slideIn);
        }
    }

    protected void displayActionBar()
    {
        if (actionBarLayout != null && securityId != null)
        {
            actionBarLayout.display(new StockActionBarRelativeLayout.Requisite(
                    securityId,
                    securityCompactDTO,
                    watchedList,
                    mappedAlerts));
        }
    }

    protected void handleAddToWatchlistRequested(@NonNull StockActionBarRelativeLayout.WatchlistUserAction userAction)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, userAction.securityId);
        if (userAction.add)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
            ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_add_title));
        }
        else
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
            ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_edit_title));
        }
        if (navigator != null)
        {
            navigator.get().pushFragment(WatchlistEditFragment.class, args);
        }
    }

    protected void handleUpdateAlertRequested(@NonNull StockActionBarRelativeLayout.UpdateAlertUserAction userAction)
    {
        AlertEditDialogFragment.newInstance(userAction.alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()))
                .show(getFragmentManager(), AlertEditDialogFragment.class.getName());
    }

    protected void handleAddAlertRequested(@NonNull StockActionBarRelativeLayout.CreateAlertUserAction userAction)
    {
        AlertCreateDialogFragment.newInstance(userAction.securityId)
                .show(getFragmentManager(), BaseAlertEditDialogFragment.class.getName());
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_trade_now)
    protected void handleButtonSellClicked(View view)
    {
        if (securityId == null)
        {
            Timber.e(new NullPointerException(), "We should not have reached here");
        }
        else
        {
            if (isLiveTrading.get())
            {
                LiveBuySellParcelable liveBuySellParcelable =
                        new LiveBuySellParcelable(securityId, 0);

                getActivity().getIntent().putExtra("LiveBuySellParcelable", liveBuySellParcelable);
                navigator.get().pushFragment(LiveBuySellFragment.class);
            }
            else
            {
                Bundle args = new Bundle();
                AbstractBuySellFragment.putRequisite(
                        args,
                        purchaseApplicableOwnedPortfolioId != null
                                ? new AbstractBuySellFragment.Requisite(
                                securityId,
                                purchaseApplicableOwnedPortfolioId,
                                0)
                                : new AbstractBuySellFragment.Requisite(
                                        securityId,
                                        new Bundle(),
                                        portfolioCompactListCache,
                                        currentUserId));
                navigator.get().pushFragment(SecurityCompactDTOUtil.fragmentFor(securityCompactDTO), args);
            }
        }
    }

    @Override public boolean shouldHandleLiveColor()
    {
        return true;
    }
}
