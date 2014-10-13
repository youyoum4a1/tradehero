package com.tradehero.th.fragments.chinabuild.fragment.security;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.prefs.StringPreference;
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
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.th.fragments.chinabuild.fragment.ShareSellDialogFragment;
import com.tradehero.th.fragments.trade.AlertDialogUtilBuySell;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-2.
 */
public class BuySaleSecurityFragment extends DashboardFragment
{
    protected static final String KEY_BUY_OR_SALE = BuySaleSecurityFragment.class.getName() + ".buy_or_sale";
    protected static final String KEY_SECURITY_ID = BuySaleSecurityFragment.class.getName() + ".security_id";
    protected static final String KEY_PORTFOLIO_ID = BuySaleSecurityFragment.class.getName() + ".portfolio_id";
    protected static final String KEY_QUOTE_DTO = BuySaleSecurityFragment.class.getName() + ".quote_dto";
    protected static final String KEY_SECURITY_NAME = BuySaleSecurityFragment.class.getName() + ".securit_name";
    protected static final String KEY_COMPETITION_ID = BuySaleSecurityFragment.class.getName() + ".competition_id";
    protected static final String KEY_POSITION_COMPACT_DTO = BuySaleSecurityFragment.class.getName() + ".position_compact_dto";

    protected int competitionID = 0;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityId securityId;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    protected QuoteDTO quoteDTO;
    protected String securityName;
    protected Integer mTransactionQuantity = 0;
    private PortfolioId portfolioId;
    protected UserProfileDTO userProfileDTO;
    private TextWatcher mQuantityTextWatcher;

    @Inject UserProfileCache userProfileCache;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject AlertDialogUtilBuySell alertDialogUtilBuySell;
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;
    private ProgressDialog mTransactionDialog;
    private MiddleCallback<SecurityPositionDetailDTO> buySellMiddleCallback;

    @InjectView(R.id.llBuySaleLine5) LinearLayout llBuySaleLine5;//预估盈利
    @InjectView(R.id.llBuySaleLine7) LinearLayout llBuySaleLine7;//分享到社交网络
    @InjectView(R.id.share_to_social_checkbox) CheckBox mShareToSocialCheckBox;//分享到社交网络
    @InjectView(R.id.llBuySaleLineBottom) LinearLayout llBuySaleLineBottom;//确认出售

    @InjectView(R.id.buy_sell_item_title0) TextView tvTitle0;//卖出价格 or 买入价格
    @InjectView(R.id.buy_sell_item_title1) TextView tvTitle1;//卖出数量 or 买入数量
    @InjectView(R.id.buy_sell_item_title6) TextView tvTitle6;//资产余额 or 剩余股份
    @InjectView(R.id.buy_sell_item_title_bottom) TextView tvTitleBottom;

    @InjectView(R.id.tvBuySalePrice) TextView tvBuySalePrice;//交易价格
    @InjectView(R.id.tvBuySaleRate) TextView tvBuySaleRate;//涨跌幅
    @InjectView(R.id.tvBuySaleCost) TextView tvBuySaleCost;//手续费
    @InjectView(R.id.tvBuySaleTotalValue) TextView tvBuySaleTotalValue;//股票总价
    @InjectView(R.id.tvBuySaleCashLeft) TextView tvBuySaleCashLeft;//资产余额
    @InjectView(R.id.tvBuySaleMayProfit) TextView tvBuySaleMayProfit;//预估盈利

    @InjectView(R.id.seek_bar) protected SeekBar mSeekBar;

    @InjectView(R.id.vquantity) protected EditText mQuantityEditText;

