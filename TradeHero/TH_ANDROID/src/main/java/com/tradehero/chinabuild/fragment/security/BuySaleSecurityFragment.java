package com.tradehero.chinabuild.fragment.security;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.chinabuild.fragment.ShareSellDialogFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.MainActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trade.AlertDialogUtilBuySell;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by huhaiping on 14-9-2.
 */
public class BuySaleSecurityFragment extends DashboardFragment
{
    public static final String KEY_IS_BUY_DIRECTLY = BuySaleSecurityFragment.class.getName() + ".is_buy_directly";

    public static final String KEY_BUY_OR_SALE = BuySaleSecurityFragment.class.getName() + ".buy_or_sale";
    public static final String KEY_SECURITY_ID = BuySaleSecurityFragment.class.getName() + ".security_id";
    public static final String KEY_PORTFOLIO_ID = BuySaleSecurityFragment.class.getName() + ".portfolio_id";
    public static final String KEY_QUOTE_DTO = BuySaleSecurityFragment.class.getName() + ".quote_dto";
    public static final String KEY_SECURITY_NAME = BuySaleSecurityFragment.class.getName() + ".securit_name";
    public static final String KEY_COMPETITION_ID = BuySaleSecurityFragment.class.getName() + ".competition_id";
    public static final String KEY_POSITION_COMPACT_DTO = BuySaleSecurityFragment.class.getName() + ".position_compact_dto";
    public static final String KEY_PRE_CLOSE = BuySaleSecurityFragment.class.getName() + ".preclose";

    protected DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO> securityPositionDetailListener;

    protected int competitionID = 0;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityId securityId;
    protected PositionDTOCompactList positionDTOCompactList;
    public PortfolioCompactDTO portfolioCompactDTO;
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
    private ProgressDialog mBuyDirectlyDialog;
    private MiddleCallback<SecurityPositionDetailDTO> buySellMiddleCallback;

    @InjectView(R.id.llBuySaleLine5) LinearLayout llBuySaleLine5;//预估盈利
    @InjectView(R.id.share_to_social_checkbox) CheckBox mShareToSocialCheckBox;//分享到社交网络

    @InjectView(R.id.llBuySaleLine8) LinearLayout llBuySaleLine8;//成本均价
    @InjectView(R.id.llBuySaleLine9) LinearLayout llBuySaleLine9;//持有股数

    @InjectView(R.id.buy_sell_item_title0) TextView tvTitle0;//卖出价格 or 买入价格
    @InjectView(R.id.buy_sell_item_title1) TextView tvTitle1;//卖出数量 or 买入数量
    @InjectView(R.id.buy_sell_item_title6) TextView tvTitle6;//资产余额 or 剩余股份
    @InjectView(R.id.buy_sell_item_title_bottom) TextView tvTitleBottom;

    @InjectView(R.id.tvBuySalePrice) TextView tvBuySalePrice;//交易价格
    @InjectView(R.id.tvBuySaleRate) TextView tvBuySaleRate;//涨跌幅
    @InjectView(R.id.tvBuySaleTotalValue) TextView tvBuySaleTotalValue;//股票总价
    @InjectView(R.id.tvBuySaleCashLeft) TextView tvBuySaleCashLeft;//资产余额
    @InjectView(R.id.tvBuySaleMayProfit) TextView tvBuySaleMayProfit;//预估盈利

    @InjectView(R.id.tvBuySaleShared) TextView tvBuySaleShared;//持有股数
    @InjectView(R.id.tvBuySaleALLAV) TextView tvBuySaleALLAV;//成本均价

    @InjectView(R.id.seek_bar) protected SeekBar mSeekBar;

    @InjectView(R.id.vquantity) protected EditText mQuantityEditText;

    private boolean isBuyDirectly = false;
    private boolean isBuy = false;
    private boolean isSending = false;
    private boolean isLoadingBuyDirectly = false;

    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    private OwnedPortfolioId shownPortfolioId;
    @Nullable protected QuoteDTO quoteDTO;
    public ShareDialogFragment shareDialogFragment;

    @Inject Analytics analytics;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    private Callback<QuoteDTO> quoteCallback;

