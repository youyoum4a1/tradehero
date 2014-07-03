package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.thm.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.PositionDTOCompactListUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import javax.inject.Inject;

public class TradeQuantityView extends TableLayout
{
    public static final int COLOR_ID_PL_NEUTRAL = R.color.black;
    public static final int COLOR_ID_PL_GAIN = R.color.projected_pl_gain;
    public static final int COLOR_ID_PL_LOSS = R.color.projected_pl_loss;

    @InjectView(R.id.sec_type) protected TextView mSecurityType;
    @InjectView(R.id.vprice_as_of) protected TextView mPriceAsOf;
    @InjectView(R.id.vcash_available) protected TextView mCashAvailable;
    @InjectView(R.id.vshare_available) protected TextView mShareAvailable;
    @InjectView(R.id.vquantity) protected TextView mQuantity;
    @InjectView(R.id.vtrade_value) protected TextView mTradeValue;
    @InjectView(R.id.p_and_l_value) protected TextView mProjectedPLValue;

    @InjectView(R.id.cash_available_row) protected TableRow mCashAvailableRow;
    @InjectView(R.id.share_available_row) protected TableRow mShareAvailableRow;
    @InjectView(R.id.quantity_row) protected TableRow mQuantityRow;
    @InjectView(R.id.trade_value_row) protected TableRow mTradeValueRow;
    @InjectView(R.id.p_and_l_value_row) protected TableRow mProjectedPLRow;

    @Inject PositionDTOCompactListUtil positionDTOCompactListUtil;
    private PortfolioCompactDTO portfolioCompactDTO;
    private PortfolioId portfolioId;
    private SecurityCompactDTO securityCompactDTO;
    private PositionDTOCompactList positionDTOCompactList;
    private UserProfileDTO userProfileDTO;
    private QuoteDTO quoteDTO;
    private boolean buy = true;
    private boolean refreshingQuote = false;
    private Integer shareQuantity;
    private boolean mHighlightQuantity = false;
    private int mNormalQuantityColor;
    private int mHighlightQuantityColor;
    private int colorPlNeutral;
    private int colorPlGain;
    private int colorPlLoss;

    //<editor-fold desc="Constructors">
    public TradeQuantityView(Context context)
    {
        super(context);
        init();
    }

    public TradeQuantityView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        colorPlNeutral = getResources().getColor(COLOR_ID_PL_NEUTRAL);
        colorPlGain = getResources().getColor(COLOR_ID_PL_GAIN);
        colorPlLoss = getResources().getColor(COLOR_ID_PL_LOSS);
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public boolean isBuy()
    {
        return buy;
    }

    public void setBuy(boolean buy)
    {
        this.buy = buy;
        displayCashAvailableRow();
        displayShareAvailableRow();
        displayTradeValue();
        displayProjectedPL();
        displayProjectedPLRow();
    }

    public boolean isRefreshingQuote()
    {
        return refreshingQuote;
    }

