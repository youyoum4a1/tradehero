package com.tradehero.th.fragments.trade;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ActionMode;
import android.view.KeyEvent;
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
import com.android.internal.util.Predicate;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.FacebookShareActivity;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
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
import com.tradehero.th.persistence.position.PositionListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import dagger.Lazy;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
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
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject Lazy<PositionListCacheRx> positionCompactListCache;
    @Inject Analytics analytics;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    @Inject THBillingInteractorRx userInteractor;
    @Inject Lazy<DashboardNavigator> navigator;

    protected Subscription buySellSubscription;
    protected SecurityId securityId;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected PortfolioCompactDTOList portfolioCompactDTOs;
    protected PortfolioId portfolioId;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    protected QuoteDTO quoteDTO;
    protected Integer mTransactionQuantity = 0;
    @Nullable protected PositionDTOList positionDTOList;
    @Nullable protected PositionDTOCompact positionDTOCompact;
    protected boolean showProfitLossUsd = true; // false will show in RefCcy

    protected BuySellTransactionListener buySellTransactionListener;

    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;
    private TextWatcher mQuantityTextWatcher;
    private TransactionEditCommentFragment transactionCommentFragment;
    Editable unSpannedComment;

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
        mQuantityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                return false;
            }
        });

        mCashShareLeftLabelTextView.setText(getCashLeftLabelResId());

        mSeekBar.setOnSeekBarChangeListener(createSeekBarListener());

        mBtnAddCash.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View ignored)
            {
                DeviceUtil.dismissKeyboard(mCommentsEditText);
                AbstractTransactionDialogFragment.this.handleBtnAddCashPressed();
            }
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

    @Override public void onDestroyView()
    {
        mQuantityEditText.removeTextChangedListener(mQuantityTextWatcher);
        mQuantityTextWatcher = null;
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
        positionDTOList = null;
        super.onDestroy();
    }

    //<editor-fold desc="Fetches">
    private void initFetches()
    {
        fetchSecurityCompact();
        fetchPortfolioCompact();
        quoteDTO = getBundledQuoteDTO();
        fetchQuote();
        fetchPositionCompactList();
    }

    private void fetchSecurityCompact()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                securityCompactCache.get(getSecurityId()))
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<SecurityId, SecurityCompactDTO>>()
                        {
                            @Override public void call(Pair<SecurityId, SecurityCompactDTO> pair)
                            {
                                AbstractTransactionDialogFragment.this.linkWith(pair.second);
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    protected void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        initSecurityRelatedInfo();
    }

    private void fetchPortfolioCompact()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                portfolioCompactCache.get(getPortfolioId())
                        .map(new PairGetSecond<PortfolioId, PortfolioCompactDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PortfolioCompactDTO>()
                        {
                            @Override public void call(PortfolioCompactDTO compact)
                            {
                                linkWith(compact);
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        setPositionDTO();
        initPortfolioRelatedInfo();
    }

    private void fetchQuote()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                quoteServiceWrapper.getQuoteRx(securityId)
                        .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                        {
                            @Override public Observable<?> call(Observable<? extends Void> observable)
                            {
                                return observable.delay(5000, TimeUnit.MILLISECONDS);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<QuoteDTO>()
                        {
                            @Override public void call(QuoteDTO quote)
                            {
                                linkWith(quote);
                            }
                        },
                        new ToastOnErrorAction()));
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

    private void fetchPositionCompactList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                positionCompactListCache.get()
                        .get(this.securityId)
                        .map(new PairGetSecond<SecurityId, PositionDTOList>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PositionDTOList>()
                        {
                            @Override public void call(PositionDTOList list)
                            {
                                linkWith(list);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    protected void linkWith(PositionDTOList positionDTOs)
    {
        this.positionDTOList = positionDTOs;
        setPositionDTO();
        initPortfolioRelatedInfo();
        clampQuantity();
        displayCashShareLabel();
    }

    protected void fetchPortfolioCompactList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, PortfolioCompactDTOList>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PortfolioCompactDTOList>()
                        {
                            @Override public void call(PortfolioCompactDTOList list)
                            {
                                linkWith(list);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_portfolio_list_info),
                                "Failed fetching the list of porfolios")));
    }

    protected void linkWith(PortfolioCompactDTOList value)
    {
        this.portfolioCompactDTOs = value;
        portfolioCompactDTO = value.findFirstWhere(new Predicate<PortfolioCompactDTO>()
        {
            @Override public boolean apply(PortfolioCompactDTO portfolioCompactDTO1)
            {
                return portfolioCompactDTO1.getPortfolioId().equals(AbstractTransactionDialogFragment.this.getPortfolioId());
            }
        });
        updateTransactionDialog();
        displayAddCashButton();
        displayCashShareLabel();
    }

    protected void setPositionDTO()
    {
        if (positionDTOList != null && portfolioCompactDTO != null)
        {
            this.positionDTOCompact = positionDTOList.findFirstWhere(new Predicate<PositionDTO>()
            {
                @Override public boolean apply(PositionDTO position)
                {
                    return position.portfolioId == portfolioCompactDTO.id && position.shares != null && position.shares != 0;
                }
            });
        }
        if (positionDTOCompact == null && positionDTOList != null && portfolioCompactDTO != null)
        {
            this.positionDTOCompact = positionDTOList.findFirstWhere(new Predicate<PositionDTO>()
            {
                @Override public boolean apply(PositionDTO position)
                {
                    return position.portfolioId == portfolioCompactDTO.id;
                }
            });
        }
    }
    //</editor-fold>

    private void initSecurityRelatedInfo()
    {
        if (securityCompactDTO != null)
        {
            if (!StringUtils.isNullOrEmpty(securityCompactDTO.name))
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

    protected abstract String getLabel();

    @NonNull protected abstract THSignedNumber getFormattedPrice(double price);

    protected abstract int getCashLeftLabelResId();

    @Nullable abstract protected Boolean isClosingPosition();

    @Nullable protected Integer getMaxPurchasableShares()
    {
        return PortfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO,
                positionDTOCompact);
    }

    @Nullable protected Integer getMaxSellableShares()
    {
        return PortfolioCompactDTOUtil.getMaxSellableShares(
                portfolioCompactDTO,
                quoteDTO,
                positionDTOCompact);
    }

    @Nullable protected Double getRemainingForPurchaseInPortfolioRefCcy()
    {
        QuoteDTO quoteInPortfolioCcy = PortfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
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
        QuoteDTO quoteInPortfolioCcy = PortfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
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
            cashLeftText = "0"; //getResources().getString(R.string.na);
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
            shareLeftText = "0";//getResources().getString(R.string.na);
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
        DeviceUtil.dismissKeyboard(mCommentsEditText);
        //noinspection unchecked
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userInteractor.purchaseAndClear(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PurchaseResult>()
                        {
                            @Override public void call(PurchaseResult result)
                            {
                                userProfileCache.get(currentUserId.toUserBaseKey());
                                portfolioCompactListCache.get(currentUserId.toUserBaseKey());
                            }
                        },
                        Actions.empty()
                ));
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
                unsubscribe(buySellSubscription);
                buySellSubscription = getTransactionSubscription(transactionFormDTO);
            }
            else
            {
                AlertDialogBuySellRxUtil.informBuySellOrderWasNull(getActivity())
                        .subscribe(Actions.empty());
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
            onStopSubscriptions.add(mQuickPriceButtonSet.getPriceSelectedObservable()
                    .subscribe(
                            new Action1<Double>()
                            {
                                @Override public void call(Double price)
                                {
                                    AbstractTransactionDialogFragment.this.handleQuickPriceSelected(price);
                                }
                            },
                            new TimberOnErrorAction("")));
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
                AlertDialogBuySellRxUtil.informBuySellOrderReturnedNull(getActivity())
                        .subscribe(Actions.empty());
                return;
            }

            if (buySellTransactionListener != null)
            {
                buySellTransactionListener.onTransactionSuccessful(isBuy, securityPositionDetailDTO, mCommentsEditText.getText().toString());
            }

            if (mBtnShareWeChat.isChecked())
            {
                //shareWeChatClient(isBuy);
            }
        }

        @Override public void onCompleted()
        {
            Dialog dialog = getDialog();
            // FIXME should we dismiss the dialog on failure?
            if (dialog != null)
            {
                dialog.dismiss();
            }

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

    protected void shareFacebookClient(boolean isBuy)
    {
        Intent shareIntent = new Intent(getActivity(), FacebookShareActivity.class);
        Bundle extras = new Bundle();
        FacebookShareActivity.setMessage(
                extras,
                String.format(
                        getString(R.string.traded_facebook_share_message),
                        THSignedNumber.builder(mTransactionQuantity).build().toString(),
                        securityCompactDTO.name,
                        SecurityCompactDTOUtil.getShortSymbol(securityCompactDTO),
                        getFormattedPrice(isBuy ? quoteDTO.ask : quoteDTO.bid)));
        FacebookShareActivity.setName(extras, "TradeHero");
        FacebookShareActivity.setCaption(extras, "tradehero.mobi");
        FacebookShareActivity.setDescription(
                extras,
                String.format(
                        "Follow %s on TradeHero for great stock tips!",
                        userProfileDTO.displayName));
        FacebookShareActivity.setLinkUrl(extras, "http://www.facebook.com");
        if (securityCompactDTO.imageBlobUrl == null)
        {
            FacebookShareActivity.setDefaultPictureUrl(extras);
        }
        else
        {
            FacebookShareActivity.setPictureUrl(extras, securityCompactDTO.imageBlobUrl);
        }
        shareIntent.putExtras(extras);
        getActivity().startActivity(shareIntent);
    }

    public interface BuySellTransactionListener
    {
        void onTransactionSuccessful(boolean isBuy, @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO, String commentString);

        void onTransactionFailed(boolean isBuy, THException error);
    }
}