    private Double preClose;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        securityPositionDetailListener = new AbstractBuySellSecurityPositionCacheListener();
        portfolioCompactListFetchListener = new BasePurchaseManagementPortfolioCompactListFetchListener();

        showBuyDirctlyLoadingDialog();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getSecurityName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.buy_sale_layout, container, false);
        ButterKnife.inject(this, view);
        isBuyDirectly = getArguments().getBoolean(KEY_IS_BUY_DIRECTLY, false);
        preClose = getArguments().getDouble(KEY_PRE_CLOSE, 0);

        if (isBuyDirectly)
        {
            initDirectly();
            initView();
        }
        else
        {
            init();
        }

        updateHeadView(true);
        mShareToSocialCheckBox.setChecked(true);
        isSending = false;
        return view;
    }

    public void initView()
    {
        tvBuySalePrice.setText(String.valueOf(getLabel()));
        mQuantityEditText.setText(String.valueOf(mTransactionQuantity));
        mQuantityEditText.addTextChangedListener(getQuantityTextChangeListener());
        mQuantityEditText.setCustomSelectionActionModeCallback(createActionModeCallBackForQuantityEditText());
        mQuantityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        mSeekBar.setOnSeekBarChangeListener(createSeekBarListener());
        Integer maxValue = getMaxValue(isBuy);
        if (maxValue != null)
        {
            mSeekBar.setMax(maxValue);
            mSeekBar.setEnabled(maxValue > 0);

            if (!isBuy)
            {//卖出设置为最大
                int progressMax = 100;
                mSeekBar.setProgress(progressMax);
                mTransactionQuantity = maxValue;
                updateTransactionDialog();
            }
        }
    }

    protected String getLabel()
    {
        if (isBuy)
        {
            if (quoteDTO == null || quoteDTO.ask == null) return "- -";
            THSignedNumber bThSignedNumber = THSignedMoney
                    .builder(quoteDTO.ask)
                    .withOutSign()
                    .currency(securityCompactDTO == null ? "-" : securityCompactDTO.getCurrencyDisplay())
                    .build();
            return bThSignedNumber.toString();
        }
        else
        {
            if (quoteDTO == null || quoteDTO.bid == null) return "- -";
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

    private int getTradeQuantityFrom(String string) {
        if(TextUtils.isEmpty(string)){
            return 0;
        }
        int val = 0;
        try {
            val = Integer.valueOf(string.trim());
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

    private void initDirectly()
    {
        isBuy = true;
        setBuyOrSale(isBuy);

        securityId = getSecurityId();
        getSecurityPositionDetailDTO();
        fetchPortfolioCompactList(false);
        linkWith(userProfileCache.get(currentUserId.toUserBaseKey()));
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

        this.mTransactionQuantity = clampedQuantity(mTransactionQuantity);
        updateTransactionDialog();
        linkWith(userProfileCache.get(currentUserId.toUserBaseKey()));
    }

    public void updateTransactionDialog()
    {
        if (getActivity() == null) {
            return;
        }
        mSeekBar.setProgress(mTransactionQuantity);
        mQuantityEditText.setText(String.valueOf(mTransactionQuantity));
        mQuantityEditText.setSelection(mQuantityEditText.getText().length());

        Double profitLoss = getProfitOrLoss(isBuy);
        if (profitLoss != null && mTransactionQuantity != null && mTransactionQuantity > 0) {
            tvBuySaleMayProfit.setText(
                    THSignedMoney.builder(profitLoss)
                            .withSign()
                            .build().toString());

            tvBuySaleMayProfit.setTextColor(getResources().getColor(ColorUtils.getColorResourceIdForNumber(profitLoss)));
        } else {
            tvBuySaleMayProfit.setText("--");
        }
        tvBuySaleCashLeft.setText(getCashShareLeft());
        tvBuySaleTotalValue.setText(getTradeValueText());

        Double rate = getRiseRate();
        if ((tvBuySaleRate != null) && (rate != null)) {
            THSignedNumber roi = THSignedPercentage.builder(rate * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvBuySaleRate.setText(roi.toString());
            tvBuySaleRate.setTextColor(getResources().getColor(roi.getColorResId()));
        }

        updatePositionInfo();
        tvBuySalePrice.setText(String.valueOf(getLabel()));
    }

    private Double getRiseRate() {
        if (quoteDTO == null
        || quoteDTO.bid == null
        || quoteDTO.ask == null
        || preClose == null
        || preClose == 0) {
            return null;
        }

        if (isBuy) {
            return (quoteDTO.ask - preClose) / preClose;
        } else {
            return (quoteDTO.bid - preClose) / preClose;
        }
    }

    //显示 累计盈亏，持有股数，成本均价
    public void updatePositionInfo() {
        if (positionDTOCompactList != null && portfolioCompactDTO != null) {
            int shared = positionDTOCompactList.getShareCountIn(portfolioCompactDTO.getPortfolioId());
            double avPrice = positionDTOCompactList.getAvPrice(portfolioCompactDTO.getPortfolioId());

            if (shared != 0 && avPrice != 0) {
                tvBuySaleShared.setText(String.valueOf(shared));
                tvBuySaleALLAV.setText(securityCompactDTO.getCurrencyDisplay() + " " + PositionDTOCompact.getShortDouble(avPrice));
                llBuySaleLine8.setVisibility(View.VISIBLE);
                llBuySaleLine9.setVisibility(View.VISIBLE);
                return;
            }
        }
        llBuySaleLine8.setVisibility(View.GONE);
        llBuySaleLine9.setVisibility(View.GONE);
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
                        .currency(portfolioCompactDTO.getCurrencyDisplay())
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
                            .currency(portfolioCompactDTO.getCurrencyDisplay())
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
        quoteServiceWrapper.stopQuoteTask();
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
        refreshQuote();
    }

    @Override public void onPause()
    {
        super.onPause();
        quoteServiceWrapper.stopQuoteTask();
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
        tvTitle0.setText("卖出价格：");
        tvTitle1.setText("卖出数量：");
        tvTitle6.setText("剩余股份：");
        tvTitleBottom.setText("确定出售");
    }

    public void setBuyView()
    {
        llBuySaleLine5.setVisibility(View.GONE);
        tvTitle0.setText("买入价格：");
        tvTitle1.setText("买入数量：");
        tvTitle6.setText("资产余额：");
        tvTitleBottom.setText("确定购买");
    }

    @OnClick(R.id.llBuySaleLineBottom)
    public void onBuySaleClicked()
    {
        if (isSending) return;
        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_BUY_SALE_CONFIRM));
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
                isSending = true;
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
            //onFinish();

            if (securityPositionDetailDTO == null)
            {
                return;
            }

            if (isBuy)
            {
                THToast.show("购买成功!");
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_BUY_SUCCESSFULLY));
            }
            else
            {
                THToast.show("出售成功!");
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_SALE_SUCCESSFULLY));
            }
            if(isBuyDirectly){
                onFinishBuyDirectlyLoading();
            }
            if (mShareToSocialCheckBox!=null && mShareToSocialCheckBox.isChecked())
            {
                if (isBuy)
                {
                    //buy share
                    if (getActivity() == null)
                    {
                        return;
                    }
                    String endPoint = THSharePreferenceManager.getShareEndPoint(getActivity() );
                    mShareSheetTitleCache.set(getString(R.string.share_buy_dialog_summary,
                            securityId.getDisplayName(), currentUserId.get().toString(),
                            securityCompactDTO.id.toString(), endPoint));

                    shareDialogFragment = ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                            getString(R.string.share_buy_dialog_title, securityId.getDisplayName()), getString(R.string.share_buy_dialog_summary,
                            securityId.getDisplayName(), currentUserId.get().toString(),
                            securityCompactDTO.id.toString(), endPoint));

                    shareDialogFragment.setOnDismissListener(new ShareDialogFragment.DialogDissmissListener()
                    {
                        @Override public void onDismissDialog()
                        {
                           popCurrentFragment();
                        }
                    });
                }
                else
                {
                    if (securityPositionDetailDTO == null || securityPositionDetailDTO.positionId <= 0)
                    {
                        return;
                    }
                    int positionId = securityPositionDetailDTO.positionId;
                    Double profitLoss = getProfitOrLoss(isBuy);
                    //sell share
                    ShareSellDialogFragment.showSellDialog(
                            getActivity().getSupportFragmentManager(), getSecurityName(),
                            securityId.getDisplayName(), tvBuySaleRate.getText().toString(),
                            mQuantityEditText.getText().toString(),
                            tvBuySaleMayProfit.getText().toString(),
                            currentUserId.get().toString(), String.valueOf(positionId), String.valueOf(securityPositionDetailDTO.tradeId),
                            profitLoss);
                }
            }else{
                if(isBuyDirectly){
                    //跟买后，如果没有分享则直接退出该页面。
                    popCurrentFragment();
                }
            }
            if(!isBuyDirectly)
            {
                //正常流程，退出前先获取自己的持仓
                ExitBuySellFragment();
            }

        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            THException thException = new THException(retrofitError);
            THToast.show(thException);
        }
    }

    private void onFinish() {
        if (mTransactionDialog != null) {
            mTransactionDialog.dismiss();
        }
        isSending = false;
    }


    private void showBuyDirctlyLoadingDialog() {
        mBuyDirectlyDialog = progressDialogUtil.show(BuySaleSecurityFragment.this.getActivity(),
                R.string.processing, R.string.alert_dialog_please_wait);
        mBuyDirectlyDialog.setCancelable(true);
        mBuyDirectlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override public void onCancel(DialogInterface dialogInterface)
            {
                loadBuyDirectlyFailed();
            }
        });
        isLoadingBuyDirectly = true;
    }

    private void onFinishBuyDirectlyLoading() {
        if (mBuyDirectlyDialog != null) {
            mBuyDirectlyDialog.dismiss();
        }
        isLoadingBuyDirectly = false;
    }

    public void ExitBuySellFragment()
    {
        getPositionDirectly(currentUserId.toUserBaseKey());
    }

    public TransactionFormDTO getBuySellOrder()
    {
        if (quoteDTO == null)
        {
            return null;
        }
        if (portfolioId == null){
            return null;
        }

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

    private MiddleCallback<GetPositionsDTO> getPositionDTOCallback;
    @Inject Lazy<PositionServiceWrapper> positionServiceWrapper;

    protected void getPositionDirectly(@NotNull UserBaseKey heroId)
    {
        detachGetPositionMiddleCallback();
        getPositionDTOCallback =
                positionServiceWrapper.get()
                        .getPositionsDirect(heroId.key, 1, 20, new GetPositionCallback());
    }

    private void detachGetPositionMiddleCallback()
    {
        if (getPositionDTOCallback != null)
        {
            getPositionDTOCallback.setPrimaryCallback(null);
        }
        getPositionDTOCallback = null;
    }

    public class GetPositionCallback implements Callback<GetPositionsDTO> {
        @Override public void success(GetPositionsDTO getPositionsDTO, Response response) {
            onFinish();
            MainActivity.setGetPositionDTO(getPositionsDTO);
            if(getActivity()!=null) {
                popCurrentFragment();
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            if(getActivity()!=null) {
                popCurrentFragment();
            }
        }
    }

    public void getSecurityPositionDetailDTO()
    {
        SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);
        if (detailDTO != null)
        {
            linkWith(detailDTO, true);
        }
        else
        {
            requestPositionDetail();
        }
    }

    private void linkWith(SecurityPositionDetailDTO detailDTO, boolean andDisplay)
    {
        this.securityPositionDetailDTO = detailDTO;

        if (securityPositionDetailDTO != null)
        {
            linkWith(securityPositionDetailDTO.security);
            this.positionDTOCompactList = securityPositionDetailDTO.positions;
        }
    }

    public void linkWith(final SecurityCompactDTO securityCompactDTO)
    {
        //if (!securityCompactDTO.getSecurityId().equals(this.securityId))
        //{
        //    throw new IllegalArgumentException("This security compact is not for " + this.securityId);
        //}
        this.securityCompactDTO = securityCompactDTO;
        if (!StringUtils.isNullOrEmpty(securityCompactDTO.name))
        {
            this.securityName = securityCompactDTO.name;
            setHeadViewMiddleMain(securityCompactDTO.name);
        }

        updateBuyDirectlyInfoLoading();
    }

    protected void requestPositionDetail()
    {
        detachSecurityPositionDetailCache();
        securityPositionDetailCache.get().register(this.securityId, securityPositionDetailListener);
        securityPositionDetailCache.get().getOrFetchAsync(this.securityId);
    }

    protected void detachSecurityPositionDetailCache()
    {
        securityPositionDetailCache.get().unregister(securityPositionDetailListener);
    }

    protected class AbstractBuySellSecurityPositionCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO>
    {
        @Override public void onDTOReceived(@NotNull final SecurityId key, @NotNull final SecurityPositionDetailDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
        {
        }
    }

    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

    private void fetchPortfolioCompactList(boolean force)
    {
        detachPortfolioCompactListCache();
        portfolioCompactListCache.register(currentUserId.toUserBaseKey(), portfolioCompactListFetchListener);
        portfolioCompactListCache.getOrFetchAsync(currentUserId.toUserBaseKey(), force);
    }

    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
        }
    }

    protected void prepareApplicableOwnedPortolioId(@Nullable PortfolioCompactDTO defaultIfNotInArgs)
    {
        if (defaultIfNotInArgs != null)
        {
            shownPortfolioId = defaultIfNotInArgs.getOwnedPortfolioId();
            portfolioId = shownPortfolioId.getPortfolioIdKey();
            portfolioCompactDTO = portfolioCompactCache.get(portfolioId);
            updateBuyDirectlyInfoLoading();
        }
    }

    protected void updateQuoteInfo(QuoteDTO quoteDTO)
    {
        this.quoteDTO = quoteDTO;

        if ((quoteDTO == null)
            || (quoteDTO.ask != null && quoteDTO.ask == 0)
            || (quoteDTO.bid != null && quoteDTO.bid == 0)) {
            return;
        }

        if (isBuyOrSaleValid()) {
            updateTransactionDialog();
            updateBuyDirectlyInfoLoading();
        } else {
            loadBuyDirectlyFailed();
        }
    }

    public void updateBuyDirectlyInfoLoading()
    {
        if(isLoadingBuyDirectly)
        {
            if (quoteDTO == null) return;
            if (securityId == null) return;
            if (securityCompactDTO == null) return;
            if (portfolioId == null) return;
            onFinishBuyDirectlyLoading();
            initView();
        }
    }

    public void loadBuyDirectlyFailed()
    {
        onFinishBuyDirectlyLoading();
        popCurrentFragment();
    }

    public static final int ERROR_NO_ASK_BID = 0;
    public static final int ERROR_NO_ASK = 1;
    public static final int ERROR_NO_BID = 2;
    public boolean isBuyOrSaleValid()
    {
        if (quoteDTO == null) return false;
        if (quoteDTO.ask == null && quoteDTO.bid == null)
        {//ask bid 都没有返回 则说明停牌

                showBuyOrSaleError(ERROR_NO_ASK_BID);
                return false;
        }
        else if (quoteDTO.bid == null && (!isBuy))
        {//跌停

                showBuyOrSaleError(ERROR_NO_BID);
                return false;
        }
        else if (quoteDTO.ask == null && (isBuy))
        {//涨停

                showBuyOrSaleError(ERROR_NO_ASK);
                return false;
        }

        return true;
    }

    public void showBuyOrSaleError(int type)
    {
        if (type == ERROR_NO_ASK_BID)
        {
            THToast.show("这只股票停牌中");
        }
        else if (type == ERROR_NO_BID)
        {
            THToast.show("这只股票跌停了");
        }
        else if (type == ERROR_NO_ASK)
        {
            THToast.show("这只股票涨停了");
        }
    }

    void refreshQuote() {
        if (securityId == null) {
            return;
        }

        if (quoteCallback == null) {
            quoteCallback = new Callback<QuoteDTO>() {
                @Override
                public void success(QuoteDTO dto, Response response) {
                    if (dto == null) {
                        return;
                    }
                    updateQuoteInfo(dto);
                }

                @Override
                public void failure(RetrofitError error) {
                }
            };
        }
        quoteServiceWrapper.getRepeatingQuote(securityId, quoteCallback);
    }

}
