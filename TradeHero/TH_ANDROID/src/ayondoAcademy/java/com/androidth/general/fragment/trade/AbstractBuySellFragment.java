package com.androidth.general.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.internal.util.Predicate;
import com.androidth.general.R;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.OwnedPortfolioIdList;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioCompactDTOUtil;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.key.PortfolioCompactListKey;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOList;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.exception.THException;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.OnMovableBottomTranslateListener;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.androidth.general.fragments.position.TabbedPositionListFragment;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.security.SignatureContainer2;
import com.androidth.general.fragments.settings.AskForInviteDialogFragment;
import com.androidth.general.fragments.settings.SendLoveBroadcastSignal;
import com.androidth.general.fragments.trade.view.PortfolioSelectorView;
import com.androidth.general.fragments.tutorial.WithTutorial;
import com.androidth.general.models.portfolio.MenuOwnedPortfolioId;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.QuoteServiceWrapper;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.persistence.portfolio.OwnedPortfolioIdListCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.position.PositionListCacheRx;
import com.androidth.general.persistence.prefs.ShowAskForInviteDialog;
import com.androidth.general.persistence.prefs.ShowAskForReviewDialog;
import com.androidth.general.persistence.prefs.ShowMarketClosed;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.androidth.general.utils.DeviceUtil;
import com.androidth.general.utils.LiveConstants;
import com.androidth.general.utils.SecurityUtils;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.utils.route.THRouter;
import com.androidth.general.widget.OffOnViewSwitcherEvent;
import com.tradehero.route.RouteProperty;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

abstract public class AbstractBuySellFragment extends DashboardFragment
        implements WithTutorial
{
    private final static String BUNDLE_KEY_REQUISITE = AbstractBuySellFragment.class.getName() + ".requisite";

    public final static String BUNDLE_KEY_SECURITY_DTO = AbstractBuySellFragment.class.getName() + ".securityDTO";

    private final static long MILLISECOND_QUOTE_REFRESH = 30000;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;
    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected PositionListCacheRx positionCompactListCache;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject protected PortfolioCacheRx portfolioCache;
    @Inject protected OwnedPortfolioIdListCacheRx ownedPortfolioIdListCache;
    @Inject protected THRouter thRouter;
    @Inject @ShowMarketClosed protected TimingIntervalPreference showMarketClosedIntervalPreference;
    @Inject @ShowAskForReviewDialog protected TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog protected TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject protected BroadcastUtils broadcastUtils;
    @Inject RequestHeaders requestHeaders;

    @Bind(R.id.portfolio_selector_container) protected PortfolioSelectorView selectedPortfolioContainer;
    @Bind(R.id.quote_refresh_countdown) protected ProgressBar quoteRefreshProgressBar;
    @Bind(R.id.bottom_button) protected ViewGroup buySellBtnContainer;
    @Bind(R.id.btn_buy) protected Button buyBtn;
    @Bind(R.id.btn_sell) protected Button sellBtn;

    @RouteProperty("applicablePortfolioId")
    @Nullable protected Integer routedApplicablePortfolioId;
    protected Requisite requisite;
    @Nullable protected LiveQuoteDTO quoteDTO;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected PositionDTO closeablePositionDTO;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    @Nullable protected PortfolioCompactDTO defaultPortfolio;
    @Nullable protected OwnedPortfolioIdList applicableOwnedPortfolioIds;

    protected Animation progressAnimation;
    protected AbstractTransactionFragment abstractTransactionFragment;

    protected AbstractBuySellPopupDialogFragment abstractBuySellPopupDialogFragment;

    protected boolean poppedPortfolioChanged = false;
    private PublishSubject<Void> quoteRepeatSubject;
    private Observable<Void> quoteRepeatDelayedObservable;
    public Observable<LiveQuoteDTO> quoteObservable;
    public Observable<SecurityCompactDTO> securityObservable;

    private SignalRManager signalRManager;

    Subscription quoteSubscription;
    String topBarColor;
    private boolean isInCompetition;

    public static void putRequisite(@NonNull Bundle args, @NonNull Requisite requisite)
    {
        args.putBundle(BUNDLE_KEY_REQUISITE, requisite.getArgs());
    }

    @NonNull private static Requisite getRequisite(@NonNull Bundle args,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache,
            @NonNull CurrentUserId currentUserId)
    {
        Bundle requisiteBundle = args.getBundle(BUNDLE_KEY_REQUISITE);
        if (requisiteBundle == null)
        {
            throw new NullPointerException("Requisite need to be passed on");
        }
        return new Requisite(requisiteBundle, portfolioCompactListCache, currentUserId);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        if (routedApplicablePortfolioId != null)
        {
            Requisite.putApplicablePorfolioId(getArguments(), new OwnedPortfolioId(currentUserId.get(), routedApplicablePortfolioId));
        }
        if(getArguments().containsKey(BUNDLE_KEY_SECURITY_DTO)){
            securityCompactDTO = getArguments().getParcelable(BUNDLE_KEY_SECURITY_DTO);
        }

        requisite = createRequisite();
        quoteRepeatSubject = PublishSubject.create();
        quoteRepeatDelayedObservable = quoteRepeatSubject.delay(getMillisecondQuoteRefresh(), TimeUnit.MILLISECONDS);
    }
    @Override  public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        super.onLiveTradingChanged(event);
        if(LiveConstants.isInLiveMode) {
            int liveColor = getResources().getColor(R.color.general_red_live);
            buyBtn.setBackgroundColor(liveColor);
            sellBtn.setBackgroundColor(liveColor);
        }
        else {
            int virtualColor = getResources().getColor(R.color.general_brand_color);
            buyBtn.setBackgroundColor(virtualColor);
            sellBtn.setBackgroundColor(virtualColor);
        }
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setRetainInstance(true);

        if(getArguments().containsKey(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR)){
            topBarColor = getArguments().getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR);
            isInCompetition = true;
        }else{
            topBarColor = null;
            isInCompetition = false;
        }

        buySellBtnContainer.setVisibility(View.GONE);

        progressAnimation = new Animation()
        {
            @Override protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t)
            {
                super.applyTransformation(interpolatedTime, t);
                quoteRefreshProgressBar.setProgress((int) (getMillisecondQuoteRefresh() * (1 - interpolatedTime)));
            }
        };
        progressAnimation.setDuration(getMillisecondQuoteRefresh());
        quoteRefreshProgressBar.setMax((int) getMillisecondQuoteRefresh());
        quoteRefreshProgressBar.setProgress((int) getMillisecondQuoteRefresh());
        quoteRefreshProgressBar.setAnimation(progressAnimation);


        if(LiveConstants.isInLiveMode) {
            int liveColor = getResources().getColor(R.color.general_red_live);
            buyBtn.setBackgroundColor(liveColor);
            sellBtn.setBackgroundColor(liveColor);
        }
        else {
            int virtualColor = getResources().getColor(R.color.general_brand_color);
            buyBtn.setBackgroundColor(virtualColor);
            sellBtn.setBackgroundColor(virtualColor);
        }
    }

    @Override public void onStart()
    {
        super.onStart();
        quoteObservable = createQuoteObservable();
        securityObservable = createSecurityObservable();

        onStopSubscriptions.add(quoteObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LiveQuoteDTO>()
                        {
                            @Override public void call(@NonNull LiveQuoteDTO quote)
                            {
                                linkWith(quote);
                            }
                        },
                        new ToastOnErrorAction1()));

        onStopSubscriptions.add(securityObservable
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO securityCompactDTO)
                            {
                                linkWith(securityCompactDTO);
                                signalRBuySellPrices();
                            }
                        },
                        new EmptyAction1<Throwable>()));

