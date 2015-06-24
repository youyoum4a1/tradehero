package com.tradehero.th.fragments.trade;

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
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.android.internal.util.Predicate;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.key.PortfolioCompactListKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TillExchangeOpenDuration;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.trade.view.PortfolioSelectorView;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.dto.SocialShareResult;
import com.tradehero.th.persistence.portfolio.OwnedPortfolioIdListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.PositionListCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.prefs.ShowMarketClosed;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.subjects.BehaviorSubject;

abstract public class AbstractBuySellFragment extends DashboardFragment
        implements WithTutorial
{
    private final static String BUNDLE_KEY_REQUISITE = AbstractBuySellFragment.class.getName() + ".requisite";
    private final static long MILLISECOND_QUOTE_REFRESH = 30000;
    public static final int MS_DELAY_FOR_BG_IMAGE = 200;
    public static final boolean DEFAULT_IS_SHARED_TO_WECHAT = false;

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
    @Inject protected SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject protected Lazy<SocialSharer> socialSharerLazy;

    @InjectView(R.id.portfolio_selector_container) protected PortfolioSelectorView selectedPortfolioContainer;
    @InjectView(R.id.quote_refresh_countdown) protected ProgressBar quoteRefreshProgressBar;
    @InjectView(R.id.bottom_button) protected ViewGroup buySellBtnContainer;
    @InjectView(R.id.btn_buy) protected Button buyBtn;
    @InjectView(R.id.btn_sell) protected Button sellBtn;
    @InjectView(R.id.market_close_container) @Optional protected View marketClosedContainer;
    @InjectView(R.id.market_close_hint) @Optional protected TextView marketCloseHint;

    protected Requisite requisite;
    @Nullable protected QuoteDTO quoteDTO;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected PositionDTO closeablePositionDTO;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    @Nullable protected PortfolioCompactDTO defaultPortfolio;
    @Nullable protected OwnedPortfolioIdList applicableOwnedPortfolioIds;

    protected Animation progressAnimation;
    protected AbstractTransactionDialogFragment abstractTransactionDialogFragment;

    public static void putRequisite(@NonNull Bundle args, @NonNull Requisite requisite)
    {
        args.putBundle(BUNDLE_KEY_REQUISITE, requisite.getArgs());
    }

    @NonNull private static Requisite getRequisite(@NonNull Bundle args)
    {
        Bundle requisiteBundle = args.getBundle(BUNDLE_KEY_REQUISITE);
        if (requisiteBundle == null)
        {
            throw new NullPointerException("Requisite need to be passed on");
        }
        return new Requisite(requisiteBundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        requisite = createRequisite();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        setRetainInstance(true);

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

        listenToBuySellDialog();
    }

    @Override public void onStart()
    {
        super.onStart();

        onStopSubscriptions.add(getQuoteObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<QuoteDTO>()
                        {
                            @Override public void call(@NonNull QuoteDTO quote)
                            {
                                linkWith(quote);
                            }
                        },
                        new ToastOnErrorAction()));

        onStopSubscriptions.add(getSecurityObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO securityCompactDTO)
                            {
                                linkWith(securityCompactDTO);
                            }
                        },
                        new EmptyAction1<Throwable>()));

        onStopSubscriptions.add(Observable.combineLatest(
                getSecurityObservable().observeOn(AndroidSchedulers.mainThread()),
                getQuoteObservable().observeOn(AndroidSchedulers.mainThread()),
                new Func2<SecurityCompactDTO, QuoteDTO, Boolean>()
                {
                    @Override public Boolean call(@NonNull SecurityCompactDTO securityCompactDTO, @NonNull QuoteDTO quoteDTO)
                    {
                        displayBuySellPrice(securityCompactDTO, quoteDTO);
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
                        new TimberOnErrorAction("Failed to get Security and Quote")));

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
                        new TimberOnErrorAction(getString(R.string.error_fetch_position_list_info))));

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
                        new TimberOnErrorAction("Failed to get PortfolioCompact")));

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
                                        selectedPortfolioContainer.addMenuOwnedPortfolioId(new MenuOwnedPortfolioId(
                                                candidate.getUserBaseKey(),
                                                candidate));
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
                                new TimberOnErrorAction("Failed to update portfolio selector")));

        onStopSubscriptions.add(getBuySellReady()
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean buySellReady)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction("Failed to get BuySellReady")));

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
                        new TimberOnErrorAction("Failed to get SupportSell")));

        final int closeUnits = requisite.getCloseUnits();
        if (closeUnits != 0)
        {
            onStopSubscriptions.add(Observable.zip(
                    getQuoteObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                    getSecurityObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                    getBuySellReady().take(1).observeOn(AndroidSchedulers.mainThread()),
                    new Func3<QuoteDTO, SecurityCompactDTO, Boolean, Boolean>()
                    {
                        @Override public Boolean call(
                                @NonNull QuoteDTO quoteDTO,
                                @NonNull SecurityCompactDTO securityCompactDTO,
                                @NonNull Boolean buySellReady)
                        {
                            showBuySellDialog(Math.abs(closeUnits), closeUnits > 0);
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
                            new TimberOnErrorAction("Failed to prepare for closing")));
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        if (abstractTransactionDialogFragment != null && abstractTransactionDialogFragment.getDialog() != null)
        {
            abstractTransactionDialogFragment.populateComment();
            abstractTransactionDialogFragment.getDialog().show();
        }

        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                buySellBtnContainer.setTranslationY(y);
            }
        });
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
        AbstractTransactionDialogFragment dialogCopy = abstractTransactionDialogFragment;
        if (dialogCopy != null)
        {
            dialogCopy.setBuySellTransactionListener(null);
        }

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        quoteRefreshProgressBar.clearAnimation();
        progressAnimation = null;

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        abstractTransactionDialogFragment = null;
        super.onDestroy();
    }

    @NonNull protected Requisite createRequisite()
    {
        return getRequisite(getArguments());
    }

    protected long getMillisecondQuoteRefresh()
    {
        return MILLISECOND_QUOTE_REFRESH;
    }

    @NonNull protected Observable<SecurityCompactDTO> getSecurityObservable()
    {
        return securityCompactCache.get(requisite.securityId)
                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                .share();
    }

    @NonNull protected Observable<QuoteDTO> getQuoteObservable()
    {
        return quoteServiceWrapper.getQuoteRx(requisite.securityId)
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                {
                    @Override public Observable<?> call(Observable<? extends Void> observable)
                    {
                        return observable.delay(AbstractBuySellFragment.this.getMillisecondQuoteRefresh(), TimeUnit.MILLISECONDS);
                    }
                })
                .share();
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
                positionCompactListCache.get(requisite.securityId),
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
                                return positionDTO.portfolioId == portfolioId.portfolioId
                                        && positionDTO.shares != null
                                        && positionDTO.shares != 0;
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
                .distinctUntilChanged()
                .share();
    }

    protected void popPortfolioChanged()
    {
        onStopSubscriptions.add(AlertDialogRxUtil.buildDefault(getActivity())
                .setTitle(R.string.buy_sell_portfolio_changed_title)
                .setMessage(R.string.buy_sell_portfolio_changed_message)
                .setPositiveButton(R.string.ok)
                .build()
                .subscribe(
                        new EmptyAction1<OnDialogClickEvent>(),
                        new EmptyAction1<Throwable>()));
    }

    protected abstract void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs);

    protected abstract void linkWith(@NonNull OwnedPortfolioId purchaseApplicablePortfolioId);

    @NonNull abstract protected Observable<Boolean> getSupportSell();

    @NonNull public Observable<Boolean> getBuySellReady()
    {
        return Observable.combineLatest(
                getQuoteObservable().observeOn(AndroidSchedulers.mainThread()),
                getCloseablePositionObservable().observeOn(AndroidSchedulers.mainThread()),
                getApplicablePortfolioIdsObservable().observeOn(AndroidSchedulers.mainThread()),
                new Func3<QuoteDTO, PositionDTO, OwnedPortfolioIdList, Boolean>()
                {
                    @Override public Boolean call(@NonNull QuoteDTO quoteDTO, @Nullable PositionDTO positionDTO,
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

    protected void linkWith(QuoteDTO quoteDTO)
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
        if (marketClosedContainer != null)
        {
            boolean marketIsOpen = securityCompactDTO.marketOpen == null
                    || securityCompactDTO.marketOpen;
            marketClosedContainer.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
            if (!marketIsOpen)
            {
                marketCloseHint.setText(getMarketCloseHint(securityCompactDTO));
            }
        }
    }

    @NonNull public String getMarketCloseHint(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        TillExchangeOpenDuration duration = securityCompactDTO.getTillExchangeOpen();
        if (duration == null)
        {
            return "";
        }
        return getString(R.string.market_close_hint) + " " +
                DateUtils.getDurationText(getResources(), duration.days, duration.hours, duration.minutes);
    }

    abstract public void displayBuySellPrice(@NonNull SecurityCompactDTO securityCompactDTO, @NonNull QuoteDTO quoteDTO);

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
    }

    @Nullable public Integer getMaxPurchasableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTO closeablePositionDTO)
    {
        return PortfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO,
                closeablePositionDTO);
    }

    @Nullable public Integer getMaxSellableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
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

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.market_closed_icon)
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
                .subscribe(
                        new Action1<MenuOwnedPortfolioId>()
                        {
                            public void call(MenuOwnedPortfolioId args)
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
                break;
            case R.id.btn_sell:
                isTransactionTypeBuy = false;
                break;
            default:
                throw new IllegalArgumentException("Unhandled button " + view.getId());
        }
        showBuySellDialog(null, isTransactionTypeBuy);
    }

    public void showBuySellDialog(@Nullable Integer closeUnits, boolean isTransactionTypeBuy)
    {
        if (abstractTransactionDialogFragment != null
                && abstractTransactionDialogFragment.isVisible())
        {
            return;//buy/sell dialog already shows
        }
        if (quoteDTO != null
                && BuyStockDialogFragment.canShowDialog(quoteDTO, isTransactionTypeBuy))
        {
            OwnedPortfolioId currentMenu = selectedPortfolioContainer.getCurrentMenu();
            if (currentMenu != null)
            {
                if (securityCompactDTO instanceof FxSecurityCompactDTO)
                {
                    abstractTransactionDialogFragment = AbstractFXTransactionDialogFragment.newInstance(
                            isTransactionTypeBuy,
                            new AbstractTransactionDialogFragment.Requisite(
                                    requisite.securityId,
                                    currentMenu.getPortfolioIdKey(),
                                    quoteDTO,
                                    closeUnits == null ? null : Math.abs(closeUnits)));
                }
                else
                {
                    abstractTransactionDialogFragment = AbstractStockTransactionDialogFragment.newInstance(
                            isTransactionTypeBuy,
                            new AbstractTransactionDialogFragment.Requisite(
                                    requisite.securityId,
                                    currentMenu.getPortfolioIdKey(),
                                    quoteDTO,
                                    closeUnits == null ? null : Math.abs(closeUnits)));
                }
                abstractTransactionDialogFragment.show(getActivity().getFragmentManager(), AbstractTransactionDialogFragment.class.getName());
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
        if (abstractTransactionDialogFragment != null)
        {
            abstractTransactionDialogFragment.setBuySellTransactionListener(new AbstractStockTransactionDialogFragment.BuySellTransactionListener()
            {
                @Override public void onTransactionSuccessful(boolean isBuy,
                        @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO, String commentString)
                {
                    showPrettyReviewAndInvite(isBuy);
                    shareToWeChat(commentString, isBuy);
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
                            positionType = securityPositionTransactionDTO.positions.get(0).positionStatus.name();
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
        Double profit = abstractTransactionDialogFragment.getProfitOrLossUsd();
        if (!isBuy && profit != null && profit > 0)
        {
            if (mShowAskForReviewDialogPreference.isItTime())
            {
                broadcastUtils.enqueue(new SendLoveBroadcastSignal());
            }
            else if (mShowAskForInviteDialogPreference.isItTime())
            {
                AskForInviteDialogFragment.showInviteDialog(getActivity().getFragmentManager());
            }
        }
    }

    public void shareToWeChat(String commentString, boolean isTransactionTypeBuy)
    {
        //TODO Move this!
        if (socialSharePreferenceHelperNew.isShareEnabled(SocialNetworkEnum.WECHAT, DEFAULT_IS_SHARED_TO_WECHAT))
        {
            WeChatDTO weChatDTO = new WeChatDTO();
            weChatDTO.id = securityCompactDTO.id;
            weChatDTO.type = WeChatMessageType.Trade;
            if (securityCompactDTO.imageBlobUrl != null && securityCompactDTO.imageBlobUrl.length() > 0)
            {
                weChatDTO.imageURL = securityCompactDTO.imageBlobUrl;
            }
            if (isTransactionTypeBuy)
            {
                weChatDTO.title = getString(R.string.buy_sell_switch_buy) + " "
                        + securityCompactDTO.name + " " + abstractTransactionDialogFragment.getQuantityString() + getString(
                        R.string.buy_sell_share_count) + " @" + quoteDTO.ask;
            }
            else
            {
                weChatDTO.title = getString(R.string.buy_sell_switch_sell) + " "
                        + securityCompactDTO.name + " " + abstractTransactionDialogFragment.getQuantityString() + getString(
                        R.string.buy_sell_share_count) + " @" + quoteDTO.bid;
            }
            if (commentString != null && !commentString.isEmpty())
            {
                weChatDTO.title = commentString + '\n' + weChatDTO.title;
            }
            socialSharerLazy.get().share(weChatDTO)
                    .subscribe(
                            new EmptyAction1<SocialShareResult>(),
                            new ToastAndLogOnErrorAction("Failed to share to WeChat")); // TODO proper callback?
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
            else if (navigator.get().hasBackStackName(CompetitionLeaderboardPositionListFragment.class.getName()))
            {
                navigator.get().popFragment(CompetitionLeaderboardPositionListFragment.class.getName());
                // Test for other classes in the future
            }
            else
            {
                // TODO find a better way to remove this fragment from the stack
                navigator.get().popFragment();

                Bundle args = new Bundle();
                OwnedPortfolioId applicablePortfolioId = requisite.getApplicablePortfolioIdObservable().toBlocking().first(); // TODO better
                if (applicablePortfolioId != null)
                {
                    TabbedPositionListFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                    TabbedPositionListFragment.putIsFX(args, portfolioDTO.assetClass);
                }
                TabbedPositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
                TabbedPositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
                TabbedPositionListFragment.putPositionType(args, positionType);

                if (navigator.get().hasBackStackName(MainCompetitionFragment.class.getName()))
                {
                    DashboardNavigator.putReturnFragment(args, MainCompetitionFragment.class.getName());
                }
                navigator.get().pushFragment(TabbedPositionListFragment.class, args);
            }
        }
    }

    public static class Requisite
    {
        private static final String KEY_SECURITY_ID = Requisite.class.getName() + ".securityId";
        private static final String KEY_APPLICABLE_PORTFOLIO_ID = Requisite.class.getName() + ".applicablePortfolioId";
        private final static String KEY_CLOSE_UNITS_BUNDLE = Requisite.class.getName() + ".units";

        @NonNull public final SecurityId securityId;
        @NonNull private final BehaviorSubject<OwnedPortfolioId> applicablePortfolioIdSubject;
        private int closeUnits;

        public Requisite(
                @NonNull SecurityId securityId,
                @NonNull OwnedPortfolioId applicablePortfolioId,
                int closeUnits)
        {
            this.securityId = securityId;
            this.applicablePortfolioIdSubject = BehaviorSubject.create(applicablePortfolioId);
            this.closeUnits = closeUnits;
        }

        public Requisite(@NonNull SecurityId securityId,
                @NonNull Bundle args)
        {
            this.securityId = securityId;
            this.applicablePortfolioIdSubject = BehaviorSubject.create(
                    getApplicablePortfolioId(args.getBundle(KEY_APPLICABLE_PORTFOLIO_ID)));
            this.closeUnits = getCloseUnits(args);
        }

        public Requisite(@NonNull Bundle args)
        {
            this.securityId = getSecurityId(args.getBundle(KEY_SECURITY_ID));
            this.applicablePortfolioIdSubject = BehaviorSubject.create(
                    getApplicablePortfolioId(args.getBundle(KEY_APPLICABLE_PORTFOLIO_ID)));
            this.closeUnits = getCloseUnits(args);
        }

        @NonNull private static SecurityId getSecurityId(@Nullable Bundle securityArgs)
        {
            if (securityArgs != null)
            {
                return new SecurityId(securityArgs);
            }
            throw new NullPointerException("SecurityId cannot be null");
        }

        @NonNull private static OwnedPortfolioId getApplicablePortfolioId(@Nullable Bundle portfolioArgs)
        {
            if (portfolioArgs != null)
            {
                return new OwnedPortfolioId(portfolioArgs);
            }
            throw new NullPointerException("ApplicablePortfolioId cannot be null");
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
            args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
            args.putBundle(KEY_APPLICABLE_PORTFOLIO_ID, applicablePortfolioIdSubject.toBlocking().first().getArgs());
            args.putInt(KEY_CLOSE_UNITS_BUNDLE, closeUnits);
        }
    }
}