    public void setRefreshingQuote(boolean refreshingQuote)
    {
        this.refreshingQuote = refreshingQuote;
        if (mTradeValueRow != null && refreshingQuote)
        {
            mTradeValueRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out));
        }
        displayTradeValueRow();
    }

    public Integer getShareQuantity()
    {
        return shareQuantity;
    }

    public void setShareQuantity(Integer shareQuantity)
    {
        this.shareQuantity = shareQuantity;
        displayShareQuantity();
        displayTradeValue();
        displayProjectedPL();
    }

    public boolean isHighlightQuantity()
    {
        return mHighlightQuantity;
    }

    public void setHighlightQuantity(boolean highlightQuantity)
    {
        this.mHighlightQuantity = highlightQuantity;
        displayShareQuantityRow();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initView();
    }

    private void initView()
    {
        ButterKnife.inject(this);
        mNormalQuantityColor = getResources().getColor(android.R.color.transparent);
        mHighlightQuantityColor = getResources().getColor(R.color.trade_highlight_share_quantity);
        display();
    }

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (portfolioCompactDTO != null)
        {
            linkWith(portfolioCompactDTO.getPortfolioId(), andDisplay);
        }
        else
        {
            linkWith((PortfolioId) null, andDisplay);
        }
        if (andDisplay)
        {
            displayCashAvailable();
            displayTradeValue();
        }
    }

    protected void linkWith(PortfolioId portfolioId, boolean andDisplay)
    {
        this.portfolioId = portfolioId;
        if (andDisplay)
        {
            displayShareAvailable();
        }
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            displaySecurityType();
            displayPriceAsOf();
        }
    }

    public void linkWith(PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
    {
        this.positionDTOCompactList = positionDTOCompacts;
        if (andDisplay)
        {
            displayShareAvailable();
            displayProjectedPL();
        }
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            displayCashAvailable();
        }
    }

    public void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;
        if (andDisplay)
        {
            displayPriceAsOf();
            displayTradeValue();
            displayProjectedPL();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displaySecurityType();
        displayPriceAsOf();
        displayCashAvailable();
        displayCashAvailableRow();
        displayShareAvailable();
        displayShareQuantity();
        displayShareAvailableRow();
        displayTradeValue();
        displayTradeValueRow();
        displayProjectedPL();
        displayProjectedPLRow();
    }

    private void displaySecurityType()
    {
        if (mSecurityType != null)
        {
            mSecurityType.setText(getSecurityTypeText());
        }
    }

    public String getSecurityTypeText()
    {
        if (securityCompactDTO != null && securityCompactDTO.getSecurityTypeStringResourceId() != null)
        {
            return getContext().getString(securityCompactDTO.getSecurityTypeStringResourceId()).toUpperCase(); // HACK upperCase
        }
        else
        {
            return "";
        }
    }

    private void displayPriceAsOf()
    {
        if (mPriceAsOf != null)
        {
            mPriceAsOf.setText(getPriceAsOf());
        }
    }

    public String getPriceAsOf()
    {
        if (quoteDTO != null && quoteDTO.asOfUtc != null)
        {
            return DateUtils.getFormattedDate(getResources(), quoteDTO.asOfUtc);
        }
        else if (securityCompactDTO != null && securityCompactDTO.lastPriceDateAndTimeUtc != null)
        {
            return DateUtils.getFormattedDate(getResources(), securityCompactDTO.lastPriceDateAndTimeUtc);
        }
        else
        {
            return "";
        }
    }

    private void displayCashAvailable()
    {
        if (mCashAvailable != null)
        {
            mCashAvailable.setText(getCashBalanceText());
        }
    }

    public String getCashBalanceText()
    {
        if (portfolioCompactDTO != null)
        {
            double cashAvailable = portfolioCompactDTO.cashBalance;
            THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, cashAvailable, THSignedNumber.WITHOUT_SIGN, portfolioCompactDTO.currencyDisplay);
            return thSignedNumber.toString();
        }
        else
        {
            return getResources().getString(R.string.na);
        }
    }

    private void displayCashAvailableRow()
    {
        if (mCashAvailableRow != null)
        {
            mCashAvailableRow.setVisibility(buy ? VISIBLE : GONE);
        }
    }

    private void displayShareAvailable()
    {
        if (mShareAvailable != null)
        {
            mShareAvailable.setText(getShareAvailableText());
        }
    }

    public String getShareAvailableText()
    {
        if (positionDTOCompactList == null || portfolioId == null)
        {
            return "0";
        }
        else
        {
            return String.format(
                    "%,d",
                    positionDTOCompactList.getMaxSellableShares(
                            this.quoteDTO,
                            this.portfolioCompactDTO));
        }
    }

    private void displayShareAvailableRow()
    {
        if (mShareAvailableRow != null)
        {
            mShareAvailableRow.setVisibility(buy ? GONE : VISIBLE);
        }
    }

    private void displayShareQuantity()
    {
        if (mQuantity != null)
        {
            mQuantity.setText(getShareQuantityText());
        }
    }

    public String getShareQuantityText()
    {
        if (shareQuantity == null)
        {
            return "";
        }
        else if (shareQuantity == (int) (double) shareQuantity)
        {
            return String.format("%,d", (int) (double) shareQuantity);
        }
        else
        {
            return String.format("%,.2f", shareQuantity);
        }
    }

    private void displayShareQuantityRow()
    {
        if (mQuantityRow != null)
        {
            mQuantityRow.setBackgroundColor(mHighlightQuantity ? mHighlightQuantityColor : mNormalQuantityColor);
        }
    }

    private void displayTradeValue()
    {
        if (mTradeValue != null)
        {
            mTradeValue.setText(getTradeValueText());
        }
    }

    public String getTradeValueText()
    {
        if (shareQuantity == null || quoteDTO == null)
        {
            return "-";
        }
        Double priceRefCcy = quoteDTO.getPriceRefCcy(portfolioCompactDTO, buy);
        if (priceRefCcy == null || portfolioCompactDTO == null)
        {
            return "-";
        }
        THSignedNumber thTradeValue = new THSignedNumber(THSignedNumber.TYPE_MONEY, shareQuantity * priceRefCcy, THSignedNumber.WITHOUT_SIGN, portfolioCompactDTO.currencyDisplay);
        return thTradeValue.toString();
    }

    private void displayTradeValueRow()
    {
        if (mTradeValueRow != null)
        {
            if (!refreshingQuote)
            {
                mTradeValueRow.clearAnimation();
                mTradeValueRow.setAlpha(1);
            }
        }
    }

    private void displayProjectedPL()
    {
        if (mProjectedPLValue != null)
        {
            Double plValue = positionDTOCompactListUtil.projectedPLValue(positionDTOCompactList, quoteDTO, shareQuantity);
            if (plValue != null)
            {
                mProjectedPLValue.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, plValue));
                mProjectedPLValue.setTextColor(plValue == 0 ? colorPlNeutral : plValue > 0 ? colorPlGain : colorPlLoss);
            }
            else
            {
                mProjectedPLValue.setText("-");
            }
        }
    }

    private void displayProjectedPLRow()
    {
        if (mProjectedPLRow != null)
        {
            mProjectedPLRow.setVisibility(buy ? View.INVISIBLE : View.VISIBLE);
        }
    }
    //</editor-fold>
}
