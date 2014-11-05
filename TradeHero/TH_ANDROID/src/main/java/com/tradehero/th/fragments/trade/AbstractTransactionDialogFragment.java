package com.tradehero.th.fragments.trade;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
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
import butterknife.InjectView;
import butterknife.OnClick;
import com.android.internal.util.Predicate;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;
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
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public abstract class AbstractTransactionDialogFragment extends BaseShareableDialogFragment
{
    protected static final String KEY_SECURITY_ID = AbstractTransactionDialogFragment.class.getName() + ".security_id";
    protected static final String KEY_PORTFOLIO_ID = AbstractTransactionDialogFragment.class.getName() + ".portfolio_id";
    protected static final String KEY_QUOTE_DTO = AbstractTransactionDialogFragment.class.getName() + ".quote_dto";

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
    @InjectView(R.id.dialog_btn_cancel) protected Button mCancel;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject PortfolioCompactCacheRx portfolioCompactCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject AlertDialogUtilBuySell alertDialogUtilBuySell;
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache;
    @Inject PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject Analytics analytics;

    @Inject THBillingInteractor userInteractor;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject Provider<BaseTHUIBillingRequest.Builder> uiBillingRequestBuilderProvider;

    private ProgressDialog mTransactionDialog;

    private MiddleCallback<SecurityPositionTransactionDTO> buySellMiddleCallback;
    protected SecurityId securityId;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    @Nullable protected PortfolioCompactDTOList portfolioCompactDTOs;
    private PortfolioId portfolioId;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    protected QuoteDTO quoteDTO;
    protected Integer mTransactionQuantity = 0;
    @Nullable protected PositionDTOCompactList positionDTOCompactList;
    protected boolean showProfitLossUsd = true; // false will show in RefCcy

    private BuySellTransactionListener buySellTransactionListener;

    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;
    private TextWatcher mQuantityTextWatcher;
    private TransactionEditCommentFragment transactionCommentFragment;
    Editable unSpannedComment;
    private Integer purchaseRequestCode;

    @Nullable protected abstract Integer getMaxValue();

    protected abstract boolean hasValidInfo();

    protected abstract boolean isQuickButtonEnabled();

    protected abstract double getQuickButtonMaxValue();

    protected abstract MiddleCallback<SecurityPositionTransactionDTO> getTransactionMiddleCallback(TransactionFormDTO transactionFormDTO);

    public abstract Double getPriceCcy();

    public static boolean canShowDialog(@NonNull QuoteDTO quoteDTO, boolean isBuy)
    {
        return (isBuy && quoteDTO.ask != null) ||
                (!isBuy && quoteDTO.bid != null);
    }

    public static AbstractTransactionDialogFragment newInstance(
            @NonNull SecurityId securityId,
            @NonNull PortfolioId portfolioId,
            @NonNull QuoteDTO quoteDTO,
            boolean isBuy)
    {
        AbstractTransactionDialogFragment abstractBuySellDialogFragment = isBuy ? new BuyDialogFragment() : new SellDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
        args.putBundle(KEY_PORTFOLIO_ID, portfolioId.getArgs());
        args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    protected AbstractTransactionDialogFragment()
    {
        super();
    }

    protected SecurityId getSecurityId()
    {
        if (this.securityId == null)
        {
            this.securityId = new SecurityId(getArguments().getBundle(KEY_SECURITY_ID));
        }
        return securityId;
    }

    @NonNull protected PortfolioId getPortfolioId()
    {
        if (this.portfolioId == null)
        {
            this.portfolioId = new PortfolioId(getArguments().getBundle(KEY_PORTFOLIO_ID));
        }
        return portfolioId;
    }

    private QuoteDTO getBundledQuoteDTO()
    {
        return new QuoteDTO(getArguments().getBundle(KEY_QUOTE_DTO));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.security_buy_sell_dialog, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        init();
        initViews();
    }

    @Override public void onResume()
    {
        super.onResume();

        /** To make sure that the dialog will not show when active dashboard fragment is not BuySellFragment */
        if (!(navigator.get().getCurrentFragment() instanceof BuySellFragment))
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
        detachPurchaseRequestCode();
        destroyTransactionDialog();
        detachBuySellMiddleCallback();
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

    private void init()
    {
        AndroidObservable.bindFragment(this, securityCompactCache.get(getSecurityId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        securityCompactDTO = pair.second;
                        initSecurityRelatedInfo();
                    }
                });
        AndroidObservable.bindFragment(this, portfolioCompactCache.get(getPortfolioId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<PortfolioId, PortfolioCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<PortfolioId, PortfolioCompactDTO> pair)
                    {
                        portfolioCompactDTO = pair.second;
                        initPortfolioRelatedInfo();
                    }
                });
        quoteDTO = getBundledQuoteDTO();

        AndroidObservable.bindFragment(this, securityPositionDetailCache.get()
                .get(this.securityId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<SecurityId, SecurityPositionDetailDTO>>()
                {
                    @Override public void onCompleted()
                    {
                        Timber.d("on completed");
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "error");
                    }

                    @Override public void onNext(Pair<SecurityId, SecurityPositionDetailDTO> securityIdSecurityPositionDetailDTOPair)
                    {
                        positionDTOCompactList = securityIdSecurityPositionDetailDTOPair.second.positions;
                        clampQuantity(true);
                    }
                });
    }

    private void initViews()
    {
        mQuantityEditText.setText(String.valueOf(mTransactionQuantity));
        mQuantityEditText.addTextChangedListener(getQuantityTextChangeListener());
        mQuantityEditText.setCustomSelectionActionModeCallback(createActionModeCallBackForQuantityEditText());
        mQuantityEditText.setOnEditorActionListener((textView, i, keyEvent) -> false);

        mCashShareLeftLabelTextView.setText(getCashLeftLabelResId());

        mSeekBar.setOnSeekBarChangeListener(createSeekBarListener());

        mQuickPriceButtonSet.setListener(createQuickButtonSetListener());

        mBtnAddCash.setOnClickListener(view -> {
            DeviceUtil.dismissKeyboard(mCommentsEditText);
            handleBtnAddCashPressed();
        });

        displayAddCashButton();
    }

    private void initSecurityRelatedInfo()
    {
        mStockNameTextView.setText(securityCompactDTO == null ? "-" : securityCompactDTO.name);
        mStockPriceTextView.setText(String.valueOf(getLabel()));
    }

    private void initPortfolioRelatedInfo()
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
        displayQuickPriceButtonSet();
        updateTransactionDialog();
    }

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

    public String getTitle()
    {
        return mStockNameTextView.getText().toString();
    }

    public String getSubtitle()
    {
        return mStockPriceTextView.getText().toString();
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
    public void onQuantityClicked(/*View v*/)
    {
        mPriceSelectionMethod = AnalyticsConstants.ManualQuantityInput;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_btn_cancel)
    public void onCancelClicked(/*View v*/)
    {
        getDialog().dismiss();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_btn_confirm)
    public void onConfirmClicked(/*View v*/)
    {
        updateConfirmButton(true);
        saveShareSettings();
        fireBuySellReport();
        launchBuySell();
    }

    @OnClick(R.id.comments) void onCommentAreaClicked(/*View commentTextBox*/)
    {
        Bundle bundle = new Bundle();
        SecurityDiscussionEditPostFragment.putSecurityId(bundle, securityId);
        transactionCommentFragment = navigator.get().pushFragment(TransactionEditCommentFragment.class, bundle);

        getDialog().hide();
    }

    protected void fetchPortfolioCompactList()
    {
        AndroidObservable.bindFragment(this,
                portfolioCompactListCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createPortfolioCompactListCacheObserver());
    }

    protected Observer<Pair<UserBaseKey, PortfolioCompactDTOList>> createPortfolioCompactListCacheObserver()
    {
        return new TransactionDialogPortfolioCompactListCacheObserver();
    }

    protected class TransactionDialogPortfolioCompactListCacheObserver implements Observer<Pair<UserBaseKey, PortfolioCompactDTOList>>
    {
        @Override public void onNext(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    protected void linkWith(PortfolioCompactDTOList value, boolean andDisplay)
    {
        this.portfolioCompactDTOs = value;
        portfolioCompactDTO = value.findFirstWhere(new Predicate<PortfolioCompactDTO>()
        {
            @Override public boolean apply(PortfolioCompactDTO portfolioCompactDTO)
            {
                return portfolioCompactDTO.getPortfolioId().equals(getPortfolioId());
            }
        });
        if (andDisplay)
        {
            updateTransactionDialog();
            displayAddCashButton();
        }
    }

    public void setBuySellTransactionListener(BuySellTransactionListener buySellTransactionListener)
    {
        this.buySellTransactionListener = buySellTransactionListener;
    }

    public void displayQuickPriceButtonSet()
    {
        QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        if (buttonSetCopy != null)
        {
            buttonSetCopy.setEnabled(isQuickButtonEnabled());
            buttonSetCopy.setMaxPrice(getQuickButtonMaxValue());
        }
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
        updateSeekbar();
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

    private void updateSeekbar()
    {
        mSeekBar.setProgress(mTransactionQuantity);
    }

    @OnClick(R.id.dialog_profit_and_loss)
    protected void toggleProfitLossUsdRefCcy()
    {
        this.showProfitLossUsd = !showProfitLossUsd;
        updateProfitLoss();
    }

    private void updateProfitLoss()
    {
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

    private void updateConfirmButton(boolean forceDisable)
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

    protected void clampQuantity(boolean andDisplay)
    {
        linkWithQuantity(mTransactionQuantity, andDisplay);
    }

    protected void linkWithQuantity(Integer quantity, boolean andDisplay)
    {
        this.mTransactionQuantity = clampedQuantity(quantity);
        if (andDisplay)
        {
            updateTransactionDialog();
        }
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
        detachBuySellMiddleCallback();

        if (checkValidToTransact())
        {
            TransactionFormDTO transactionFormDTO = getBuySellOrder();
            if (transactionFormDTO != null)
            {
                dismissTransactionProgress();
                mTransactionDialog = progressDialogUtil.show(AbstractTransactionDialogFragment.this.getActivity(),
                        R.string.processing, R.string.alert_dialog_please_wait);

                buySellMiddleCallback = getTransactionMiddleCallback(transactionFormDTO);
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
                //shareLocation ? null : null, // TODO implement location
                //shareLocation ? null : null,
                //shareLocation ? null : null,
                null,
                null,
                null,
                //sharePublic,
                false,
                unSpannedComment != null ? unSpannedComment.toString() : null,
                quoteDTO.rawResponse,
                mTransactionQuantity,
                portfolioId.key
        );
    }

    private void detachBuySellMiddleCallback()
    {
        if (buySellMiddleCallback != null)
        {
            buySellMiddleCallback.setPrimaryCallback(null);
            buySellMiddleCallback = null;
        }
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

    private QuickPriceButtonSet.OnQuickPriceButtonSelectedListener createQuickButtonSetListener()
    {
        return new QuickPriceButtonSet.OnQuickPriceButtonSelectedListener()
        {
            @Override public void onQuickPriceButtonSelected(double priceSelected)
            {
                if (quoteDTO == null)
                {
                    // Nothing to do
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
                        linkWithQuantity((int) Math.floor(priceSelected / priceRefCcy), true);
                    }
                }

                Integer selectedQuantity = mTransactionQuantity;
                mTransactionQuantity = selectedQuantity != null ? selectedQuantity : 0;
                updateTransactionDialog();
                mPriceSelectionMethod = AnalyticsConstants.MoneySelection;
            }
        };
    }

    protected class BuySellCallback implements retrofit.Callback<SecurityPositionTransactionDTO>
    {
        private final boolean isBuy;

        public BuySellCallback(boolean isBuy)
        {
            this.isBuy = isBuy;
        }

        @Override
        public void success(SecurityPositionTransactionDTO securityPositionDetailDTO, Response response)
        {
            onFinish();

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

        private void onFinish()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }

            // FIXME should we dismiss the dialog on failure?
            getDialog().dismiss();

            updateConfirmButton(false);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            if (retrofitError != null)
            {
                Timber.e(retrofitError, "Reporting the error to Crashlytics %s", retrofitError.getBody());
            }
            THException thException = new THException(retrofitError);
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