    private boolean isBuy = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getSecurityName());
        setHeadViewMiddleSub(securityId.getDisplayName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.buy_sale_layout, container, false);
        ButterKnife.inject(this, view);
        init();
        initView();
        updateHeadView(true);
        mShareToSocialCheckBox.setChecked(true);
        return view;
    }

    @Override public void updateHeadView(boolean display)
    {
        super.updateHeadView(display);
    }

    public void initView()
    {
        tvBuySalePrice.setText(String.valueOf(getLabel()));
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

        mSeekBar.setOnSeekBarChangeListener(createSeekBarListener());
        Integer maxValue = getMaxValue(isBuy);
        if (maxValue != null)
        {
            mSeekBar.setMax(maxValue);
            mSeekBar.setEnabled(maxValue > 0);
        }
    }

    protected String getLabel()
    {
        if (isBuy)
        {
            THSignedNumber bThSignedNumber = THSignedMoney
                    .builder(quoteDTO.ask)
                    .withOutSign()
                    .currency(securityCompactDTO == null ? "-" : securityCompactDTO.getCurrencyDisplay())
                    .build();
            return bThSignedNumber.toString();
        }
        else
        {
            THSignedNumber sthSignedNumber = THSignedMoney
                    .builder(quoteDTO.bid)
                    .withOutSign()
                    .currency(securityCompactDTO == null ? "-" : securityCompactDTO.getCurrencyDisplay())
                    .build();
            return sthSignedNumber.toString();
        }
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
                    //mPriceSelectionMethod = AnalyticsConstants.Slider;
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

    private int getTradeQuantityFrom(@NotNull String string)
    {
        int val = 0;
        try
        {
            val = Integer.parseInt(string.trim());
            if (val > getMaxValue(isBuy))
            {
                val = getMaxValue(isBuy);
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

    private ActionMode.Callback createActionModeCallBackForQuantityEditText()
    {
        //We want to disable action mode since it's irrelevant
        return new ActionMode.Callback()
        {
            @Override public boolean onCreateActionMode(ActionMode actionMode, android.view.Menu menu)
            {
                return false;
            }

            @Override public boolean onPrepareActionMode(ActionMode actionMode, android.view.Menu menu)
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

    private void init()
    {
        isBuy = getIsBuy();
        setBuyOrSale(isBuy);
        securityCompactDTO = securityCompactCache.get(getSecurityId());
        portfolioCompactDTO = portfolioCompactCache.get(getPortfolioId());
        quoteDTO = getBundledQuoteDTO();
        competitionID = getCompetitionID();

        if (competitionID == 0)
        {
            SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);

            if (detailDTO != null)
            {
                positionDTOCompactList = detailDTO.positions;
            }
        }
        else
        {
            positionDTOCompactList = getPositionDTOCompactList();
        }

        clampQuantity(true);
        linkWith(userProfileCache.get(currentUserId.toUserBaseKey()));
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

    public void updateTransactionDialog()
    {
        updateSeekbar();
        updateQuantityView();
        updateProfitLoss();
        updateTradeValueAndCashShareLeft();
        //updateConfirmButton();
        updateRate();
        updateCostUSD();
    }

    private void updateRate()
    {
        if (securityCompactDTO != null)
        {
            tvBuySaleRate.setText("" + securityCompactDTO.risePercent);
            THSignedNumber roi = THSignedPercentage.builder(securityCompactDTO.risePercent * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvBuySaleRate.setText(roi.toString());
            tvBuySaleRate.setTextColor(getResources().getColor(roi.getColorResId()));
        }
    }

    private void updateTradeValueAndCashShareLeft()
    {
        tvBuySaleCashLeft.setText(getCashShareLeft());
        tvBuySaleTotalValue.setText(getTradeValueText());
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

    public String getCashShareLeft()
    {
        if (isBuy)
        {
            String cashLeftText = getResources().getString(R.string.na);
            if (quoteDTO != null)
            {
                Double priceRefCcy = getPriceCcy();
                if (priceRefCcy != null && portfolioCompactDTO != null)
                {
                    double value = mTransactionQuantity * priceRefCcy;

                    double cashAvailable = portfolioCompactDTO.cashBalance;
                    THSignedNumber thSignedNumber = THSignedMoney
                            .builder(cashAvailable - value)
                            .withOutSign()
                            .currency(portfolioCompactDTO.currencyDisplay)
                            .build();
                    cashLeftText = thSignedNumber.toString();
                }
            }
            return cashLeftText;
        }
        else
        {
            String shareLeftText = getResources().getString(R.string.na);
            if (quoteDTO != null)
            {
                Double priceRefCcy = getPriceCcy();
                if (priceRefCcy != null && portfolioCompactDTO != null)
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
            }
            return shareLeftText;
        }
    }

    public Double getPriceCcy()
    {
        if (quoteDTO == null)
        {
            return null;
        }
        return quoteDTO.getPriceRefCcy(portfolioCompactDTO, isBuy);
    }

    private void updateCostUSD()
    {
        tvBuySaleCost.setText(portfolioCompactDTO.currencyDisplay + portfolioCompactDTO.getProperTxnCostUsd());
    }

    private void updateProfitLoss()
    {
        Double profitLoss = getProfitOrLoss(isBuy);
        if (profitLoss != null && mTransactionQuantity != null && mTransactionQuantity > 0)
        {
            tvBuySaleMayProfit.setText(
                    THSignedMoney.builder(profitLoss)
                            .withSign()
                            .build().toString());
        }
        else
        {
            tvBuySaleMayProfit.setText("--");
        }
    }

    protected Double getProfitOrLoss(boolean isBuy)
    {
        if (isBuy)
        {
            return null;
        }
        else
        {
            if (positionDTOCompactList == null || portfolioCompactDTO == null)
            {
                return null;
            }
            Double netProceeds = positionDTOCompactList.getNetSellProceedsUsd(
                    mTransactionQuantity,
                    quoteDTO,
                    getPortfolioId(),
                    true,
                    portfolioCompactDTO.getProperTxnCostUsd());
            Double totalSpent = positionDTOCompactList.getSpentOnQuantity(mTransactionQuantity, getPortfolioId());
            if (netProceeds == null || totalSpent == null)
            {
                return null;
            }
            return netProceeds - totalSpent;
        }
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

    protected Integer clampedQuantity(Integer candidate)
    {
        Integer maxTransactionValue = getMaxValue(isBuy);
        if (candidate == null || maxTransactionValue == null)
        {
            return 0;
        }
        return Math.min(candidate, maxTransactionValue);
    }

    protected Integer getMaxValue(boolean isBuy)
    {
        if (isBuy)
        {
            return getMaxPurchasableShares();
        }
        else
        {
            return getMaxSellableShares();
        }
    }

    public Integer getMaxSellableShares()
    {
        return positionDTOCompactList == null ? null :
                positionDTOCompactList.getMaxSellableShares(
                        this.quoteDTO,
                        this.portfolioCompactDTO);
    }

    public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO);
    }

    private void linkWith(UserProfileDTO updatedUserProfileDTO)
    {
        this.userProfileDTO = updatedUserProfileDTO;
    }

    protected boolean getIsBuy()
    {
        return getArguments().getBoolean(KEY_BUY_OR_SALE, true);
    }

    protected String getSecurityName()
    {
        if (this.securityName == null)
        {
            this.securityName = getArguments().getString(KEY_SECURITY_NAME);
        }
        return securityName;
    }

    protected SecurityId getSecurityId()
    {
        if (this.securityId == null)
        {
            this.securityId = new SecurityId(getArguments().getBundle(KEY_SECURITY_ID));
        }
        return securityId;
    }

    protected int getCompetitionID()
    {
        if (this.competitionID == 0)
        {
            this.competitionID = getArguments().getInt(KEY_COMPETITION_ID, 0);
        }
        return competitionID;
    }

    protected PortfolioId getPortfolioId()
    {
        if (this.portfolioId == null)
        {
            this.portfolioId = new PortfolioId(getArguments().getBundle(KEY_PORTFOLIO_ID));
        }
        return portfolioId;
    }

    protected PositionDTOCompactList getPositionDTOCompactList()
    {
        if (this.positionDTOCompactList == null)
        {
            this.positionDTOCompactList = (PositionDTOCompactList) getArguments().getSerializable(KEY_POSITION_COMPACT_DTO);
        }
        return positionDTOCompactList;
    }

    private QuoteDTO getBundledQuoteDTO()
    {
        return new QuoteDTO(getArguments().getBundle(KEY_QUOTE_DTO));
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        Timber.d("OnRusme: StockGodList 1 ");
    }

    public void setBuyOrSale(boolean isBuy)
    {
        this.isBuy = isBuy;
        if (isBuy)
        {
            setBuyView();
        }
        else
        {
            setSaleView();
        }
    }

    public void setSaleView()
    {
        llBuySaleLine5.setVisibility(View.VISIBLE);
        //llBuySaleLine7.setVisibility(View.VISIBLE);
        tvTitle0.setText("卖出价格：");
        tvTitle1.setText("卖出数量：");
        tvTitle6.setText("剩余股份：");
        tvTitleBottom.setText("确定出售");
    }

    public void setBuyView()
    {
        llBuySaleLine5.setVisibility(View.GONE);
        //llBuySaleLine7.setVisibility(View.GONE);
        tvTitle0.setText("买入价格：");
        tvTitle1.setText("买入数量：");
        tvTitle6.setText("资产余额：");
        tvTitleBottom.setText("确定购买");
    }

    @OnClick(R.id.llBuySaleLineBottom)
    public void onBuySaleClicked()
    {
        Timber.d("onBuySaleClicked!!!");
        launchBuySell();
    }

    private void launchBuySell()
    {
        detachBuySellMiddleCallback();

        if (checkValidToTransact())
        {
            TransactionFormDTO transactionFormDTO = getBuySellOrder();
            if (transactionFormDTO != null)
            {
                mTransactionDialog = progressDialogUtil.show(BuySaleSecurityFragment.this.getActivity(),
                        R.string.processing, R.string.alert_dialog_please_wait);

                buySellMiddleCallback = getTransactionMiddleCallback(transactionFormDTO);
            }
            else
            {
                alertDialogUtilBuySell.informBuySellOrderWasNull(getActivity());
            }
        }
    }

    protected MiddleCallback<SecurityPositionDetailDTO> getTransactionMiddleCallback(TransactionFormDTO transactionFormDTO)
    {

        return securityServiceWrapper.doTransaction(
                securityId, transactionFormDTO, isBuy,
                new BuySellCallback(isBuy));
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
                //alertDialogUtilBuySell.informBuySellOrderReturnedNull(getActivity());
                return;
            }

            if (isBuy)
            {
                THToast.show("购买成功!");
            }
            else
            {
                THToast.show("出售成功!");
            }

            if (mShareToSocialCheckBox.isChecked())
            {
                if (isBuy)
                {
                    //buy share
                    mShareSheetTitleCache.set(getString(R.string.share_buy_dialog_summary,
                            securityId.getDisplayName(), currentUserId.get().toString(),
                            securityCompactDTO.id.toString()));
                    ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                            getString(R.string.share_buy_dialog_title, securityId.getDisplayName()));
                }
                else
                {
                    //sell share
                    ShareSellDialogFragment.showSellDialog(
                            getActivity().getSupportFragmentManager(), getSecurityName(),
                            securityId.getDisplayName(), tvBuySaleRate.getText().toString(),
                            mQuantityEditText.getText().toString(),
                            tvBuySaleMayProfit.getText().toString(),
                            currentUserId.get().toString(), securityCompactDTO.id.toString());
                }
            }
            popCurrentFragment();
            //if (buySellTransactionListener != null)
            //{
            //    buySellTransactionListener.onTransactionSuccessful(isBuy);
            //}
        }

        private void onFinish()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }
            mTransactionDialog.dismiss();
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

            //if (buySellTransactionListener != null)
            //{
            //    buySellTransactionListener.onTransactionFailed(isBuy, thException);
            //}
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
                null,
                null,
                null,//shareForTransaction(SocialNetworkEnum.LN),
                null,//shareForTransaction(SocialNetworkEnum.WB),
                //shareLocation ? null : null, // TODO implement location
                //shareLocation ? null : null,
                //shareLocation ? null : null,
                null,
                null,
                null,
                //sharePublic,
                false,
                null,//unSpannedComment != null ? unSpannedComment.toString() : null,
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

    private boolean checkValidToTransact()
    {
        return securityId != null && securityId.getExchange() != null
                && securityId.getSecuritySymbol() != null && mTransactionQuantity != 0;
    }
}