//        onStopSubscriptions.add(Observable.create(new Observable.OnSubscribe<Void>() {
//
//            @Override
//            public void call(Subscriber<? super Void> subscriber) {
//
//            }
//
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .doOnError(new Action1<Throwable>() {
//
//            @Override
//            public void call(Throwable throwable) {
//                if(throwable!=null){
//                    new TimberOnErrorAction1(throwable.getMessage());
//                }else{
//
//                }
//            }
//        }).subscribe());

        quoteSubscription =  Observable.combineLatest(
                securityObservable.observeOn(AndroidSchedulers.mainThread()),
                quoteObservable.observeOn(AndroidSchedulers.mainThread()),
                new Func2<SecurityCompactDTO, LiveQuoteDTO, Boolean>()
                {
                    @Override public Boolean call(@NonNull SecurityCompactDTO securityCompactDTO, @Nullable LiveQuoteDTO quoteDTO)
                    {
                        if(quoteDTO!=null)
                            displayBuySellPrice(securityCompactDTO, quoteDTO.getAskPrice(), quoteDTO.getBidPrice());
                        return true;
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction1("Failed to get Security and Quote"));

        onStopSubscriptions.add(quoteSubscription);

        onStopSubscriptions.add(getCloseablePositionObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PositionDTO>()
                        {
                            @Override public void call(@Nullable PositionDTO closeablePosition)
                            {
                                linkWith(closeablePosition);
                            }
                        },
                        new TimberOnErrorAction1(getString(R.string.error_fetch_position_list_info))));

        onStopSubscriptions.add(getPortfolioCompactObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PortfolioCompactDTO>()
                        {
                            @Override public void call(PortfolioCompactDTO portfolioCompactDTO)
                            {
                                linkWith(portfolioCompactDTO);
                            }
                        },
                        new TimberOnErrorAction1("Failed to get PortfolioCompact")));

        onStopSubscriptions.add(
                Observable.zip(
                        getApplicablePortfolioIdObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                        getPortfolioCompactListObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                        getApplicablePortfolioIdsObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                        new Func3<OwnedPortfolioId, PortfolioCompactDTOList, OwnedPortfolioIdList, Boolean>()
                        {
                            @Override public Boolean call(
                                    @NonNull OwnedPortfolioId applicablePortfolioId,
                                    @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
                                    @NonNull OwnedPortfolioIdList ownedPortfolioIds)
                            {
                                linkWith(applicablePortfolioId);
                                selectedPortfolioContainer.setDefaultPortfolioId(applicablePortfolioId);
                                for (PortfolioCompactDTO candidate : portfolioCompactDTOs)
                                {
                                    if (ownedPortfolioIds.contains(candidate.getOwnedPortfolioId()))
                                    {
                                        MenuOwnedPortfolioId menuOwnedPortfolioId = new MenuOwnedPortfolioId(
                                                candidate.getUserBaseKey(),
                                                candidate);

                                        if (menuOwnedPortfolioId.title != null)
                                        {
                                            if (menuOwnedPortfolioId.title.equals(getString(R.string.my_stocks_con)))
                                            {
                                                menuOwnedPortfolioId.title = getString(R.string.trending_tab_stocks_main);
                                            }
                                            else if (menuOwnedPortfolioId.title.equals(getString(R.string.my_fx_con)))
                                            {
                                                menuOwnedPortfolioId.title = getString(R.string.my_fx);
                                            }
                                        }

                                        selectedPortfolioContainer.addMenuOwnedPortfolioId(menuOwnedPortfolioId);
                                    }
                                }
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean aBoolean)
                                    {
                                        // Nothing to do
                                    }
                                },
                                new TimberOnErrorAction1("Failed to update portfolio selector")));

        onStopSubscriptions.add(getBuySellReady()
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean buySellReady)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction1("Failed to get BuySellReady")));

        onStopSubscriptions.add(getSupportSell()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean supportSell)
                            {
                                if (sellBtn != null)
                                {
                                    sellBtn.setVisibility(supportSell ? View.VISIBLE : View.GONE);
                                }
                            }
                        },
                        new TimberOnErrorAction1("Failed to get SupportSell")));

        final int closeUnits = requisite.getCloseUnits();
        if (closeUnits != 0)
        {
            onStopSubscriptions.add(Observable.zip(
                    quoteObservable.take(1).observeOn(AndroidSchedulers.mainThread()),
                    securityObservable.take(1).observeOn(AndroidSchedulers.mainThread()),
                    getBuySellReady().take(1).observeOn(AndroidSchedulers.mainThread()),
                    new Func3<LiveQuoteDTO, SecurityCompactDTO, Boolean, Boolean>()
                    {
                        @Override public Boolean call(
                                @NonNull LiveQuoteDTO quoteDTO,
                                @NonNull SecurityCompactDTO securityCompactDTO,
                                @NonNull Boolean buySellReady)
                        {
                            pushBuySellScreen(Math.abs(closeUnits), closeUnits < 0);
                            requisite.cancelCloseUnits();
                            return true;
                        }
                    })
                    .subscribe(
                            new Action1<Boolean>()
                            {
                                @Override public void call(Boolean aBoolean)
                                {
                                    // Nothing to do
                                }
                            },
                            new TimberOnErrorAction1("Failed to prepare for closing")));
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                buySellBtnContainer.setTranslationY(y);
            }
        });

        if (abstractBuySellPopupDialogFragment != null && abstractBuySellPopupDialogFragment.getDialog() != null)
        {
            abstractBuySellPopupDialogFragment.populateComment();
            abstractBuySellPopupDialogFragment.getDialog().show();
        }

        if(isInCompetition){
            GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_BUY_SELL);
        }else{
            if(securityCompactDTO!=null && securityCompactDTO instanceof FxSecurityCompactDTO){
                GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_FX_BUY_SELL);
            }else{
                GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_BUY_SELL);
            }
        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }
    //</editor-fold>

    @Override public void onPause()
    {
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        super.onPause();
    }

    @Override public void onStop()
    {
        quoteDTO = null;
        AbstractBuySellPopupDialogFragment copy = abstractBuySellPopupDialogFragment;
        if (copy != null)
        {
            copy.setBuySellTransactionListener(null);
        }
        if(quoteSubscription!=null){
            quoteSubscription.unsubscribe();
        }


        super.onStop();
    }

    @Override public void onDestroyView()
    {
        quoteRefreshProgressBar.clearAnimation();
        progressAnimation = null;
        quoteObservable = null;

        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        quoteRepeatSubject.onCompleted();
        if(abstractBuySellPopupDialogFragment!=null){
            try{
                abstractBuySellPopupDialogFragment.dismiss();
            }catch (Exception e){}

        }

        abstractBuySellPopupDialogFragment = null;
        super.onDestroy();
    }

