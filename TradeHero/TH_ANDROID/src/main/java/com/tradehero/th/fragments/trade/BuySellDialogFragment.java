package com.tradehero.th.fragments.trade;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
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
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.social.SocialLinkHelper;
import com.tradehero.th.fragments.social.SocialLinkHelperFactory;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.misc.exception.THException;
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
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class BuySellDialogFragment extends BaseDialogFragment
{
    private static final String KEY_SECURITY_ID = BuySellDialogFragment.class.getName() + ".security_id";
    private static final String KEY_PORTFOLIO_ID = BuySellDialogFragment.class.getName() + ".portfolio_id";
    private static final String KEY_QUOTE_DTO = BuySellDialogFragment.class.getName() + ".quote_dto";
    private static final String KEY_IS_BUY = BuySellDialogFragment.class.getName() + ".is_buy";

    @InjectView(R.id.dialog_stock_name) protected TextView mStockNameTextView;
    @InjectView(R.id.vtrade_value) protected TextView mTradeValueTextView;
    @InjectView(R.id.vquantity) protected TextView mQuantityTextView;
    @InjectView(R.id.vcash_left) protected TextView mCashShareLeftTextView;
    @InjectView(R.id.dialog_cash_left) protected TextView mCashShareLeftLabelTextView;
    @InjectView(R.id.dialog_price) protected TextView mStockPriceTextView;

    @InjectView(R.id.seek_bar) protected SeekBar mSeekBar;

    @InjectView(R.id.quick_price_button_set) protected QuickPriceButtonSet mQuickPriceButtonSet;

    @InjectView(R.id.comments) protected EditText mCommentsEditText;

    @InjectView(R.id.dialog_btn_add_cash) protected ImageButton mBtnAddCash;
    @InjectView(R.id.dialog_btn_confirm) protected Button mConfirm;
    @InjectView(R.id.dialog_btn_cancel) protected Button mCancel;

    @InjectView(R.id.btn_share_fb) protected ToggleButton mBtnShareFb;
    @InjectView(R.id.btn_share_li) protected ToggleButton mBtnShareLn;
    @InjectView(R.id.btn_share_tw) protected ToggleButton mBtnShareTw;
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
    private MiddleCallback<SecurityPositionDetailDTO> buySellMiddleCallback;
    @Inject Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject AlertDialogUtil alertDialogUtil;
    @Inject SocialLinkHelperFactory socialLinkHelperFactory;

    SocialLinkHelper socialLinkHelper;
    private ProgressDialog transactionDialog;

    private SecurityId securityId;
    private SecurityCompactDTO securityCompactDTO;
    private PortfolioId portfolioId;
    private PortfolioCompactDTO portfolioCompactDTO;
    private QuoteDTO quoteDTO;
    private boolean isTransactionBuy; // TODO eventually remove this flag and create appropriate subclass to handle buy/sell
    private int mQuantity;
    private boolean isTransactionRunning;
    private Integer mTransactionQuantity;
    private Integer mBuyQuantity;
    private Integer mSellQuantity;
    private PositionDTOCompactList positionDTOCompactList;
    private UserProfileDTO userProfileDTO;
    private BuySellTransactionListener buySellTransactionListener;

    private BuySellDialogFragment()
    {
        super();
    }

    public static BuySellDialogFragment newInstance(SecurityId securityId, PortfolioId portfolioId, QuoteDTO quoteDTO, boolean isBuy)
    {
        BuySellDialogFragment buySellDialogFragment = new BuySellDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
        args.putBundle(KEY_PORTFOLIO_ID, portfolioId.getArgs());
        args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
        args.putBoolean(KEY_IS_BUY, isBuy);
        buySellDialogFragment.setArguments(args);
        return buySellDialogFragment;
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

    private boolean getBundledTransactionFlag()
    {
        return getArguments().getBoolean(KEY_IS_BUY);
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
        isTransactionBuy = getBundledTransactionFlag();
        SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);
        if (detailDTO != null)
        {
            positionDTOCompactList = detailDTO.positions;
        }
        if (isTransactionBuy)
        {
            clampBuyQuantity(true);
        }
        else
        {
            clampSellQuantity(true);
        }
        linkWith(userProfileCache.get(currentUserId.toUserBaseKey()));
        setPublishToShareBySetting();
    }

    private void initViews()
    {
        mStockNameTextView.setText(securityCompactDTO.name);

        String display = securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay;

        String bPrice;
        THSignedNumber bThSignedNumber;
        bThSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.ask, THSignedNumber.WITHOUT_SIGN, "");
        bPrice = bThSignedNumber.toString();
        String buyPriceText = getString(R.string.buy_sell_button_buy, display, bPrice);

        String sPrice;
        THSignedNumber sthSignedNumber;
        sthSignedNumber =
                new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.bid, THSignedNumber.WITHOUT_SIGN, "");
        sPrice = sthSignedNumber.toString();
        String sellPriceText = getString(R.string.buy_sell_button_sell, display, sPrice);

        mStockPriceTextView.setText(String.valueOf(isTransactionBuy ? buyPriceText : sellPriceText));

        mQuantityTextView.setText(String.valueOf(mQuantity));

        mCashShareLeftLabelTextView.setText(
                isTransactionBuy ? R.string.buy_sell_cash_left : R.string.buy_sell_share_left);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    mQuantity = progress;
                    mQuantityTextView.setText(String.valueOf(progress));
                    updateBuySellDialog();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        if (isTransactionBuy)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                mSeekBar.setMax(maxPurchasableShares);
                mSeekBar.setEnabled(maxPurchasableShares > 0);
            }
        }
        else
        {
            Integer maxSellableShares = getMaxSellableShares();
            if (maxSellableShares != null)
            {
                mSeekBar.setMax(maxSellableShares);
                mSeekBar.setEnabled(maxSellableShares > 0);
            }
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
        updateBuySellDialog();
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
            if (quoteDTO == null)
            {
                buttonSetCopy.setEnabled(false);
            }
            else if (isTransactionBuy && quoteDTO.ask == null)
            {
                buttonSetCopy.setEnabled(false);
            }
            else if (isTransactionBuy)
            {
                buttonSetCopy.setEnabled(true);
                //if (this.userProfileDTO != null && userProfileDTO.portfolio != null)
                //{
                buttonSetCopy.setMaxPrice(portfolioCompactDTO.cashBalance);
                //}
            }
            else if ((quoteDTO.bid == null || quoteDTO.toUSDRate == null))
            {
                buttonSetCopy.setEnabled(false);
            }
            else
            {
                buttonSetCopy.setEnabled(true);
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null)
                {
                    // TODO see other currencies
                    buttonSetCopy.setMaxPrice(
                            maxSellableShares * quoteDTO.bid * quoteDTO.toUSDRate);
                }
            }
        }
    }

    public void handleBtnAddCashPressed()
    {
        THToast.show("What is the square root of fish? Yay!! Free Money!");
    }

    public void updateBuySellDialog()
    {
        String valueText = "-";
        String cashLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null)
        {
            Double priceRefCcy = quoteDTO.getPriceRefCcy(portfolioCompactDTO, true);
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                double value = mQuantity * priceRefCcy;
                THSignedNumber thTradeValue =
                        new THSignedNumber(THSignedNumber.TYPE_MONEY, value, THSignedNumber.WITHOUT_SIGN,
                                portfolioCompactDTO.currencyDisplay);
                valueText = thTradeValue.toString();

                if (isTransactionBuy)
                {
                    double cashAvailable = portfolioCompactDTO.cashBalance;
                    THSignedNumber thSignedNumber =
                            new THSignedNumber(THSignedNumber.TYPE_MONEY, cashAvailable - value,
                                    THSignedNumber.WITHOUT_SIGN, portfolioCompactDTO.currencyDisplay);
                    cashLeftText = thSignedNumber.toString();
                }
            }
            if (!isTransactionBuy
                    //&& positionDTOCompactList != null
                    && portfolioCompactDTO != null)
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null && maxSellableShares != 0)
                {
                    cashLeftText = String.valueOf(maxSellableShares - mQuantity);//share left
                }
            }
        }
        if (mTradeValueTextView != null)
        {
            mTradeValueTextView.setText(valueText);
        }
        if (mQuantityTextView != null)
        {
            mQuantityTextView.setText(String.valueOf(mQuantity));
        }
        if (mCashShareLeftTextView != null)
        {
            mCashShareLeftTextView.setText(cashLeftText);
        }
        if (mSeekBar != null)
        {
            mSeekBar.setProgress(mQuantity);
        }
        if (mConfirm != null)
        {
            mConfirm.setEnabled(mQuantity != 0 && (
                    (
                            //!isTransactionRunning &&
                            isTransactionBuy && hasValidInfoForBuy()) ||
                            (
                                    //!isSelling &&
                                    !isTransactionBuy && hasValidInfoForSell())
            ));
        }
    }

    public Integer getMaxSellableShares()
    {
        return
                positionDTOCompactList.getMaxSellableShares(
                        this.quoteDTO,
                        this.portfolioCompactDTO);
    }

    public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(portfolioCompactDTO, quoteDTO);
    }

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.ask != null;
    }

    protected boolean hasValidInfoForSell()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.bid != null;
    }

    protected void clampBuyQuantity(boolean andDisplay)
    {
        linkWithBuyQuantity(mBuyQuantity, andDisplay);
    }

    protected void linkWithBuyQuantity(Integer buyQuantity, boolean andDisplay)
    {
        this.mBuyQuantity = clampedBuyQuantity(buyQuantity);
    }

    protected Integer clampedBuyQuantity(Integer candidate)
    {
        Integer maxPurchasable = getMaxPurchasableShares();
        if (candidate == null || maxPurchasable == null)
        {
            return candidate;
        }
        return Math.min(candidate, maxPurchasable);
    }

    protected void linkWithSellQuantity(Integer sellQuantity, boolean andDisplay)
    {
        this.mSellQuantity = clampedSellQuantity(sellQuantity);
    }

    protected Integer clampedSellQuantity(Integer candidate)
    {
        Integer maxSellable = getMaxSellableShares();
        if (candidate == null || maxSellable == null || maxSellable == 0)
        {
            return candidate;
        }
        return Math.min(candidate, maxSellable);
    }

    protected void clampSellQuantity(boolean andDisplay)
    {
        linkWithSellQuantity(mSellQuantity, andDisplay);
    }

    private boolean checkValidToBuyOrSell()
    {
        return securityId != null && securityId.getExchange() != null
                && securityId.getSecuritySymbol() != null;
    }

    private void launchBuySell()
    {
        detachBuySellMiddleCallback();

        if (checkValidToBuyOrSell())
        {
            TransactionFormDTO transactionFormDTO = getBuySellOrder(isTransactionBuy);
            if (transactionFormDTO != null)
            {
                transactionDialog = progressDialogUtil.show(BuySellDialogFragment.this.getActivity(),
                        R.string.processing, R.string.alert_dialog_please_wait);

                buySellMiddleCallback = securityServiceWrapper.doTransaction(
                        securityId, transactionFormDTO, isTransactionBuy,
                        new BuySellCallback(isTransactionBuy));

                isTransactionRunning = true;
            }
            else
            {
                alertDialogUtilBuySell.informBuySellOrderWasNull(getActivity());
            }
        }
    }

    private TransactionFormDTO getBuySellOrder(boolean isBuy)
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
                mQuantity,
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
        alertDialogUtil.popWithOkCancelButton(
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

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        securityCompactDTO = null;
        portfolioCompactDTO = null;
        quoteDTO = null;
        detachBuySellMiddleCallback();
        detachSocialLinkHelper();
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

    private class SocialLinkingCallback implements retrofit.Callback<UserProfileDTO>
    {
        final SocialNetworkEnum socialNetworkEnum;

        SocialLinkingCallback(final SocialNetworkEnum socialNetworkEnum)
        {
            this.socialNetworkEnum = socialNetworkEnum;
        }

        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            linkWith(userProfileDTO);
            setPublishEnable(socialNetworkEnum);
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
                    Double priceRefCcy =
                            quoteDTO.getPriceRefCcy(portfolioCompactDTO, isTransactionBuy);
                    if (priceRefCcy == null || priceRefCcy == 0)
                    {
                        // Nothing to do
                    }
                    else
                    {
                        if (isTransactionBuy)
                        {
                            linkWithBuyQuantity((int) Math.floor(priceSelected / priceRefCcy), true);
                        }
                        else
                        {
                            linkWithSellQuantity((int) Math.floor(priceSelected / priceRefCcy), true);
                        }
                    }
                }

                Integer selectedQuantity = isTransactionBuy ? mBuyQuantity : mSellQuantity;
                mQuantity = selectedQuantity != null ? selectedQuantity : 0;
                updateBuySellDialog();
            }
        };
    }

    private class BuySellCallback implements retrofit.Callback<SecurityPositionDetailDTO>
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
            //if (pushPortfolioFragmentRunnable != null)
            //{
            //    pushPortfolioFragmentRunnable.pushPortfolioFragment(securityPositionDetailDTO);
            //}
        }

        private void onFinish()
        {
            if (transactionDialog != null)
            {
                transactionDialog.dismiss();
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

    public interface BuySellTransactionListener
    {
        void onTransactionSuccessful(boolean isBuy);

        void onTransactionFailed(boolean isBuy, THException error);
    }
}
