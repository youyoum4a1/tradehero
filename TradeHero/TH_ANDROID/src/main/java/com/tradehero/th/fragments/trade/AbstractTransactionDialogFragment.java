package com.tradehero.th.fragments.trade;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.THBasePurchaseActionInteractor;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.social.SocialLinkHelper;
import com.tradehero.th.fragments.social.SocialLinkHelperFactory;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public abstract class AbstractTransactionDialogFragment extends BaseDialogFragment
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

    @InjectView(R.id.seek_bar) protected SeekBar mSeekBar;

    @InjectView(R.id.quick_price_button_set) protected QuickPriceButtonSet mQuickPriceButtonSet;

    @InjectView(R.id.vquantity) protected EditText mQuantityEditText;
    @InjectView(R.id.comments) protected EditText mCommentsEditText;

    @InjectView(R.id.dialog_btn_add_cash) protected ImageButton mBtnAddCash;
    @InjectView(R.id.dialog_btn_confirm) protected Button mConfirm;
    @InjectView(R.id.dialog_btn_cancel) protected Button mCancel;

    @Optional @InjectView(R.id.btn_share_fb) protected ToggleButton mBtnShareFb;
    @InjectView(R.id.btn_share_li) protected ToggleButton mBtnShareLn;
    @Optional @InjectView(R.id.btn_share_tw) protected ToggleButton mBtnShareTw;
    @InjectView(R.id.btn_share_wb) protected ToggleButton mBtnShareWb;
    @InjectView(R.id.btn_share_wechat) protected ToggleButton mBtnShareWeChat;

    @Inject UserProfileCache userProfileCache;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject CurrentUserId currentUserId;
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject AlertDialogUtilBuySell alertDialogUtilBuySell;
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject AlertDialogUtil alertDialogUtil;
    @Inject SocialLinkHelperFactory socialLinkHelperFactory;
    @Inject Analytics analytics;

    @Inject THBillingInteractor userInteractor;
    @Inject Provider<THUIBillingRequest> uiBillingRequestProvider;

    SocialLinkHelper socialLinkHelper;
    private ProgressDialog mTransactionDialog;

    private MiddleCallback<SecurityPositionDetailDTO> buySellMiddleCallback;
    protected SecurityId securityId;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    private PortfolioId portfolioId;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    protected QuoteDTO quoteDTO;
    private boolean isTransactionRunning;
    protected Integer mTransactionQuantity;
    @Nullable protected PositionDTOCompactList positionDTOCompactList;

    private BuySellTransactionListener buySellTransactionListener;
    protected UserProfileDTO userProfileDTO;

    private AlertDialog mSocialLinkingDialog;
    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;
    private TextWatcher mQuantityTextWatcher;

    protected abstract String getLabel();

    protected abstract int getCashLeftLabelResId();

    public abstract String getCashShareLeft();

    protected abstract Integer getMaxValue();

    protected abstract boolean hasValidInfo();

    protected abstract boolean isQuickButtonEnabled();

    protected abstract double getQuickButtonMaxValue();

    protected abstract MiddleCallback<SecurityPositionDetailDTO> getTransactionMiddleCallback(TransactionFormDTO transactionFormDTO);

    public abstract Double getPriceCcy();

    public static AbstractTransactionDialogFragment newInstance(
            @NotNull SecurityId securityId,
            @NotNull PortfolioId portfolioId,
            @NotNull QuoteDTO quoteDTO,
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

    protected PortfolioId getPortfolioId()
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

    private void init()
    {
        securityCompactDTO = securityCompactCache.get(getSecurityId());
        portfolioCompactDTO = portfolioCompactCache.get(getPortfolioId());
        quoteDTO = getBundledQuoteDTO();
        SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);
        if (detailDTO != null)
        {
            positionDTOCompactList = detailDTO.positions;
        }
        clampQuantity(true);
        linkWith(userProfileCache.get(currentUserId.toUserBaseKey()));
        setPublishToShareBySetting();
    }

    private void initViews()
    {
        mStockNameTextView.setText(securityCompactDTO == null ? "-" : securityCompactDTO.name);

        mStockPriceTextView.setText(String.valueOf(getLabel()));

        mPortfolioTextView.setText(
                getString(R.string.buy_sell_portfolio_selected_title) + " " + (portfolioCompactDTO == null ? "-" : portfolioCompactDTO.title));

        mQuantityEditText.setText(String.valueOf(mTransactionQuantity));
        mQuantityEditText.addTextChangedListener(getQuantityTextChangeListener());
        mQuantityEditText.setCustomSelectionActionModeCallback(createActionModeCallBackForQuantityEditText());

        mCashShareLeftLabelTextView.setText(getCashLeftLabelResId());

        mSeekBar.setOnSeekBarChangeListener(createSeekBarListener());

        Integer maxValue = getMaxValue();
        if (maxValue != null)
        {
            mSeekBar.setMax(maxValue);
            mSeekBar.setEnabled(maxValue > 0);
        }

        mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
        mQuickPriceButtonSet.addButton(R.id.toggle5k);
        mQuickPriceButtonSet.addButton(R.id.toggle10k);
        mQuickPriceButtonSet.addButton(R.id.toggle25k);
        mQuickPriceButtonSet.addButton(R.id.toggle50k);
        displayQuickPriceButtonSet();

        mBtnAddCash.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                DeviceUtil.dismissKeyboard(getActivity(), mCommentsEditText);
                handleBtnAddCashPressed();
            }
        });

        initSocialButtons();
        updateTransactionDialog();
    }

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
                THSignedNumber thTradeValue = THSignedMoney.builder()
                        .value(value)
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

    public QuickPriceButtonSet getQuickPriceButtonSet()
    {
        return mQuickPriceButtonSet;
    }

    public EditText getCommentView()
    {
        return mCommentsEditText;
    }

    public SeekBar getSeekBar()
    {
        return mSeekBar;
    }

    public Button getConfirmButton()
    {
        return mConfirm;
    }

    public CompoundButton getFacebookShareButton()
    {
        return mBtnShareFb;
    }

    public CompoundButton getTwitterShareButton()
    {
        return mBtnShareTw;
    }

    public CompoundButton getLinkedInShareButton()
    {
        return mBtnShareLn;
    }

    public CompoundButton getWeiboShareButton()
    {
        return mBtnShareWb;
    }

    public CompoundButton getWeChatShareButton()
    {
        return mBtnShareWeChat;
    }

    public EditText getQuantityEditText()
    {
        return mQuantityEditText;
    }

    public AlertDialog getSocialLinkingDialog()
    {
        return mSocialLinkingDialog;
    }

    @OnClick(R.id.vquantity)
    public void onQuantityClicked()
    {
        mPriceSelectionMethod = AnalyticsConstants.ManualQuantityInput;
    }

    @OnClick(R.id.dialog_btn_cancel)
    public void onCancelClicked(View v)
    {
        getDialog().dismiss();
    }

    @OnClick(R.id.dialog_btn_confirm)
    public void onConfirmClicked(View v)
    {
        socialSharePreferenceHelperNew.save();
        fireBuySellReport();
        launchBuySell();
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

    public void handleBtnAddCashPressed()
    {
        THBasePurchaseActionInteractor.builder()
                .setBillingInteractor(userInteractor)
                .setPurchaseApplicableOwnedPortfolioId(new OwnedPortfolioId(currentUserId.get(), portfolioId.key))
                .setBillingRequest(uiBillingRequestProvider.get())
                .startWithProgressDialog(true) // true by default
                .popIfBillingNotAvailable(true)  // true by default
                .popIfProductIdentifierFetchFailed(true) // true by default
                .popIfInventoryFetchFailed(true) // true by default
                .popIfPurchaseFailed(true) // true by default
                .error(new UIBillingRequest.OnErrorListener()
                {
                    @Override public void onError(int requestCode, BillingException billingException)
                    {
                        Timber.e(billingException, "Store had error");
                    }
                })
                .setPurchaseReportedListener(createPurchaseReportedListener())
                .build()
                .buyVirtualDollar();
    }

    public void updateTransactionDialog()
    {
        updateSeekbar();
        updateQuantityView();
        updateTradeValueAndCashShareLeft();
        updateConfirmButton();
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

    private void updateTradeValueAndCashShareLeft()
    {
        mCashShareLeftTextView.setText(getCashShareLeft());
        mTradeValueTextView.setText(getTradeValueText());
    }

    private void updateConfirmButton()
    {
        mConfirm.setEnabled(mTransactionQuantity != 0 && (hasValidInfo()));
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
                mTransactionDialog = progressDialogUtil.show(AbstractTransactionDialogFragment.this.getActivity(),
                        R.string.processing, R.string.alert_dialog_please_wait);

                buySellMiddleCallback = getTransactionMiddleCallback(transactionFormDTO);

                isTransactionRunning = true;
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
        //Timber.d("fb=%b tw=%b li=%b location=%b public=%b quantity=%d", publishToFb,
        //        publishToTw, publishToLi, shareLocation, sharePublic,
        //        isBuy ? mBuyQuantity : mSellQuantity);

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
                mCommentsEditText == null ? null : mCommentsEditText.getText().toString(),
                quoteDTO.rawResponse,
                mTransactionQuantity,
                portfolioId.key
        );
    }

    protected boolean shareForTransaction(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinkedOr(socialNetworkEnum, true));
    }

    private void detachBuySellMiddleCallback()
    {
        if (buySellMiddleCallback != null)
        {
            buySellMiddleCallback.setPrimaryCallback(null);
            buySellMiddleCallback = null;
        }
    }

    public void setPublishToShareBySetting()
    {
        socialSharePreferenceHelperNew.load();
    }

    public void onSuccessSocialLink(UserProfileDTO userProfileDTO, SocialNetworkEnum socialNetworkEnum)
    {
        linkWith(userProfileDTO);
        setPublishEnable(socialNetworkEnum);
    }

    public void setPublishEnable(SocialNetworkEnum socialNetwork)
    {
        socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetwork, true);
        switch (socialNetwork)
        {
            case FB:
                mBtnShareFb.setChecked(true);
                break;
            case TW:
                mBtnShareTw.setChecked(true);
                break;
            case LN:
                mBtnShareLn.setChecked(true);
                break;
            case WB:
                mBtnShareWb.setChecked(true);
                break;
        }
    }

    @Nullable public Boolean isSocialLinked(SocialNetworkEnum socialNetwork)
    {
        UserProfileDTO userProfileCopy = userProfileDTO;
        if (userProfileCopy != null)
        {
            switch (socialNetwork)
            {
                case FB:
                    return userProfileCopy.fbLinked;
                case TW:
                    return userProfileCopy.twLinked;
                case LN:
                    return userProfileCopy.liLinked;
                case WB:
                    return userProfileCopy.wbLinked;
                case WECHAT:
                    return null;
                default:
                    Timber.e(new IllegalArgumentException(), "Unhandled socialNetwork.%s", socialNetwork);
                    return false;
            }
        }
        return null;
    }

    public boolean isSocialLinkedOr(SocialNetworkEnum socialNetwork, boolean orElse)
    {
        @Nullable Boolean socialLinked = isSocialLinked(socialNetwork);
        return socialLinked != null ? socialLinked : orElse;
    }

    private void initSocialButtons()
    {
        initSocialButton(mBtnShareFb, SocialNetworkEnum.FB);
        initSocialButton(mBtnShareTw, SocialNetworkEnum.TW);
        initSocialButton(mBtnShareLn, SocialNetworkEnum.LN);
        initSocialButton(mBtnShareWeChat, SocialNetworkEnum.WECHAT, createCheckedChangeListenerForWechat());
        initSocialButton(mBtnShareWb, SocialNetworkEnum.WB);
    }

    private void initSocialButton(CompoundButton socialButton, SocialNetworkEnum socialNetworkEnum)
    {
        initSocialButton(socialButton, socialNetworkEnum, createCheckedChangeListener());
    }

    private void initSocialButton(CompoundButton socialButton, SocialNetworkEnum socialNetworkEnum,
            CompoundButton.OnCheckedChangeListener onCheckedChangedListener)
    {
        if (socialButton != null)
        {
            socialButton.setTag(socialNetworkEnum);
            socialButton.setChecked(initialShareButtonState(socialNetworkEnum));
            socialButton.setOnCheckedChangeListener(onCheckedChangedListener);
        }
    }

    protected boolean initialShareButtonState(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(
                socialNetworkEnum,
                isSocialLinkedOr(socialNetworkEnum, false));
    }

    public void askToLinkAccountToSocial(final SocialNetworkEnum socialNetwork)
    {
        mSocialLinkingDialog = alertDialogUtil.popWithOkCancelButton(
                getActivity(),
                getActivity().getApplicationContext().getString(R.string.link, socialNetwork.getName()),
                getActivity().getApplicationContext().getString(R.string.link_description, socialNetwork.getName()),
                R.string.link_now,
                R.string.later,
                new DialogInterface.OnClickListener()//Ok
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        linkSocialNetwork(socialNetwork);
                    }
                },
                new DialogInterface.OnClickListener()//Cancel
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        alertDialogUtil.dismissProgressDialog();
                    }
                },
                new DialogInterface.OnDismissListener()
                {
                    @Override public void onDismiss(DialogInterface dialogInterface)
                    {
                        destroySocialLinkDialog();
                    }
                }
        );
    }

    private void linkSocialNetwork(SocialNetworkEnum socialNetworkEnum)
    {
        detachSocialLinkHelper();
        socialLinkHelper = socialLinkHelperFactory.buildSocialLinkerHelper(socialNetworkEnum);
        socialLinkHelper.link(new SocialLinkingCallback(socialNetworkEnum));
    }

    private void detachSocialLinkHelper()
    {
        if (socialLinkHelper != null)
        {
            socialLinkHelper.setSocialLinkingCallback(null);
            socialLinkHelper = null;
        }
    }

    private void linkWith(UserProfileDTO updatedUserProfileDTO)
    {
        this.userProfileDTO = updatedUserProfileDTO;
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

    @Override public void onDestroy()
    {
        securityCompactDTO = null;
        portfolioCompactDTO = null;
        quoteDTO = null;
        positionDTOCompactList = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        mQuantityEditText.removeTextChangedListener(mQuantityTextWatcher);
        mQuantityTextWatcher = null;
        destroyTransactionDialog();
        destroySocialLinkDialog();
        detachBuySellMiddleCallback();
        detachSocialLinkHelper();
        super.onDestroyView();
    }

    private void destroySocialLinkDialog()
    {
        if (mSocialLinkingDialog != null && mSocialLinkingDialog.isShowing())
        {
            mSocialLinkingDialog.dismiss();
        }
        mSocialLinkingDialog = null;
    }

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

    private int getTradeQuantityFrom(@NotNull String string)
    {
        int val = 0;
        try
        {
            val = Integer.parseInt(string.trim());
            if (val > getMaxValue())
            {
                val = getMaxValue();
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

    private CompoundButton.OnCheckedChangeListener createCheckedChangeListenerForWechat()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (!compoundButton.isPressed())
                {
                    return;
                }
                SocialNetworkEnum networkEnum = (SocialNetworkEnum) compoundButton.getTag();
                socialSharePreferenceHelperNew.updateSocialSharePreference(networkEnum, isChecked);
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener createCheckedChangeListener()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (!compoundButton.isPressed())
                {
                    return;
                }
                SocialNetworkEnum networkEnum = (SocialNetworkEnum) compoundButton.getTag();
                Boolean socialLinked = isSocialLinked(networkEnum);
                if (isChecked && (socialLinked == null || !socialLinked))
                {
                    if (socialLinked != null)
                    {
                        askToLinkAccountToSocial(networkEnum);
                    }
                    isChecked = false;
                }

                compoundButton.setChecked(isChecked);
                socialSharePreferenceHelperNew.updateSocialSharePreference(networkEnum, isChecked);
            }
        };
    }

    private BuySellPurchaseReportedListener createPurchaseReportedListener()
    {
        return new BuySellPurchaseReportedListener();
    }

    private class SocialLinkingCallback implements retrofit.Callback<UserProfileDTO>
    {
        final SocialNetworkEnum socialNetworkEnum;

        SocialLinkingCallback(final SocialNetworkEnum socialNetworkEnum)
        {
            this.socialNetworkEnum = socialNetworkEnum;
        }

        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            onSuccessSocialLink(userProfileDTO, socialNetworkEnum);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
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

    protected class BuySellCallback implements retrofit.Callback<SecurityPositionDetailDTO>
    {
        private final boolean isBuy;

        public BuySellCallback(boolean isBuy)
        {
            this.isBuy = isBuy;
        }

        @Override
        public void success(SecurityPositionDetailDTO securityPositionDetailDTO, Response response)
        {
            onFinish();

            if (securityPositionDetailDTO == null)
            {
                alertDialogUtilBuySell.informBuySellOrderReturnedNull(getActivity());
                return;
            }

            if (isBuy)
            {
                isTransactionRunning = false;
            }
            else
            {
                isTransactionRunning = false;
            }

            if (buySellTransactionListener != null)
            {
                buySellTransactionListener.onTransactionSuccessful(isBuy);
            }
        }

        private void onFinish()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }
            getDialog().dismiss();
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

    protected class BuySellPurchaseReportedListener
            implements PurchaseReporter.OnPurchaseReportedListener
    {
        @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase,
                UserProfileDTO updatedUserProfile)
        {
            linkWith(updatedUserProfile);
            updateTransactionDialog();
        }

        @Override
        public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase,
                BillingException error)
        {
        }
    }

    public interface BuySellTransactionListener
    {
        void onTransactionSuccessful(boolean isBuy);

        void onTransactionFailed(boolean isBuy, THException error);
    }
}
