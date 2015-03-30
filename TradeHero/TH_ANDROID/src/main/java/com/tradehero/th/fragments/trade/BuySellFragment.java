package com.tradehero.th.fragments.trade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.android.internal.util.Predicate;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.key.PortfolioCompactListKey;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.trade.view.PortfolioSelectorView;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.dto.SocialShareResult;
import com.tradehero.th.persistence.portfolio.OwnedPortfolioIdListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;

abstract public class BuySellFragment extends AbstractBuySellFragment
        implements WithTutorial
{
    public static final String EVENT_CHART_IMAGE_CLICKED = BuySellFragment.class.getName() + ".chartButtonClicked";

    public static final int MS_DELAY_FOR_BG_IMAGE = 200;

    public static final boolean DEFAULT_IS_SHARED_TO_WECHAT = false;

    @InjectView(R.id.portfolio_selector_container) PortfolioSelectorView mSelectedPortfolioContainer;
    @Nullable Subscription portfolioMenuSubscription;
    @InjectView(R.id.market_closed_icon) protected ImageView mMarketClosedIcon;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject BroadcastUtils broadcastUtils;

    @InjectView(R.id.quote_refresh_countdown) protected ProgressBar mQuoteRefreshProgressBar;
    @InjectView(R.id.bottom_button) protected ViewGroup mBuySellBtnContainer;
    @InjectView(R.id.btn_buy) protected Button mBuyBtn;
    @InjectView(R.id.btn_sell) protected Button mSellBtn;

    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject PortfolioCacheRx portfolioCache;
    protected Observable<PortfolioDTO> portfolioObservable;
    @Nullable protected Subscription portfolioCacheSubscription;
    @Nullable protected Subscription portfolioChangedSubscription;

    protected Animation progressAnimation;
    protected BroadcastReceiver chartImageButtonClickReceiver;

    @Nullable protected Subscription alertCompactListCacheSubscription;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    private AbstractTransactionDialogFragment abstractTransactionDialogFragment;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @Inject protected OwnedPortfolioIdListCacheRx ownedPortfolioIdListCache;
    @Nullable protected OwnedPortfolioIdList applicableOwnedPortfolioIds;
    @Nullable protected Subscription securityApplicableOwnedPortfolioIdListSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        setRetainInstance(true);
        chartImageButtonClickReceiver = createImageButtonClickBroadcastReceiver();
        mSelectedPortfolioContainer.setDefaultPortfolioId(getApplicablePortfolioId(getArguments()));

        mBuySellBtnContainer.setVisibility(View.GONE);

        progressAnimation = new Animation()
        {
            @Override protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t)
            {
                super.applyTransformation(interpolatedTime, t);
                mQuoteRefreshProgressBar.setProgress((int) (getMillisecondQuoteRefresh() * (1 - interpolatedTime)));
            }
        };
        progressAnimation.setDuration(getMillisecondQuoteRefresh());
        mQuoteRefreshProgressBar.setMax((int) getMillisecondQuoteRefresh());
        mQuoteRefreshProgressBar.setProgress((int) getMillisecondQuoteRefresh());
        mQuoteRefreshProgressBar.setAnimation(progressAnimation);

        listenToBuySellDialog();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        displayActionBarElements();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(chartImageButtonClickReceiver,
                        new IntentFilter(EVENT_CHART_IMAGE_CLICKED));

        if (abstractTransactionDialogFragment != null && abstractTransactionDialogFragment.getDialog() != null)
        {
            abstractTransactionDialogFragment.populateComment();
            abstractTransactionDialogFragment.getDialog().show();
        }

        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                mBuySellBtnContainer.setTranslationY(y);
            }
        });
    }

    @Override public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(chartImageButtonClickReceiver);
        dashboardTabHost.get().setOnTranslate(null);
        super.onPause();
    }

    @Override public void onStop()
    {
        unsubscribe(portfolioCacheSubscription);
        portfolioCacheSubscription = null;
        unsubscribe(alertCompactListCacheSubscription);
        alertCompactListCacheSubscription = null;
        unsubscribe(portfolioChangedSubscription);
        portfolioChangedSubscription = null;
        unsubscribe(portfolioMenuSubscription);
        portfolioMenuSubscription = null;
        unsubscribe(securityApplicableOwnedPortfolioIdListSubscription);
        securityApplicableOwnedPortfolioIdListSubscription = null;
        stopListeningToBuySellDialog();

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        mQuoteRefreshProgressBar.clearAnimation();
        progressAnimation = null;

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        unsubscribe(portfolioCacheSubscription);
        portfolioCacheSubscription = null;
        unsubscribe(alertCompactListCacheSubscription);
        alertCompactListCacheSubscription = null;
        unsubscribe(portfolioMenuSubscription);
        portfolioMenuSubscription = null;
    }

    @Override public void onDestroy()
    {
        chartImageButtonClickReceiver = null;
        abstractTransactionDialogFragment = null;
        portfolioObservable = null;
        super.onDestroy();
    }

    private void stopListeningToBuySellDialog()
    {
        AbstractTransactionDialogFragment dialogCopy = abstractTransactionDialogFragment;
        if (dialogCopy != null)
        {
            dialogCopy.setBuySellTransactionListener(null);
        }
    }

    private void listenToBuySellDialog()
    {
        if (abstractTransactionDialogFragment != null)
        {
            abstractTransactionDialogFragment.setBuySellTransactionListener(new AbstractStockTransactionDialogFragment.BuySellTransactionListener()
            {
                @Override public void onTransactionSuccessful(boolean isBuy,
                        @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO)
                {
                    showPrettyReviewAndInvite(isBuy);
                    pushPortfolioFragment(securityPositionTransactionDTO);
                }

                @Override public void onTransactionFailed(boolean isBuy, THException error)
                {
                    // TODO Toast error buy?
                }
            });
        }
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        if (andDisplay)
        {
            displayStockName();
            displayBuySellPrice();
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO)
    {
        super.linkWith(quoteDTO);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        displayBuySellPrice();
        displayBuySellContainer();
        displayBuySellSwitch();

        mQuoteRefreshProgressBar.startAnimation(progressAnimation);
    }

    protected void setInitialBuyQuantityIfCan()
    {
        if (mBuyQuantity == null)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                linkWithBuyQuantity((int) Math.ceil(((double) maxPurchasableShares) / 2));
            }
        }
    }

    protected void setInitialSellQuantityIfCan()
    {
        if (mSellQuantity == null)
        {
            Integer maxSellableShares = getMaxSellableShares();
            if (maxSellableShares != null)
            {
                linkWithSellQuantity(maxSellableShares);
                if (maxSellableShares == 0)
                {
                    setTransactionTypeBuy(true);
                }
            }
        }
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId,
            boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (purchaseApplicablePortfolioId != null)
        {
            portfolioObservable = portfolioCache.get(purchaseApplicablePortfolioId)
                    .map(new PairGetSecond<OwnedPortfolioId, PortfolioDTO>())
                    .publish()
                    .refCount();
            fetchPortfolio();
            conditionalDisplayPortfolioChanged(purchaseApplicablePortfolioId);
        }
        else
        {
            linkWith((PortfolioCompactDTO) null);
        }
        if (andDisplay)
        {
            displayBuySellSwitch();
        }
    }

    protected void fetchPortfolio()
    {
        unsubscribe(portfolioCacheSubscription);
        portfolioCacheSubscription = AppObservable.bindFragment(
                this,
                portfolioObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PortfolioDTO>()
                           {
                               @Override public void call(PortfolioDTO portfolioDTO)
                               {
                                   linkWith(portfolioDTO);
                               }
                           },
                        new EmptyAction1<Throwable>());
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        super.linkWith(portfolioCompactDTO);
        clampBuyQuantity();
        clampSellQuantity();
        // TODO max purchasable shares
        displayBuySellPrice();
        displayBuySellSwitch();
    }

    @Override public void linkWith(PositionDTOCompactList positionDTOCompacts)
    {
        super.linkWith(positionDTOCompacts);
        setInitialSellQuantityIfCan();
        displayBuySellSwitch();
        displayBuySellContainer();
    }

    public void displayStockName()
    {
        //if (securityCompactDTO != null)
        //{
        //    if (!StringUtils.isNullOrEmpty(securityCompactDTO.name))
        //    {
        //        setActionBarTitle(securityCompactDTO.name);
        //        setActionBarSubtitle(securityCompactDTO.getExchangeSymbol());
        //    }
        //    else
        //    {
        //        setActionBarTitle(securityCompactDTO.getExchangeSymbol());
        //        setActionBarSubtitle(null);
        //    }
        //}

        if (mMarketClosedIcon != null)
        {
            boolean marketIsOpen = securityCompactDTO == null
                    || securityCompactDTO.marketOpen == null
                    || securityCompactDTO.marketOpen;
            mMarketClosedIcon.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
        }
    }

    abstract public void displayBuySellPrice();

    public void displayActionBarElements()
    {
        displayBuySellSwitch();
    }

    public void displayBuySellContainer()
    {
        if (isBuySellReady() && mBuySellBtnContainer.getVisibility() == View.GONE)
        {
            Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
            slideIn.setFillAfter(true);
            mBuySellBtnContainer.setVisibility(View.VISIBLE);
            mBuySellBtnContainer.startAnimation(slideIn);
        }
    }

    abstract public boolean isBuySellReady();

    public void conditionalDisplayPortfolioChanged(@NonNull OwnedPortfolioId purchaseApplicablePortfolioId)
    {
        if (portfolioChangedSubscription == null)
        {
            portfolioChangedSubscription = Observable.combineLatest(
                    Observable.just(purchaseApplicablePortfolioId),
                    currentUserPortfolioCompactListObservable,
                    ownedPortfolioIdListCache.get(securityId).map(new PairGetSecond<PortfolioCompactListKey, OwnedPortfolioIdList>()),
                    new Func3<OwnedPortfolioId, PortfolioCompactDTOList, OwnedPortfolioIdList, Boolean>()
                    {
                        @Override public Boolean call(OwnedPortfolioId t1, PortfolioCompactDTOList t2,
                                OwnedPortfolioIdList t3)
                        {
                            return new PortfolioChangedHelper(t1, t2, t3).isPortfolioChanged();
                        }
                    })
                    .take(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Boolean>()
                            {
                                @Override public void call(Boolean isChanged)
                                {
                                    conditionalDisplayPortfolioChanged(isChanged);
                                }
                            },
                            new TimberOnErrorAction("Failed to check for portfolio changed")
                    );
        }
    }

    protected void conditionalDisplayPortfolioChanged(boolean isPortfolioChanged)
    {
        if (isPortfolioChanged)
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
    }

    private static class PortfolioChangedHelper
    {
        @NonNull public final OwnedPortfolioId applicable;
        @NonNull public final PortfolioCompactDTOList portfolioCompactDTOs;
        @NonNull public final OwnedPortfolioIdList applicablePortfolioIds;

        //<editor-fold desc="Constructors">
        private PortfolioChangedHelper(
                @NonNull OwnedPortfolioId applicable,
                @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
                @NonNull OwnedPortfolioIdList applicablePortfolioIds)
        {
            this.applicable = applicable;
            this.portfolioCompactDTOs = portfolioCompactDTOs;
            this.applicablePortfolioIds = applicablePortfolioIds;
        }
        //</editor-fold>

        @NonNull public Boolean isPortfolioChanged()
        {
            PortfolioCompactDTO defaultPortfolio = portfolioCompactDTOs.getDefaultPortfolio();
            if (defaultPortfolio != null && defaultPortfolio.getOwnedPortfolioId().equals(applicable))
            {
                // Default portfolio is without surprise
                return false;
            }
            return !applicablePortfolioIds.contains(applicable);
        }
    }

    public void displayBuySellSwitch()
    {
        if (mSellBtn != null)
        {
            mSellBtn.setVisibility(getSupportSell() ? View.VISIBLE : View.GONE);
        }
    }

    abstract protected boolean getSupportSell();

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (url.length() > 0);
    }

    @Override public void setTransactionTypeBuy(boolean transactionTypeBuy)
    {
        super.setTransactionTypeBuy(transactionTypeBuy);
        displayBuySellSwitch();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.portfolio_selector_container)
    protected void showPortfolioSelector()
    {
        unsubscribe(portfolioMenuSubscription);
        portfolioMenuSubscription = AppObservable.bindFragment(
                this,
                mSelectedPortfolioContainer.createMenuObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<MenuOwnedPortfolioId>()
                        {
                            public void call(MenuOwnedPortfolioId args)
                            {
                                linkWithApplicable(args, true);
                            }
                        },
                        new EmptyAction1<Throwable>());
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.btn_buy, R.id.btn_sell})
    protected void handleBuySellButtonsClicked(View view)
    {
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
        showBuySellDialog(0);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.market_closed_icon)
    protected void handleMarketClosedIconClicked()
    {
        notifyMarketClosed();
    }

    public void showBuySellDialog(int closeUnits)
    {
        if (abstractTransactionDialogFragment != null
                && abstractTransactionDialogFragment.isVisible())
        {
            return;//buy/sell dialog already shows
        }
        if (quoteDTO != null
                && BuyStockDialogFragment.canShowDialog(quoteDTO, isTransactionTypeBuy))
        {
            OwnedPortfolioId currentMenu = mSelectedPortfolioContainer.getCurrentMenu();
            if (currentMenu != null)
            {
                if (securityCompactDTO instanceof FxSecurityCompactDTO)
                {
                    abstractTransactionDialogFragment = AbstractFXTransactionDialogFragment.newInstance(
                            securityId,
                            currentMenu.getPortfolioIdKey(),
                            quoteDTO,
                            isTransactionTypeBuy,
                            closeUnits);
                }
                else
                {
                    abstractTransactionDialogFragment = AbstractStockTransactionDialogFragment.newInstance(
                            securityId,
                            currentMenu.getPortfolioIdKey(),
                            quoteDTO,
                            isTransactionTypeBuy);
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

    public void shareToWeChat()
    {
        //TODO Move this!
        if (socialSharePreferenceHelperNew.isShareEnabled(SocialNetworkEnum.WECHAT, DEFAULT_IS_SHARED_TO_WECHAT))
        {
            WeChatDTO weChatDTO = new WeChatDTO();
            weChatDTO.id = securityCompactDTO.id;
            weChatDTO.type = WeChatMessageType.Trade;
            if (isMyUrlOk())
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
            socialSharerLazy.get().share(weChatDTO)
                    .subscribe(
                            new EmptyAction1<SocialShareResult>(),
                            new EmptyAction1<Throwable>()); // TODO proper callback?
        }
    }

    private void pushPortfolioFragment(@NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO)
    {
        pushPortfolioFragment(new OwnedPortfolioId(
                currentUserId.get(),
                securityPositionTransactionDTO.portfolio.id));
    }

    private void pushPortfolioFragment(OwnedPortfolioId ownedPortfolioId)
    {
        shareToWeChat();
        if (isResumed())
        {
            DeviceUtil.dismissKeyboard(getActivity());

            // TODO find a better way to remove this fragment from the stack
            navigator.get().popFragment();

            Bundle args = new Bundle();
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
            PositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
            navigator.get().pushFragment(PositionListFragment.class, args);
        }
    }

    private BroadcastReceiver createImageButtonClickBroadcastReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                pushStockInfoFragmentIn();
            }
        };
    }

    private void pushStockInfoFragmentIn()
    {
        Bundle args = new Bundle();
        args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, this.securityId.getArgs());
        if (providerId != null)
        {
            args.putBundle(StockInfoFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE,
                    providerId.getArgs());
        }
        navigator.get().pushFragment(StockInfoFragment.class, args);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_buy_sell;
    }

    @Override protected void softFetchPortfolioCompactList()
    {
        // Force a proper fetch
        fetchPortfolioCompactList();
    }

    @Override protected void handleReceivedPortfolioCompactList(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        super.handleReceivedPortfolioCompactList(portfolioCompactDTOs);
        fetchSecurityApplicableOwnedPortfolioIds(portfolioCompactDTOs);
        linkWith(portfolioCompactDTOs);
    }

    protected void fetchSecurityApplicableOwnedPortfolioIds(@NonNull final PortfolioCompactDTOList portfolioCompactDTOs)
    {
        unsubscribe(securityApplicableOwnedPortfolioIdListSubscription);
        securityApplicableOwnedPortfolioIdListSubscription = AppObservable.bindFragment(
                this,
                ownedPortfolioIdListCache.get(securityId)
                        .map(new PairGetSecond<PortfolioCompactListKey, OwnedPortfolioIdList>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<OwnedPortfolioIdList>()
                        {
                            @Override public void call(OwnedPortfolioIdList ids)
                            {
                                applicableOwnedPortfolioIds = ids;
                                PortfolioCompactDTO candidate;
                                for (final OwnedPortfolioId id : ids)
                                {
                                    candidate = portfolioCompactDTOs.findFirstWhere(new Predicate<PortfolioCompactDTO>()
                                    {
                                        @Override public boolean apply(PortfolioCompactDTO compact)
                                        {
                                            return compact.id == id.portfolioId;
                                        }
                                    });
                                    if (candidate != null)
                                    {
                                        mSelectedPortfolioContainer.addMenuOwnedPortfolioId(
                                                new MenuOwnedPortfolioId(id.getUserBaseKey(), candidate));
                                    }
                                }
                                BuySellFragment.this.displayBuySellContainer();
                            }
                        },
                        new TimberOnErrorAction("Failed to get the applicable portfolio ids"));
    }

    protected abstract void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs);
}