//    private HubProxy hubProxy;

    public void signalRBuySellPrices(){

        onStopSubscriptions.add(securityObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(securityDTO ->{

                    if(signalRManager==null){
                        if(securityCompactDTO!=null && securityCompactDTO.getResourceId()!=null){
                            signalRManager = new SignalRManager(requestHeaders, currentUserId, LiveNetworkConstants.CLIENT_NOTIFICATION_HUB_NAME);
                            signalRManager.startConnectionWithUserId(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUP, Integer.toString(securityCompactDTO.getResourceId()));

                            signalRManager.getCurrentProxy().on("UpdateQuote", new SubscriptionHandler1<SignatureContainer2>() {

                                @Override
                                public void run(SignatureContainer2 signatureContainer2) {
                                    LiveQuoteDTO liveQuote = signatureContainer2.signedObject;
                                    if (signatureContainer2.signedObject == null || signatureContainer2.signedObject.id == 121234) {
                                        return;
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                if(liveQuote!=null) {
                                                    displayBuySellPrice(securityDTO, liveQuote.getAskPrice(), liveQuote.getBidPrice());
                                                    if (quoteSubscription != null && !quoteSubscription.isUnsubscribed())
                                                        quoteSubscription.unsubscribe();
//                                                }
                                            }
                                        });

                                    }
                                }
                            }, SignatureContainer2.class);
                        }

                    }

//            signalRManager.getConnection().start().done(actionVoid -> {
//                hubProxy.invoke(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUP, securityCompactDTO.id, currentUserId.get());
//            });

                }, new TimberOnErrorAction1("SignalR prices error")));
    }

    @NonNull protected Requisite createRequisite()
    {
        return getRequisite(getArguments(), portfolioCompactListCache, currentUserId);
    }

    protected long getMillisecondQuoteRefresh()
    {
        return MILLISECOND_QUOTE_REFRESH;
    }

    @NonNull protected Observable<SecurityCompactDTO> createSecurityObservable()
    {
        if(securityCompactDTO!=null){
            return Observable.just(securityCompactDTO);
        }else {
            return securityCompactCache.get(requisite.securityId)
                    .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                    .share()
                    .cache(1);
        }
    }

    @NonNull protected Observable<LiveQuoteDTO> createQuoteObservable()
    {
        return quoteServiceWrapper.getQuoteRx(requisite.securityIdNumber)
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                {
                    @Override public Observable<?> call(Observable<? extends Void> observable)
                    {
                        return observable.flatMap(new Func1<Void, Observable<?>>()
                        {
                            @Override public Observable<?> call(Void aVoid)
                            {
                                quoteRepeatSubject.onNext(aVoid);
                                return quoteRepeatDelayedObservable;
                            }
                        });
                    }
                })
                .share()
                .cache(1);
    }

    @NonNull protected Observable<PortfolioCompactDTOList> getPortfolioCompactListObservable()
    {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .map(new PairGetSecond<UserBaseKey, PortfolioCompactDTOList>())
                .share();
    }

    @NonNull protected Observable<PortfolioCompactDTO> getPortfolioCompactObservable() // Can pass null values
    {
        return Observable.combineLatest(
                getPortfolioCompactListObservable(),
                getApplicablePortfolioIdObservable(),
                new Func2<PortfolioCompactDTOList, OwnedPortfolioId, PortfolioCompactDTO>()
                {
                    @Override public PortfolioCompactDTO call(@NonNull PortfolioCompactDTOList portfolioCompactDTOs,
                            @NonNull final OwnedPortfolioId ownedPortfolioId)
                    {
                        PortfolioCompactDTO found = portfolioCompactDTOs.findFirstWhere(new Predicate<PortfolioCompactDTO>()
                        {
                            @Override public boolean apply(@NonNull PortfolioCompactDTO candidate)
                            {
                                return candidate.getOwnedPortfolioId().equals(ownedPortfolioId);
                            }
                        });
                        portfolioCompactDTO = found;
                        return found;
                    }
                })
                .share();
    }

    @NonNull protected Observable<PositionDTO> getCloseablePositionObservable() // It can pass null values
    {
        return Observable.combineLatest(
                positionCompactListCache.getOne(requisite.securityId),
                requisite.getApplicablePortfolioIdObservable(),
                new Func2<Pair<SecurityId, PositionDTOList>, OwnedPortfolioId, PositionDTO>()
                {
                    @Override
                    public PositionDTO call(@NonNull Pair<SecurityId, PositionDTOList> positionDTOsPair, @NonNull final OwnedPortfolioId portfolioId)
                    {
                        final PositionDTO position = positionDTOsPair.second.findFirstWhere(new Predicate<PositionDTO>()
                        {
                            @Override public boolean apply(PositionDTO positionDTO)
                            {
                                if(portfolioId.portfolioId!=null){
                                    return positionDTO.portfolioId.equals(portfolioId.portfolioId)
                                            && positionDTO.shares != null
                                            && positionDTO.shares != 0;
                                }else{
                                    return false;
                                }

                            }
                        });
                        closeablePositionDTO = position;
                        return position;
                    }
                })
                .share();
    }

    @NonNull protected Observable<OwnedPortfolioIdList> getApplicablePortfolioIdsObservable()
    {
        return ownedPortfolioIdListCache.get(requisite.securityId)
                .distinctUntilChanged(
                        new Func1<Pair<PortfolioCompactListKey, OwnedPortfolioIdList>, String>()
                        {
                            @Override
                            public String call(Pair<PortfolioCompactListKey, OwnedPortfolioIdList> pair)
                            {
                                String code = "first=" + pair.first + ", second=";
                                for (OwnedPortfolioId portfolioId : pair.second)
                                {
                                    code += portfolioId.toString() + ",";
                                }
                                return code;
                            }
                        })
                .map(new Func1<Pair<PortfolioCompactListKey, OwnedPortfolioIdList>, OwnedPortfolioIdList>()
                {
                    @Override public OwnedPortfolioIdList call(Pair<PortfolioCompactListKey, OwnedPortfolioIdList> pair)
                    {
                        applicableOwnedPortfolioIds = pair.second;
                        return pair.second;
                    }
                })
                .share();
    }

    @NonNull abstract protected Observable<PortfolioCompactDTO> getDefaultPortfolio(); // Can pipe null objects

    @NonNull protected Observable<OwnedPortfolioId> getApplicablePortfolioIdObservable()
    {
        return Observable.combineLatest(
                requisite.getApplicablePortfolioIdObservable().distinctUntilChanged(),
                getApplicablePortfolioIdsObservable(),
                getDefaultPortfolio(),
                new Func3<OwnedPortfolioId, OwnedPortfolioIdList, PortfolioCompactDTO, OwnedPortfolioId>()
                {
                    @Override
                    public OwnedPortfolioId call(
                            @NonNull OwnedPortfolioId ownedPortfolioId,
                            @NonNull OwnedPortfolioIdList ownedPortfolioIds,
                            @Nullable PortfolioCompactDTO defaultPortfolio)
                    {
                        if (ownedPortfolioIds.contains(ownedPortfolioId))
                        {
                            poppedPortfolioChanged = true;
                            return ownedPortfolioId;
                        }
                        popPortfolioChanged();
                        if (defaultPortfolio != null)
                        {
                            return defaultPortfolio.getOwnedPortfolioId();
                        }
                        throw new NullPointerException("Default portfolio was null");
                    }
                })
                .doOnNext(new Action1<OwnedPortfolioId>()
                {
                    @Override public void call(OwnedPortfolioId ownedPortfolioId)
                    {
                        Timber.d("Portfolio: %s", ownedPortfolioId.portfolioId);
                    }
                })
                .distinctUntilChanged();
    }

    protected void popPortfolioChanged()
    {
        if (!poppedPortfolioChanged)
        {
            poppedPortfolioChanged = true;
            //TODO Jeff
//            onStopSubscriptions.add(AlertDialogRxUtil.buildDefault(getActivity())
//                    .setTitle(R.string.buy_sell_portfolio_changed_title)
//                    .setMessage(R.string.buy_sell_portfolio_changed_message)
//                    .setPositiveButton(R.string.ok)
//                    .build()
//                    .subscribe(
//                            new EmptyAction1<OnDialogClickEvent>(),
//                            new EmptyAction1<Throwable>()));
        }
    }

    protected abstract void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs);

    protected abstract void linkWith(@NonNull OwnedPortfolioId purchaseApplicablePortfolioId);

    @NonNull abstract protected Observable<Boolean> getSupportSell();

    @NonNull public Observable<Boolean> getBuySellReady()
    {
        return Observable.combineLatest(
                quoteObservable.observeOn(AndroidSchedulers.mainThread()),
                getCloseablePositionObservable().observeOn(AndroidSchedulers.mainThread()),
                getApplicablePortfolioIdsObservable().observeOn(AndroidSchedulers.mainThread()),
                new Func3<LiveQuoteDTO, PositionDTO, OwnedPortfolioIdList, Boolean>()
                {
                    @Override public Boolean call(@NonNull LiveQuoteDTO quoteDTO, @Nullable PositionDTO positionDTO,
                            @NonNull OwnedPortfolioIdList ownedPortfolioIds)
                    {
                        handleBuySellReady();
                        return true;
                    }
                })
                .share();
    }

    protected void handleBuySellReady()
    {
        if (buySellBtnContainer.getVisibility() == View.GONE)
        {
            Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
            slideIn.setFillAfter(true);
            buySellBtnContainer.setVisibility(View.VISIBLE);
            buySellBtnContainer.startAnimation(slideIn);
        }
    }

    protected void linkWith(LiveQuoteDTO quoteDTO)
    {
        this.quoteDTO = quoteDTO;
        quoteRefreshProgressBar.startAnimation(progressAnimation);
    }

    public void linkWith(@NonNull final SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        displayMarketClose(securityCompactDTO);
        displayStockName(securityCompactDTO);
    }

    public void linkWith(@Nullable PositionDTO closeablePosition)
    {
    }

    public void displayStockName(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        //Nothing to do.
    }

    abstract public void displayBuySellPrice(@NonNull SecurityCompactDTO securityCompactDTO, @Nullable Double askPrice, @Nullable Double bidPrice);

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
    }

    @Nullable public Integer getMaxSellableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTO closeablePositionDTO)
    {
        return PortfolioCompactDTOUtil.getMaxSellableShares(
                portfolioCompactDTO,
                quoteDTO,
                closeablePositionDTO);
    }

    protected void displayMarketClose(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        boolean marketIsOpen = securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen;
        if (!marketIsOpen && showMarketClosedIntervalPreference.isItTime())
        {
            notifyMarketClosed();
            showMarketClosedIntervalPreference.justHandled();
        }
    }

    protected void notifyMarketClosed()
    {
        onStopSubscriptions.add(AlertDialogBuySellRxUtil.popMarketClosed(getActivity(), requisite.securityId)
                .subscribe(new EmptyAction1<OnDialogClickEvent>(),
                        new EmptyAction1<Throwable>()));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.portfolio_selector_container)
    protected void showPortfolioSelector()
    {
        onStopSubscriptions.add(selectedPortfolioContainer.createMenuObservable()
                .map(new Func1<MenuOwnedPortfolioId, OwnedPortfolioId>()
                {
                    @Override public OwnedPortfolioId call(MenuOwnedPortfolioId menuOwnedPortfolioId)
                    {
                        return new OwnedPortfolioId(menuOwnedPortfolioId);
                    }
                })
                .subscribe(
                        new Action1<OwnedPortfolioId>()
                        {
                            public void call(OwnedPortfolioId args)
                            {
                                requisite.onNext(args);
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_buy_sell;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.btn_buy, R.id.btn_sell})
    protected void handleBuySellButtonsClicked(View view)
    {
        boolean isTransactionTypeBuy;
        switch (view.getId())
        {
            case R.id.btn_buy:
                isTransactionTypeBuy = true;
                if(isInCompetition){
                    GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_BUY);
                }else{
                    //local
                }
                break;
            case R.id.btn_sell:
                isTransactionTypeBuy = false;
                if(isInCompetition){
                    GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_SELL);
                }else{
                    //local
                }
                break;
            default:
                throw new IllegalArgumentException("Unhandled button " + view.getId());
        }
        pushBuySellScreen(null, isTransactionTypeBuy);
    }

    public void pushBuySellScreen(@Nullable Integer closeUnits, boolean isTransactionTypeBuy)
    {
        if (abstractBuySellPopupDialogFragment != null
                && abstractBuySellPopupDialogFragment.isVisible())
        {
            return;//buy/sell dialog already shows
        }
        if (quoteDTO != null
                && BuyStockFragment.canShowTransactionScreen(quoteDTO, isTransactionTypeBuy))
        {
            OwnedPortfolioId currentMenu = selectedPortfolioContainer.getCurrentMenu();
            if (currentMenu != null)
            {
                if (securityCompactDTO instanceof FxSecurityCompactDTO)
                {
//                    Bundle args = new Bundle();
//                    Class klass = isTransactionTypeBuy ? BuyFXFragment.class : SellFXFragment.class;
//
//                    AbstractTransactionFragment.Requisite transactionRequisite = new AbstractTransactionFragment.Requisite(
//                            requisite.securityId,
//                            currentMenu.getPortfolioIdKey(),
//                            quoteDTO,
//                            closeUnits == null ? null : Math.abs(closeUnits));
//
//                    AbstractStockTransactionFragment.putRequisite(args, transactionRequisite);
//
//                    abstractTransactionFragment = (AbstractTransactionFragment) navigator.get().pushFragment(klass, args);

                    abstractBuySellPopupDialogFragment = AbstractStockTransactionFragment.newInstance(
                            isTransactionTypeBuy,
                            new AbstractBuySellPopupDialogFragment.Requisite(
                                    requisite.securityId,
                                    currentMenu.getPortfolioIdKey(),
                                    quoteDTO,
                                    closeUnits == null ? null : Math.abs(closeUnits)),
                            topBarColor);

                    if(isTransactionTypeBuy){
                        GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_FX_BUY_NOW);
                    }else{
                        GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_FX_SELL_NOW);
                    }
                }
                else
                {
                    abstractBuySellPopupDialogFragment = AbstractStockTransactionFragment.newInstance(
                            isTransactionTypeBuy,
                            new AbstractBuySellPopupDialogFragment.Requisite(
                                    requisite.securityId,
                                    currentMenu.getPortfolioIdKey(),
                                    quoteDTO,
                                    closeUnits == null ? null : Math.abs(closeUnits)),
                            topBarColor);

                    if(isTransactionTypeBuy){
                        if(isInCompetition){
                            GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_BUY_NOW);
                        }else{
                            GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_BUY_NOW);
                        }
                    }else{
                        if(isInCompetition){
                            GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_SELL_NOW);
                        }else{
                            GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.LOCAL_SELL_NOW);
                        }
                    }

