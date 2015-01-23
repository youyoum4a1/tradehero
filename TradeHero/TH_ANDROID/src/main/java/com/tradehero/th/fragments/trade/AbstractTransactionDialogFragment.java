package com.tradehero.th.fragments.trade;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.billing.THPurchaser;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.fragments.discussion.SecurityDiscussionEditPostFragment;
import com.tradehero.th.fragments.discussion.TransactionEditCommentFragment;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import dagger.Lazy;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

abstract public class AbstractTransactionDialogFragment extends BaseShareableDialogFragment
{
    private static final String KEY_SECURITY_ID = AbstractTransactionDialogFragment.class.getName() + ".security_id";
    private static final String KEY_PORTFOLIO_ID = AbstractTransactionDialogFragment.class.getName() + ".portfolio_id";
    private static final String KEY_QUOTE_DTO = AbstractTransactionDialogFragment.class.getName() + ".quote_dto";

    @InjectView(R.id.dialog_stock_name) protected TextView mStockNameTextView;
    @InjectView(R.id.vcash_left) protected TextView mCashShareLeftTextView;
    @InjectView(R.id.dialog_cash_left) protected TextView mCashShareLeftLabelTextView;
    @InjectView(R.id.vtrade_value) protected TextView mTradeValueTextView;
    @InjectView(R.id.dialog_price) protected TextView mStockPriceTextView;
    @InjectView(R.id.dialog_portfolio) protected TextView mPortfolioTextView;
    @InjectView(R.id.dialog_profit_and_loss) protected TextView mProfitLossView;

    @InjectView(R.id.seek_bar) protected SeekBar mSeekBar;
    @InjectView(R.id.quick_price_button_set) protected QuickPriceButtonSet mQuickPriceButtonSet;

    @InjectView(R.id.vquantity) protected EditText mQuantityEditText;
    @InjectView(R.id.comments) protected TextView mCommentsEditText;

    @InjectView(R.id.dialog_btn_add_cash) protected ImageButton mBtnAddCash;
    @InjectView(R.id.dialog_btn_confirm) protected Button mConfirm;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject PortfolioCompactCacheRx portfolioCompactCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject AlertDialogUtilBuySell alertDialogUtilBuySell;
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache;
    @Inject PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject Analytics analytics;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    @Inject ToastOnErrorAction toastOnErrorAction;

    @Inject THBillingInteractor userInteractor;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject Provider<BaseTHUIBillingRequest.Builder> uiBillingRequestBuilderProvider;

    protected ProgressDialog mTransactionDialog;

    @NonNull protected final SubscriptionList subscriptions;
    protected Subscription buySellSubscription;
    protected SecurityId securityId;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected PortfolioCompactDTOList portfolioCompactDTOs;
    protected PortfolioId portfolioId;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    protected QuoteDTO quoteDTO;
    protected Integer mTransactionQuantity = 0;
    @Nullable protected SecurityPositionDetailDTO securityPositionDetailDTO;
    @Nullable protected PositionDTOCompactList positionDTOCompactList;
    @Nullable protected PositionDTOCompact positionDTOCompact;
    protected boolean showProfitLossUsd = true; // false will show in RefCcy

    protected BuySellTransactionListener buySellTransactionListener;

    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;
    private TextWatcher mQuantityTextWatcher;
    private TransactionEditCommentFragment transactionCommentFragment;
    Editable unSpannedComment;
    private Integer purchaseRequestCode;

    @Nullable protected abstract Integer getMaxValue();

    protected abstract boolean hasValidInfo();

    protected abstract boolean isQuickButtonEnabled();

    protected abstract double getQuickButtonMaxValue();

