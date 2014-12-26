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
import android.view.MenuInflater;
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
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
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
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import timber.log.Timber;

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
    @Nullable protected Subscription quoteTimerSubscription;

    @Nullable protected Subscription alertCompactListCacheSubscription;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    private AbstractTransactionDialogFragment abstractTransactionDialogFragment;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        chartImageButtonClickReceiver = createImageButtonClickBroadcastReceiver();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mSelectedPortfolioContainer.setDefaultPortfolioId(getApplicablePortfolioId(getArguments()));

        mBuySellBtnContainer.setVisibility(View.GONE);

        progressAnimation = new Animation()
        {
            @Override protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t)
            {
                super.applyTransformation(interpolatedTime, t);
                mQuoteRefreshProgressBar.setProgress((int) (MILLISEC_QUOTE_REFRESH * (1 - interpolatedTime)));
            }
        };
        progressAnimation.setDuration(MILLISEC_QUOTE_REFRESH);
        mQuoteRefreshProgressBar.setMax((int) MILLISEC_QUOTE_REFRESH);
        mQuoteRefreshProgressBar.setProgress((int) MILLISEC_QUOTE_REFRESH);
        mQuoteRefreshProgressBar.setAnimation(progressAnimation);

        listenToBuySellDialog();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.buy_sell_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

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

        dashboardTabHost.get().setOnTranslate((x, y) -> mBuySellBtnContainer.setTranslationY(y));
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
        unsubscribe(quoteTimerSubscription);
        quoteTimerSubscription = null;
        unsubscribe(portfolioCacheSubscription);
        portfolioCacheSubscription = null;
        unsubscribe(alertCompactListCacheSubscription);
        alertCompactListCacheSubscription = null;
        unsubscribe(portfolioChangedSubscription);
        portfolioChangedSubscription = null;
        detachPortfolioMenuSubscription();
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
        unsubscribe(quoteTimerSubscription);
        quoteTimerSubscription = null;
        unsubscribe(portfolioCacheSubscription);
        portfolioCacheSubscription = null;
        unsubscribe(alertCompactListCacheSubscription);
        alertCompactListCacheSubscription = null;
        detachPortfolioMenuSubscription();
    }

    @Override public void onDestroy()
    {
        chartImageButtonClickReceiver = null;
        abstractTransactionDialogFragment = null;
        portfolioObservable = null;
        super.onDestroy();
    }

    private void detachPortfolioMenuSubscription()
    {
        unsubscribe(portfolioMenuSubscription);
        portfolioMenuSubscription = null;
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

    @Override public void linkWith(@NonNull final SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        super.linkWith(securityPositionDetailDTO);

        ProviderDTOList providerDTOs = securityPositionDetailDTO.providers;
        if (providerDTOs != null)
        {
            for (ProviderDTO providerDTO : providerDTOs)
            {
                if (providerDTO.associatedPortfolio != null)
                {
                    processPortfolioForProvider(providerDTO);
                }
            }
        }

        setInitialSellQuantityIfCan();

        displayBuySellSwitch();
        displayBuySellPrice();
        displayBuySellContainer();
    }

    protected void processPortfolioForProvider(ProviderDTO providerDTO)
    {
        mSelectedPortfolioContainer.addMenuOwnedPortfolioId(
                new MenuOwnedPortfolioId(
                        currentUserId.toUserBaseKey(),
                        providerDTO.associatedPortfolio));
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
    }

    @Override protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        super.linkWith(quoteDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        if (andDisplay)
        {
            displayBuySellPrice();
            displayBuySellContainer();
            displayBuySellSwitch();
        }

        mQuoteRefreshProgressBar.startAnimation(progressAnimation);
    }

    protected void setInitialBuyQuantityIfCan()
    {
        if (mBuyQuantity == null)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                linkWithBuyQuantity((int) Math.ceil(((double) maxPurchasableShares) / 2), true);
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
                linkWithSellQuantity(maxSellableShares, true);
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
                    .map(pair -> pair.second)
                    .publish()
                    .refCount();
            fetchPortfolio(purchaseApplicablePortfolioId);
            conditionalDisplayPortfolioChanged(purchaseApplicablePortfolioId);
        }
        else
        {
            linkWith((PortfolioCompactDTO) null, andDisplay);
        }
        if (andDisplay)
        {
            displayBuySellSwitch();
        }
    }

    protected void fetchPortfolio(OwnedPortfolioId purchaseApplicablePortfolioId)
    {
        unsubscribe(portfolioMenuSubscription);
        portfolioCacheSubscription = AndroidObservable.bindFragment(
                this,
                portfolioObservable)
                .subscribe(new EmptyObserver<PortfolioDTO>()
                {
                    @Override public void onNext(PortfolioDTO portfolioDTO)
                    {
                        linkWith(portfolioDTO, true);
                    }
                });
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        super.linkWith(portfolioCompactDTO, andDisplay);
        clampBuyQuantity(andDisplay);
        clampSellQuantity(andDisplay);
        if (andDisplay)
        {
            // TODO max purchasable shares
            displayBuySellPrice();
            displayBuySellSwitch();
        }
    }

    @Override public void display()
    {
        displayActionBarElements();
        displayPageElements();
    }

    public void displayPageElements()
    {
        displayBuySellPrice();
        displayStockName();
    }

    public void displayStockName()
    {
        if (securityCompactDTO != null)
        {
            setActionBarTitle(securityCompactDTO.name);
            setActionBarSubtitle(securityCompactDTO.getExchangeSymbol());
        }

        if (mMarketClosedIcon != null)
        {
            boolean marketIsOpen = securityCompactDTO == null
                    || securityCompactDTO.marketOpen == null
                    || securityCompactDTO.marketOpen;
            mMarketClosedIcon.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
        }
    }

    public void displayPositionStatus()
    {

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
                    securityPositionDetailObservable,
                    PortfolioChangedHelper::new)
                    .map(PortfolioChangedHelper::isPortfolioChanged)
                    .take(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::conditionalDisplayPortfolioChanged,
                            e -> Timber.e(e, "Failed to check for portfolio changed")
                    );
        }
    }

    protected void conditionalDisplayPortfolioChanged(boolean isPortfolioChanged)
    {
        if (isPortfolioChanged)
        {
            alertDialogUtil.popWithNegativeButton(getActivity(),
                    R.string.buy_sell_portfolio_changed_title,
                    R.string.buy_sell_portfolio_changed_message,
                    R.string.ok);
        }
    }

    private static class PortfolioChangedHelper
    {
        @NonNull public final OwnedPortfolioId applicable;
        @NonNull public final PortfolioCompactDTOList portfolioCompactDTOs;
        @NonNull public final SecurityPositionDetailDTO securityPositionDetailDTO;

        //<editor-fold desc="Constructors">
        private PortfolioChangedHelper(
                @NonNull OwnedPortfolioId applicable,
                @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
                @NonNull SecurityPositionDetailDTO securityPositionDetailDTO)
        {
            this.applicable = applicable;
            this.portfolioCompactDTOs = portfolioCompactDTOs;
            this.securityPositionDetailDTO = securityPositionDetailDTO;
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
            ProviderDTOList providers = securityPositionDetailDTO.providers;
            if (providers == null)
            {
                return true;
            }
            OwnedPortfolioIdList providerPortfolioIds = providers.getAssociatedOwnedPortfolioIds();
            return !providerPortfolioIds.contains(applicable);
        }
    }

    public void displayBuySellSwitch()
    {
        if (mSellBtn != null)
        {
            mSellBtn.setVisibility(getSupportSell() ? View.VISIBLE : View.GONE);
        }
<<<<<<< HEAD
=======

        displayPositionStatus();
>>>>>>> origin/develop
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
        detachPortfolioMenuSubscription();
        portfolioMenuSubscription = AndroidObservable.bindFragment(
                this,
                mSelectedPortfolioContainer.createMenuObservable())
                .subscribe(new EmptyObserver<MenuOwnedPortfolioId>()
                {
                    @Override public void onNext(MenuOwnedPortfolioId args)
                    {
                        super.onNext(args);
                        linkWithApplicable(args, true);
                    }
                });
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
        showBuySellDialog();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.market_closed_icon)
    protected void handleMarketClosedIconClicked()
    {
        notifyMarketClosed();
    }

    public void showBuySellDialog()
    {
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
                            isTransactionTypeBuy);
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
                alertDialogUtil.popWithNegativeButton(
                        getActivity(),
                        R.string.buy_sell_no_portfolio_title,
                        R.string.buy_sell_no_portfolio_message,
                        R.string.buy_sell_no_portfolio_cancel);
            }
        }
        else
        {
            alertDialogUtil.popWithNegativeButton(
                    getActivity(),
                    R.string.buy_sell_no_quote_title,
                    R.string.buy_sell_no_quote_message,
                    R.string.buy_sell_no_quote_cancel);
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
            socialSharerLazy.get().share(weChatDTO); // TODO proper callback?
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

    @Override @NonNull protected Observer<PortfolioCompactDTOList> createCurrentUserPortfolioCompactListObserver()
    {
        return new BuySellPortfolioCompactListObserver();
    }

    protected class BuySellPortfolioCompactListObserver extends BasePurchaseManagementPortfolioCompactListObserver
    {
        @Override public void onNext(PortfolioCompactDTOList list)
        {
            super.onNext(list);
            PortfolioCompactDTO defaultPortfolio = list.getDefaultPortfolio();
            if (defaultPortfolio != null)
            {
                mSelectedPortfolioContainer.addMenuOwnedPortfolioId(new MenuOwnedPortfolioId(currentUserId.toUserBaseKey(), defaultPortfolio));
            }
            setInitialSellQuantityIfCan();
        }
    }
}