//                    Bundle args = new Bundle();
//                    Class klass = isTransactionTypeBuy ? BuyStockFragment.class : SellStockFragment.class;
//
//                    AbstractBuySellPopupDialogFragment.Requisite transactionReq = new AbstractBuySellPopupDialogFragment.Requisite(
//                            this.requisite.securityId,
//                            currentMenu.getPortfolioIdKey(),
//                            quoteDTO,
//                            closeUnits == null ? null : Math.abs(closeUnits));
//
//                    AbstractBuySellPopupDialogFragment.putRequisite(args, transactionReq);
//
//                    abstractBuySellPopupDialogFragment = (AbstractBuySellPopupDialogFragment) navigator.get().pushFragment(AbstractBuySellPopupDialogFragment.class, args);

//                    AbstractTransactionFragment.Requisite transactionRequisite = new AbstractTransactionFragment.Requisite(
//                            this.requisite.securityId,
//                            currentMenu.getPortfolioIdKey(),
//                            quoteDTO,
//                            closeUnits == null ? null : Math.abs(closeUnits));
//
//                    AbstractStockTransactionFragment.putRequisite(args, transactionRequisite);
//
//                    abstractTransactionFragment = (AbstractTransactionFragment) navigator.get().pushFragment(klass, args);
                }

                abstractBuySellPopupDialogFragment.show(getActivity().getSupportFragmentManager(), AbstractBuySellPopupDialogFragment.class.getName());
                listenToBuySellDialog();
            }
            else
            {
                onStopSubscriptions.add(AlertDialogRxUtil.buildDefault(getActivity())
                        .setTitle(R.string.buy_sell_no_portfolio_title)
                        .setMessage(R.string.buy_sell_no_portfolio_message)
                        .setPositiveButton(R.string.buy_sell_no_portfolio_cancel)
                        .build()
                        .subscribe(
                                new EmptyAction1<OnDialogClickEvent>(),
                                new EmptyAction1<Throwable>()));
            }
        }
        else
        {
            onStopSubscriptions.add(AlertDialogRxUtil.buildDefault(getActivity())
                    .setTitle(R.string.buy_sell_no_quote_title)
                    .setMessage(R.string.buy_sell_no_quote_message)
                    .setPositiveButton(R.string.buy_sell_no_quote_cancel)
                    .build()
                    .subscribe(
                            new EmptyAction1<OnDialogClickEvent>(),
                            new EmptyAction1<Throwable>()));
        }
    }

    protected void listenToBuySellDialog()
    {
        if (abstractBuySellPopupDialogFragment != null)
        {
            abstractBuySellPopupDialogFragment.setBuySellTransactionListener(new AbstractStockTransactionFragment.BuySellTransactionListener()
            {
                @Override public void onTransactionSuccessful(boolean isBuy,
                                                              @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO, String commentString)
                {
                    showPrettyReviewAndInvite(isBuy);
//                    shareToWeChat(commentString, isBuy);
                    String positionType = null;
                    if (securityPositionTransactionDTO.positions == null)
                    {
                        positionType = TabbedPositionListFragment.TabType.CLOSED.name();
                    }
                    else
                    {
                        if (securityPositionTransactionDTO.positions.size() == 0)
                        {
                            positionType = TabbedPositionListFragment.TabType.CLOSED.name();
                        }
                        else
                        {
                            if(securityPositionTransactionDTO.positions.get(0).positionStatus!=null){
                                positionType = securityPositionTransactionDTO.positions.get(0).positionStatus.name();
                            }else{
                                positionType = TabbedPositionListFragment.TabType.CLOSED.name();
                            }

                        }
                    }
                    pushPortfolioFragment(
                            new OwnedPortfolioId(currentUserId.get(), securityPositionTransactionDTO.portfolio.id),
                            securityPositionTransactionDTO.portfolio,
                            positionType);
                }

                @Override public void onTransactionFailed(boolean isBuy, THException error)
                {
                    // TODO Toast error buy?
                }
            });
        }
    }

    private void showPrettyReviewAndInvite(boolean isBuy)
    {
        Double profit = abstractBuySellPopupDialogFragment.getProfitOrLossUsd();
        if (!isBuy && profit != null && profit > 0)
        {
            if (mShowAskForReviewDialogPreference.isItTime())
            {
                broadcastUtils.enqueue(new SendLoveBroadcastSignal());
            }
            else if (mShowAskForInviteDialogPreference.isItTime())
            {
                AskForInviteDialogFragment.showInviteDialog(getActivity().getSupportFragmentManager());
            }
        }
    }

    private void pushPortfolioFragment(OwnedPortfolioId ownedPortfolioId, PortfolioDTO portfolioDTO, String positionType)
    {
        if (isResumed())
        {
            DeviceUtil.dismissKeyboard(getActivity());

            if (navigator.get().hasBackStackName(TabbedPositionListFragment.class.getName()))
            {
                navigator.get().popFragment(TabbedPositionListFragment.class.getName());
            }
            else {
                if (navigator.get().hasBackStackName(CompetitionLeaderboardPositionListFragment.class.getName())) {
                    navigator.get().popFragment(CompetitionLeaderboardPositionListFragment.class.getName());
                    // Test for other classes in the future
                } else {
                    // TODO find a better way to remove this fragment from the stack
                    navigator.get().popFragment();

                    Bundle args = new Bundle();
                    OwnedPortfolioId applicablePortfolioId = requisite.getApplicablePortfolioIdObservable().toBlocking().first(); // TODO better
                    if (applicablePortfolioId != null) {
                        TabbedPositionListFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                        TabbedPositionListFragment.putIsFX(args, portfolioDTO.assetClass);
                    }
                    TabbedPositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
                    TabbedPositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
                    TabbedPositionListFragment.putPositionType(args, positionType);

                    if (navigator.get().hasBackStackName(MainCompetitionFragment.class.getName())) {
                        DashboardNavigator.putReturnFragment(args, MainCompetitionFragment.class.getName());
                    }
                    navigator.get().pushFragment(TabbedPositionListFragment.class, args);
                }
            }
        }
    }

    public static class Requisite
    {
        private static final String KEY_SECURITY_ID = Requisite.class.getName() + ".securityId";
        private static final String KEY_APPLICABLE_PORTFOLIO_ID = Requisite.class.getName() + ".applicablePortfolioId";
        private final static String KEY_CLOSE_UNITS_BUNDLE = Requisite.class.getName() + ".units";
        private final static String KEY_SECURITY_RESOURCE_ID = Requisite.class.getName() + ".securityResourceId";

        public final SecurityId securityId;
        @NonNull private final BehaviorSubject<OwnedPortfolioId> applicablePortfolioIdSubject;
        private int closeUnits;
        private int securityIdNumber;

        public Requisite(
                @NonNull SecurityId securityId,
                @NonNull OwnedPortfolioId applicablePortfolioId,
                int closeUnits)
        {
            this.securityIdNumber = securityId.getSecurityIdNumber();
            this.securityId = securityId;
            this.applicablePortfolioIdSubject = BehaviorSubject.create(applicablePortfolioId);
            this.closeUnits = closeUnits;
        }

//        public Requisite(
//                @NonNull SecurityId securityId,
//                @NonNull OwnedPortfolioId applicablePortfolioId,
//                int closeUnits)
//        {
//            this.securityId = securityId;
//            this.applicablePortfolioIdSubject = BehaviorSubject.create(applicablePortfolioId);
//            this.closeUnits = closeUnits;
//        }

        public Requisite(@NonNull SecurityId securityId,
                @NonNull Bundle args,
                @NonNull PortfolioCompactListCacheRx portfolioCompactListCache,
                @NonNull CurrentUserId currentUserId)
        {
            this.securityId = securityId;
            this.applicablePortfolioIdSubject = createApplicablePortfolioIdSubject(args.getBundle(KEY_APPLICABLE_PORTFOLIO_ID),
                    securityId,
                    portfolioCompactListCache,
                    currentUserId);
            this.closeUnits = getCloseUnits(args);
        }

        public Requisite(@NonNull Bundle args,
                @NonNull PortfolioCompactListCacheRx portfolioCompactListCache,
                @NonNull CurrentUserId currentUserId)
        {
            this.securityId = getSecurityId(args.getBundle(KEY_SECURITY_ID));
            this.securityIdNumber = securityId!=null? securityId.getSecurityIdNumber(): 0;
            this.applicablePortfolioIdSubject = createApplicablePortfolioIdSubject(args.getBundle(KEY_APPLICABLE_PORTFOLIO_ID),
                    securityId,
                    portfolioCompactListCache,
                    currentUserId);
            this.closeUnits = getCloseUnits(args);
        }

        @Nullable private static SecurityId getSecurityId(@Nullable Bundle securityArgs)
        {
            if (securityArgs != null) {
                return new SecurityId(securityArgs);
            }else{
                return null;
            }
//            throw new NullPointerException("SecurityId cannot be null");
        }

        @NonNull private static int getSecurityIdNumber(@Nullable Bundle securityArgs)
        {
            if (securityArgs != null)
            {
                return securityArgs.getInt(KEY_SECURITY_RESOURCE_ID);
            }
            return 0;
        }

        @NonNull private static BehaviorSubject<OwnedPortfolioId> createApplicablePortfolioIdSubject(
                @Nullable Bundle portfolioArgs,
                @NonNull final SecurityId securityId,
                @NonNull PortfolioCompactListCacheRx portfolioCompactListCache,
                @NonNull CurrentUserId currentUserId)
        {
            if (portfolioArgs != null)
            {
                return BehaviorSubject.create(new OwnedPortfolioId(portfolioArgs));
            }
            final BehaviorSubject<OwnedPortfolioId> subject = BehaviorSubject.create();
            portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
                    .map(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, OwnedPortfolioId>()
                    {
                        @Override public OwnedPortfolioId call(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                        {
                            //noinspection ConstantConditions
                            return (SecurityUtils.isFX(securityId)
                                    ? pair.second.getDefaultFxPortfolio()
                                    : pair.second.getDefaultPortfolio())
                                    .getOwnedPortfolioId();
                        }
                    })
                    .subscribe(
                            new Action1<OwnedPortfolioId>()
                            {
                                @Override public void call(OwnedPortfolioId ownedPortfolioId)
                                {
                                    subject.onNext(ownedPortfolioId);
                                }
                            },
                            new TimberOnErrorAction1("Failed to get portfolio list"),
                            new Action0()
                            {
                                @Override public void call()
                                {
                                    // Intercepting it so that the BehaviorSubject does not end there.
                                }
                            });
            return subject;
        }

        private static int getCloseUnits(@NonNull Bundle args)
        {
            return args.getInt(KEY_CLOSE_UNITS_BUNDLE, 0);
        }

        @NonNull public Observable<OwnedPortfolioId> getApplicablePortfolioIdObservable()
        {
            return applicablePortfolioIdSubject.asObservable();
        }

        public void onNext(@NonNull OwnedPortfolioId nextPortfolioId)
        {
            applicablePortfolioIdSubject.onNext(nextPortfolioId);
        }

        public int getCloseUnits()
        {
            return closeUnits;
        }

        public void cancelCloseUnits()
        {
            closeUnits = 0;
        }

        @NonNull public Bundle getArgs()
        {
            Bundle args = new Bundle();
            populate(args);
            return args;
        }

        protected void populate(@NonNull Bundle args)
        {
            args.putInt(KEY_SECURITY_RESOURCE_ID, securityIdNumber);
            args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
            args.putBundle(KEY_APPLICABLE_PORTFOLIO_ID, applicablePortfolioIdSubject.toBlocking().first().getArgs());
            args.putInt(KEY_CLOSE_UNITS_BUNDLE, closeUnits);
        }

        static void putApplicablePorfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId applicablePortfolioId)
        {
            args.putBundle(KEY_APPLICABLE_PORTFOLIO_ID, applicablePortfolioId.getArgs());
        }
    }
}