    abstract protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO);

    public abstract Double getPriceCcy();

    public static boolean canShowDialog(@NonNull QuoteDTO quoteDTO, boolean isBuy)
    {
        return (isBuy && quoteDTO.ask != null) ||
                (!isBuy && quoteDTO.bid != null);
    }

    protected AbstractTransactionDialogFragment()
    {
        super();
        subscriptions = new SubscriptionList();
    }

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
    }

    @NonNull protected SecurityId getSecurityId()
    {
        if (this.securityId == null)
        {
            this.securityId = new SecurityId(getArguments().getBundle(KEY_SECURITY_ID));
        }
        return securityId;
    }

    public static void putPortfolioId(@NonNull Bundle args, @NonNull PortfolioId portfolioId)
    {
        args.putBundle(KEY_PORTFOLIO_ID, portfolioId.getArgs());
    }

    @NonNull protected PortfolioId getPortfolioId()
    {
        if (this.portfolioId == null)
        {
            this.portfolioId = new PortfolioId(getArguments().getBundle(KEY_PORTFOLIO_ID));
        }
        return portfolioId;
    }

    public static void putQuoteDTO(@NonNull Bundle args, @NonNull QuoteDTO quoteDTO)
    {
        args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
    }

    @NonNull private QuoteDTO getBundledQuoteDTO()
    {
        return new QuoteDTO(getArguments().getBundle(KEY_QUOTE_DTO));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.security_buy_sell_dialog, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mQuantityEditText.setText(String.valueOf(mTransactionQuantity));
        mQuantityEditText.addTextChangedListener(getQuantityTextChangeListener());
        mQuantityEditText.setCustomSelectionActionModeCallback(createActionModeCallBackForQuantityEditText());
        mQuantityEditText.setOnEditorActionListener((textView, i, keyEvent) -> false);

        mCashShareLeftLabelTextView.setText(getCashLeftLabelResId());

        mSeekBar.setOnSeekBarChangeListener(createSeekBarListener());

        mBtnAddCash.setOnClickListener(ignored -> {
            DeviceUtil.dismissKeyboard(mCommentsEditText);
            handleBtnAddCashPressed();
        });

        displayAddCashButton();
    }

    @Override public void onStart()
    {
        super.onStart();
        initFetches();
        attachQuickPriceButtonSet();
    }

    @Override public void onResume()
    {
        super.onResume();

        /** To make sure that the dialog will not show when active dashboard fragment is not BuySellFragment */
        if (!(navigator.get().getCurrentFragment() instanceof AbstractBuySellFragment))
        {
            getDialog().hide();
        }

        fetchPortfolioCompactList();
    }

    @Override public void onDetach()
    {
        transactionCommentFragment = null;
        super.onDetach();
    }

    @Override public void onStop()
    {
        subscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        mQuantityEditText.removeTextChangedListener(mQuantityTextWatcher);
        mQuantityTextWatcher = null;
        detachPurchaseRequestCode();
        destroyTransactionDialog();
        unsubscribe(buySellSubscription);
        buySellSubscription = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        securityCompactDTO = null;
        portfolioCompactDTO = null;
        quoteDTO = null;
        positionDTOCompactList = null;
        super.onDestroy();
    }

    //<editor-fold desc="Fetches">
    private void initFetches()
    {
        fetchSecurityCompact();
        fetchPortfolioCompact();
        quoteDTO = getBundledQuoteDTO();
        fetchQuote();
        fetchSecurityPositionDetail();
    }

    private void fetchSecurityCompact()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                securityCompactCache.get(getSecurityId()))
                .take(1)
                .subscribe(pair -> this.linkWith(pair.second),
                        error -> {
                        }));
    }

    protected void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        initSecurityRelatedInfo();
    }

    private void fetchPortfolioCompact()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                portfolioCompactCache.get(getPortfolioId())
                        .map(pair -> pair.second))
                .subscribe(
                        this::linkWith,
                        error -> {
                        }));
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        setPositionDTO();
        initPortfolioRelatedInfo();
    }

    private void fetchQuote()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                quoteServiceWrapper.getQuoteRx(securityId)
                        .repeatWhen(observable -> observable.delay(5000, TimeUnit.MILLISECONDS)))
                .subscribe(
                        this::linkWith,
                        toastOnErrorAction));
    }

    protected void linkWith(QuoteDTO quoteDTO)
    {
        this.quoteDTO = quoteDTO;
        initSecurityRelatedInfo();
        initPortfolioRelatedInfo();
        updateProfitLoss();
        updateTransactionDialog();
        displayCashShareLabel();
    }

    private void fetchSecurityPositionDetail()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                securityPositionDetailCache.get()
                        .get(this.securityId)
                        .map(pair -> pair.second))
                .subscribe(
                        this::linkWith,
                        toastOnErrorAction));
    }

    protected void linkWith(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        this.positionDTOCompactList = securityPositionDetailDTO.positions;
        setPositionDTO();
        initPortfolioRelatedInfo();
        clampQuantity();
        displayCashShareLabel();
    }

    protected void fetchPortfolioCompactList()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                        .map(pair -> pair.second))
                .subscribe(
                        this::linkWith,
                        error -> {
                            Timber.e(error, "Failed fetching the list of porfolios");
                            THToast.show(R.string.error_fetch_portfolio_list_info);
                        }));
    }

    protected void linkWith(PortfolioCompactDTOList value)
    {
        this.portfolioCompactDTOs = value;
        portfolioCompactDTO = value.findFirstWhere(portfolioCompactDTO1 -> portfolioCompactDTO1.getPortfolioId().equals(getPortfolioId()));
        updateTransactionDialog();
        displayAddCashButton();
        displayCashShareLabel();
    }

    protected void setPositionDTO()
    {
        if (positionDTOCompactList != null && portfolioCompactDTO != null)
        {
            this.positionDTOCompact = positionDTOCompactList.findFirstWhere(position -> position.portfolioId == portfolioCompactDTO.id);
        }
    }
    //</editor-fold>

    private void initSecurityRelatedInfo()
    {
        if (securityCompactDTO != null )
        {
            if(!StringUtils.isNullOrEmpty(securityCompactDTO.name))
            {
                mStockNameTextView.setText(securityCompactDTO.name);
            }
            else
            {
                mStockNameTextView.setText(securityCompactDTO.getExchangeSymbol());
            }
        }
        else
        {
            mStockNameTextView.setText("-");
        }
        mStockPriceTextView.setText(String.valueOf(getLabel()));
    }

    protected void initPortfolioRelatedInfo()
    {
        mPortfolioTextView.setText(
                getString(R.string.buy_sell_portfolio_selected_title) + " " + (portfolioCompactDTO == null ? "-" : portfolioCompactDTO.title));

        updateProfitLoss();

        Integer maxValue = getMaxValue();
        if (maxValue != null)
        {
            mSeekBar.setMax(maxValue);
            mSeekBar.setEnabled(maxValue > 0);
        }
        else
        {
            mSeekBar.setMax(0);
        }
        displayQuickPriceButtonSet();
        updateTransactionDialog();
    }

    protected void displayCashShareLabel()
    {
        if (mCashShareLeftLabelTextView != null)
        {
            mCashShareLeftLabelTextView.setText(getCashLeftLabelResId());
        }
    }

    protected abstract void displayQuickPriceButtonSet();

    protected void dismissTransactionProgress()
    {
        if (mTransactionDialog != null)
        {
            mTransactionDialog.dismiss();
        }
        mTransactionDialog = null;
    }

    protected void detachPurchaseRequestCode()
    {
        if (purchaseRequestCode != null)
        {
            userInteractor.forgetRequestCode(purchaseRequestCode);
        }
        purchaseRequestCode = null;
    }

    protected abstract String getLabel();

    protected abstract int getCashLeftLabelResId();

    @Nullable abstract protected Boolean isClosingPosition();

    @Nullable protected Integer getMaxPurchasableShares()
    {
        if (securityPositionDetailDTO == null)
        {
            // This means we have incomplete information
            return null;
        }
        return portfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO,
                positionDTOCompact);
    }

    @Nullable protected Integer getMaxSellableShares()
    {
        if (securityPositionDetailDTO == null)
        {
            // This means we have incomplete information
            return null;
        }
        return portfolioCompactDTOUtil.getMaxSellableShares(
                portfolioCompactDTO,
                quoteDTO,
                positionDTOCompact);
    }

    @Nullable protected Double getRemainingForPurchaseInPortfolioRefCcy()
    {
        QuoteDTO quoteInPortfolioCcy = portfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
        if (quoteInPortfolioCcy != null
                && quoteInPortfolioCcy.ask != null
                && portfolioCompactDTO != null)
        {
            double available = portfolioCompactDTO.getUsableForTransactionRefCcy();
            double value = mTransactionQuantity * quoteInPortfolioCcy.ask;
            return available - value;
        }
        return null;
    }

    @Nullable protected Double getRemainingForShortingInPortfolioRefCcy()
    {
        QuoteDTO quoteInPortfolioCcy = portfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
        if (quoteInPortfolioCcy != null
                && quoteInPortfolioCcy.bid != null
                && portfolioCompactDTO != null)
        {
            double available = portfolioCompactDTO.getUsableForTransactionRefCcy();
            double value = mTransactionQuantity * quoteInPortfolioCcy.bid;
            return available - value;
        }
        return null;
    }

    @NonNull public String getRemainingWhenBuy()
    {
        String cashLeftText = null;
        Boolean isClosing = isClosingPosition();
        if (isClosing != null && isClosing)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null && maxPurchasableShares != 0)
            {
                cashLeftText = THSignedNumber.builder(maxPurchasableShares - mTransactionQuantity)
                        .relevantDigitCount(1)
                        .withOutSign()
                        .build().toString();
            }
        }
        else if (isClosing != null)
        {
            Double remaining = getRemainingForPurchaseInPortfolioRefCcy();
            if (remaining != null && portfolioCompactDTO != null)
            {
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0)
                {
                    remaining /= portfolioCompactDTO.leverage;
                }
                THSignedNumber thSignedNumber = THSignedMoney
                        .builder(remaining)
                        .withOutSign()
                        .currency(portfolioCompactDTO.currencyDisplay)
                        .build();
                cashLeftText = thSignedNumber.toString();
            }
        }

        if (cashLeftText == null)
        {
            cashLeftText = getResources().getString(R.string.na);
        }

        return cashLeftText;
    }

    @NonNull public String getRemainingWhenSell()
    {
        String shareLeftText = null;
        Boolean isClosing = isClosingPosition();
        if (isClosing != null && isClosing)
        {
            Integer maxSellableShares = getMaxSellableShares();
            if (maxSellableShares != null && maxSellableShares != 0)
            {
                shareLeftText = THSignedNumber.builder(maxSellableShares - mTransactionQuantity)
                        .relevantDigitCount(1)
                        .withOutSign()
                        .build().toString();
            }
        }
        else if (isClosing != null)
        {
            Double remaining = getRemainingForShortingInPortfolioRefCcy();
            if (remaining != null && portfolioCompactDTO != null)
            {
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0)
                {
                    remaining /= portfolioCompactDTO.leverage;
                }
                THSignedNumber thSignedNumber = THSignedMoney
                        .builder(remaining)
                        .withOutSign()
                        .currency(portfolioCompactDTO.currencyDisplay)
                        .build();
                shareLeftText = thSignedNumber.toString();
            }
        }

        if (shareLeftText == null)
        {
            shareLeftText = getResources().getString(R.string.na);
        }
        return shareLeftText;
    }

    public String getTradeValueText()
    {
        String valueText = "-";
        if (quoteDTO != null)
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                double value = mTransactionQuantity * priceRefCcy;
                THSignedNumber thTradeValue = THSignedMoney.builder(value)
                        .withOutSign()
                        .currency(portfolioCompactDTO.currencyDisplay)
                        .build();
                valueText = thTradeValue.toString();
            }
        }
        return valueText;
    }

    public Integer getQuantity()
    {
        return mTransactionQuantity;
    }

    public String getQuantityString()
    {
        return mQuantityEditText.getText().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.vquantity)
    public void onQuantityClicked(View v)
    {
        mPriceSelectionMethod = AnalyticsConstants.ManualQuantityInput;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_btn_cancel)
    public void onCancelClicked(View v)
    {
        getDialog().dismiss();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_btn_confirm)
    public void onConfirmClicked(View v)
    {
        updateConfirmButton(true);
        saveShareSettings();
        fireBuySellReport();
        launchBuySell();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.comments) void onCommentAreaClicked(View commentTextBox)
    {
        Bundle bundle = new Bundle();
        SecurityDiscussionEditPostFragment.putSecurityId(bundle, securityId);
        transactionCommentFragment = navigator.get().pushFragment(TransactionEditCommentFragment.class, bundle);

        getDialog().hide();
    }

    public void setBuySellTransactionListener(BuySellTransactionListener buySellTransactionListener)
    {
        this.buySellTransactionListener = buySellTransactionListener;
    }

    public void displayAddCashButton()
    {
        if (mBtnAddCash != null)
        {
            mBtnAddCash.setVisibility(
                    (portfolioCompactDTO != null && portfolioCompactDTO.isAllowedAddCash())
                            ? View.VISIBLE
                            : View.GONE);
        }
    }

    public void handleBtnAddCashPressed()
    {
        detachPurchaseRequestCode();
        //noinspection unchecked
        purchaseRequestCode = userInteractor.run(uiBillingRequestBuilderProvider.get()
                .applicablePortfolioId(new OwnedPortfolioId(currentUserId.get(), portfolioId.key))
                .domainToPresent(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR)
                .purchaseReportedListener(createPurchaseReportedListener())
                .purchaseFinishedListener(createPurchaseFinishedListener())
                .startWithProgressDialog(true)
                .build());
    }

    public void updateTransactionDialog()
    {
        updateSeekBar();
        updateQuantityView();
        updateProfitLoss();
        updateTradeValueAndCashShareLeft();
        updateConfirmButton(false);
    }

    private void updateQuantityView()
    {
        mQuantityEditText.setText(String.valueOf(mTransactionQuantity));
        mQuantityEditText.setSelection(mQuantityEditText.getText().length());
    }

    private void updateSeekBar()
    {
        mSeekBar.setProgress(mTransactionQuantity);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_profit_and_loss)
    protected void toggleProfitLossUsdRefCcy()
    {
        this.showProfitLossUsd = !showProfitLossUsd;
        updateProfitLoss();
    }

    private void updateProfitLoss()
    {
        mProfitLossView.setVisibility(getProfitOrLossUsd() == null ? View.GONE : View.VISIBLE);
        Double profitLoss = showProfitLossUsd ? getProfitOrLossUsd() : getProfitOrLossUsd();
        if (profitLoss != null && mTransactionQuantity != null && mTransactionQuantity > 0 && quoteDTO != null)
        {
            int stringResId = profitLoss < 0 ? R.string.buy_sell_sell_loss : R.string.buy_sell_sell_profit;
            mProfitLossView.setText(
                    getString(
                            stringResId,
                            THSignedMoney.builder(profitLoss)
                                    .withOutSign()
                                    .currency(showProfitLossUsd ? null : null)
                                    .build().toString()));
        }
        else
        {
            mProfitLossView.setText(getString(R.string.buy_sell_sell_loss, "--"));
        }
    }

    @Nullable protected abstract Double getProfitOrLossUsd();  // TODO do a getProfitOrLossPortfolioCcy

    private void updateTradeValueAndCashShareLeft()
    {
        mCashShareLeftTextView.setText(getCashShareLeft());
        mTradeValueTextView.setText(getTradeValueText());
    }

    @NonNull public abstract String getCashShareLeft();

    protected void updateConfirmButton(boolean forceDisable)
    {
        if (forceDisable)
        {
            mConfirm.setEnabled(false);
        }
        else
        {
            mConfirm.setEnabled(mTransactionQuantity != 0 && hasValidInfo());
        }
    }

    protected void clampQuantity()
    {
        linkWithQuantity(mTransactionQuantity);
    }

    protected void linkWithQuantity(Integer quantity)
    {
        this.mTransactionQuantity = clampedQuantity(quantity);
        updateTransactionDialog();
    }

    protected Integer clampedQuantity(Integer candidate)
    {
        Integer maxTransactionValue = getMaxValue();
        if (candidate == null || maxTransactionValue == null)
        {
            return 0;
        }
        return Math.min(candidate, maxTransactionValue);
    }

    private boolean checkValidToTransact()
    {
        return securityId != null && securityId.getExchange() != null
                && securityId.getSecuritySymbol() != null;
    }

    private void launchBuySell()
    {
        if (checkValidToTransact())
        {
            TransactionFormDTO transactionFormDTO = getBuySellOrder();
            if (transactionFormDTO != null)
            {
                dismissTransactionProgress();
                mTransactionDialog = progressDialogUtil.show(getActivity(),
                        R.string.processing, R.string.alert_dialog_please_wait);

                unsubscribe(buySellSubscription);
                buySellSubscription = getTransactionSubscription(transactionFormDTO);
            }
            else
            {
                alertDialogUtilBuySell.informBuySellOrderWasNull(getActivity());
            }
        }
    }

    public TransactionFormDTO getBuySellOrder()
    {
        if (quoteDTO == null)
        {
            return null;
        }
        if (portfolioId == null)
        {
            Timber.e("No portfolioId to apply to", new IllegalStateException());
            return null;
        }

        return new TransactionFormDTO(
                shareForTransaction(SocialNetworkEnum.FB),
                shareForTransaction(SocialNetworkEnum.TW),
                shareForTransaction(SocialNetworkEnum.LN),
                shareForTransaction(SocialNetworkEnum.WB),
                null,
                null,
                null,
                false,
                unSpannedComment != null ? unSpannedComment.toString() : null,
                quoteDTO.getRawResponse(),
                mTransactionQuantity,
                portfolioId.key
        );
    }

    protected void fireBuySellReport()
    {
        analytics.fireEvent(getSharingOptionEvent());
    }

    public SharingOptionsEvent getSharingOptionEvent()
    {
        SharingOptionsEvent.Builder builder = new SharingOptionsEvent.Builder()
                .setSecurityId(securityId)
                .setProviderId(portfolioCompactDTO == null ? null : portfolioCompactDTO.getProviderIdKey())
                .setPriceSelectionMethod(mPriceSelectionMethod)
                .hasComment(!mCommentsEditText.getText().toString().isEmpty())
                .facebookEnabled(shareForTransaction(SocialNetworkEnum.FB))
                .twitterEnabled(shareForTransaction(SocialNetworkEnum.TW))
                .linkedInEnabled(shareForTransaction(SocialNetworkEnum.LN))
                .wechatEnabled(shareForTransaction(SocialNetworkEnum.WECHAT))
                .weiboEnabled(shareForTransaction(SocialNetworkEnum.WB));
        setBuyEventFor(builder);

        return builder.build();
    }

    protected abstract void setBuyEventFor(SharingOptionsEvent.Builder builder);

    private void destroyTransactionDialog()
    {
        if (mTransactionDialog != null && mTransactionDialog.isShowing())
        {
            mTransactionDialog.dismiss();
        }
        mTransactionDialog = null;
    }

    private ActionMode.Callback createActionModeCallBackForQuantityEditText()
    {
        //We want to disable action mode since it's irrelevant
        return new ActionMode.Callback()
        {
            @Override public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
            {
                return false;
            }

            @Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
            {
                return false;
            }

            @Override public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
            {
                return false;
            }

            @Override public void onDestroyActionMode(ActionMode actionMode)
            {

            }
        };
    }

    private TextWatcher getQuantityTextChangeListener()
    {
        if (mQuantityTextWatcher == null)
        {
            mQuantityTextWatcher = new TextWatcher()
            {
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
                {

                }

                @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
                {

                }

                @Override public void afterTextChanged(Editable editable)
                {

                    int val = getTradeQuantityFrom(editable.toString());

                    mTransactionQuantity = val;

                    mQuantityEditText.removeTextChangedListener(mQuantityTextWatcher);
                    mQuantityEditText.setText(String.valueOf(val));
                    updateTransactionDialog();
                    mQuantityEditText.addTextChangedListener(mQuantityTextWatcher);
                }
            };
        }
        return mQuantityTextWatcher;
    }

    private int getTradeQuantityFrom(@NonNull String string)
    {
        int val = 0;
        try
        {
            val = Integer.parseInt(string.trim());
            Integer maxValue = getMaxValue();
            if (maxValue != null && val > maxValue)
            {
                val = maxValue;
            }
            else if (val < 0)
            {
                val = 0;
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return val;
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarListener()
    {
        return new SeekBar.OnSeekBarChangeListener()
        {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    mTransactionQuantity = progress;
                    updateTransactionDialog();
                    mPriceSelectionMethod = AnalyticsConstants.Slider;
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        };
    }

    public void populateComment()
    {
        if (transactionCommentFragment != null)
        {
            unSpannedComment = transactionCommentFragment.getComment();
            mCommentsEditText.setText(unSpannedComment);
        }
    }

    protected void attachQuickPriceButtonSet()
    {
        if (mQuickPriceButtonSet != null)
        {
            subscriptions.add(mQuickPriceButtonSet.getPriceSelectedObservable()
                    .subscribe(
                            this::handleQuickPriceSelected,
                            error -> Timber.e(error, "")));
        }
    }

    protected void handleQuickPriceSelected(double priceSelected)
    {
        if (quoteDTO == null)
        {
            // Nothing to do
        }
        else if (mQuickPriceButtonSet.isPercent())
        {
            Integer maxValue = getMaxValue();
            if (maxValue != null)
            {
                linkWithQuantity((int) Math.floor(priceSelected * maxValue));
            }
        }
        else
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy == null || priceRefCcy == 0)
            {
                // Nothing to do
            }
            else
            {
                linkWithQuantity((int) Math.floor(priceSelected / priceRefCcy));
            }
        }

        Integer selectedQuantity = mTransactionQuantity;
        mTransactionQuantity = selectedQuantity != null ? selectedQuantity : 0;
        updateTransactionDialog();
        mPriceSelectionMethod = AnalyticsConstants.MoneySelection;
    }

    protected class BuySellObserver implements Observer<SecurityPositionTransactionDTO>
    {
        private final boolean isBuy;

        public BuySellObserver(boolean isBuy)
        {
            this.isBuy = isBuy;
        }

        @Override public void onNext(SecurityPositionTransactionDTO securityPositionDetailDTO)
        {
            if (securityPositionDetailDTO == null)
            {
                alertDialogUtilBuySell.informBuySellOrderReturnedNull(getActivity());
                return;
            }

            if (buySellTransactionListener != null)
            {
                buySellTransactionListener.onTransactionSuccessful(isBuy, securityPositionDetailDTO);
            }
        }

        @Override public void onCompleted()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }

            // FIXME should we dismiss the dialog on failure?
            getDialog().dismiss();

            updateConfirmButton(false);
        }

        @Override public void onError(Throwable e)
        {
            onCompleted();
            Timber.e(e, "Reporting the error to Crashlytics");
            THException thException = new THException(e);
            THToast.show(thException);

            if (buySellTransactionListener != null)
            {
                buySellTransactionListener.onTransactionFailed(isBuy, thException);
            }
        }
    }

    protected THPurchaser.OnPurchaseFinishedListener createPurchaseFinishedListener()
    {
        return new BuySellPurchaseFinishedListener();
    }

    protected class BuySellPurchaseFinishedListener implements THPurchaser.OnPurchaseFinishedListener
    {
        @Override public void onPurchaseFinished(int requestCode, PurchaseOrder purchaseOrder, ProductPurchase purchase)
        {
            dismissTransactionProgress();
            mTransactionDialog = progressDialogUtil.show(
                    getActivity(),
                    R.string.store_billing_report_api_launching_window_title,
                    R.string.store_billing_report_api_launching_window_message);
        }

        @Override public void onPurchaseFailed(int requestCode, PurchaseOrder purchaseOrder, BillingException billingException)
        {
            // TODO
        }
    }

    protected BuySellPurchaseReportedListener createPurchaseReportedListener()
    {
        return new BuySellPurchaseReportedListener();
    }

    protected class BuySellPurchaseReportedListener
            implements THPurchaseReporter.OnPurchaseReportedListener
    {
        @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase,
                UserProfileDTO updatedUserProfile)
        {
            Timber.e(new Exception(), "Reported purchase %s", updatedUserProfile);
            dismissTransactionProgress();
            linkWith(updatedUserProfile);
            fetchPortfolioCompactList();
        }

        @Override
        public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase,
                BillingException error)
        {
            dismissTransactionProgress();
            // TODO
        }
    }

    public interface BuySellTransactionListener
    {
        void onTransactionSuccessful(boolean isBuy, @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO);

        void onTransactionFailed(boolean isBuy, THException error);
    }
}

