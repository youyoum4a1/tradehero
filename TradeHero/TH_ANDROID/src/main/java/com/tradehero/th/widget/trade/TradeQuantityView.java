package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityType;
import com.tradehero.th.base.THUser;
import com.tradehero.th.utills.DateUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 3:44 PM To change this template use File | Settings | File Templates. */
public class TradeQuantityView extends TableLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = TradeQuantityView.class.getSimpleName();

    public static final int COLOR_ID_PL_NEUTRAL = R.color.black;
    public static final int COLOR_ID_PL_GAIN = R.color.projected_pl_gain;
    public static final int COLOR_ID_PL_LOSS = R.color.projected_pl_loss;

    private TextView mSecurityType;
    private TextView mPriceAsOf;
    private TextView mCashAvailable;
    private TextView mShareAvailable;
    private TextView mQuantity;
    private TextView mTradeValue;
    private TextView mProjectedPLValue;

    private TableRow mCashAvailableRow;
    private TableRow mShareAvailableRow;
    private TableRow mQuantityRow;
    private TableRow mTradeValueRow;
    private TableRow mProjectedPLRow;

    private SecurityCompactDTO securityCompactDTO;
    private SecurityPositionDetailDTO securityPositionDetailDTO;
    private QuoteDTO quoteDTO;
    private boolean buy = true;
    private boolean refreshingQuote = false;
    private double shareQuantity;
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
        display();
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
        display();
    }

    public double getShareQuantity()
    {
        return shareQuantity;
    }

    public void setShareQuantity(double shareQuantity)
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
        initView();
    }

    private void initView()
    {
        mSecurityType = (TextView) findViewById(R.id.sec_type);
        mPriceAsOf = (TextView) findViewById(R.id.vprice_as_of);
        mCashAvailable = (TextView) findViewById(R.id.vcash_available);
        mShareAvailable = (TextView) findViewById(R.id.vshare_available);
        mQuantity = (TextView) findViewById(R.id.vquantity);
        mTradeValue = (TextView) findViewById(R.id.vtrade_value);
        mProjectedPLValue = (TextView) findViewById(R.id.p_and_l_value);
        mCashAvailableRow = (TableRow) findViewById(R.id.cash_available_row);
        mShareAvailableRow = (TableRow) findViewById(R.id.share_available_row);
        mQuantityRow = (TableRow) findViewById(R.id.quantity_row);
        mTradeValueRow = (TableRow) findViewById(R.id.trade_value_row);
        mProjectedPLRow = (TableRow) findViewById(R.id.p_and_l_value_row);
        mNormalQuantityColor = getResources().getColor(android.R.color.transparent);
        mHighlightQuantityColor = getResources().getColor(R.color.trade_highlight_share_quantity);
        display();
    }

    @Override public void display(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        display();
    }

    public void display(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        if (securityPositionDetailDTO != null)
        {
            securityCompactDTO = securityPositionDetailDTO.security;
        }
        display();
    }

    public void display(QuoteDTO quoteDTO)
    {
        this.quoteDTO = quoteDTO;
        display();
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

    public void displaySecurityType()
    {
        if (mSecurityType != null)
        {
            if (securityCompactDTO != null)
            {
                mSecurityType.setText(SecurityType.getStringResourceId(securityCompactDTO.getSecurityType()));
            }
            else
            {
                mSecurityType.setText("");
            }
        }
    }

    public void displayPriceAsOf()
    {
        if (mPriceAsOf != null)
        {
            if (quoteDTO != null)
            {
                mPriceAsOf.setText(DateUtils.getFormatedTrendDate(quoteDTO.asOfUtc));
            }
            else if (securityCompactDTO != null)
            {
                mPriceAsOf.setText(DateUtils.getFormatedTrendDate(securityCompactDTO.lastPriceDateAndTimeUtc));
            }
            else
            {
                mPriceAsOf.setText("");
            }
        }
    }

    public void displayCashAvailable()
    {
        double cashAvailable = THUser.getCurrentUser().portfolio.cashBalance;
        if (mCashAvailable != null)
        {
            if (cashAvailable == (int) cashAvailable)
            {
                mCashAvailable.setText(String.format("US$ %,d", (int) cashAvailable));
            }
            else
            {
                mCashAvailable.setText(String.format("US$ %,.2f", cashAvailable));
            }
        }
    }

    public void displayCashAvailableRow()
    {
        if (mCashAvailableRow != null)
        {
            mCashAvailableRow.setVisibility(buy ? VISIBLE : GONE);
        }
    }

    public void displayShareAvailable()
    {
        if (mShareAvailable != null)
        {
            if (securityPositionDetailDTO == null || securityPositionDetailDTO.positions == null || securityPositionDetailDTO.positions.size() == 0)
            {
                mShareAvailable.setText("0");
            }
            else
            {
                // TODO handle the case when we have more than 1 position
                Integer sharesAvailable = securityPositionDetailDTO.positions.get(0).shares;
                if (sharesAvailable == null || sharesAvailable.intValue() == 0)
                {
                    mShareAvailable.setText("0");
                }
                else
                {
                    mShareAvailable.setText(String.format("%,d", sharesAvailable));
                }
            }
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
            if (shareQuantity == (int) shareQuantity)
            {
                mQuantity.setText(String.format("%,d", (int) shareQuantity));
            }
            else
            {
                mQuantity.setText(String.format("%,.2f", shareQuantity));
            }
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
            if (buy && quoteDTO != null && quoteDTO.ask != null && quoteDTO.toUSDRate != null)
            {
                mTradeValue.setText(String.format("US$ %,.2f", shareQuantity * quoteDTO.ask * quoteDTO.toUSDRate));
            }
            else if (!buy && quoteDTO != null && quoteDTO.bid != null && quoteDTO.toUSDRate != null)
            {
                mTradeValue.setText(String.format("US$ %,.2f", shareQuantity * quoteDTO.bid * quoteDTO.toUSDRate));
            }
            else
            {
                mTradeValue.setText("-");
            }
        }
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
            if (securityPositionDetailDTO != null && securityPositionDetailDTO.positions != null &&
                    securityPositionDetailDTO.positions.get(0).averagePriceRefCcy != null &&
                    quoteDTO != null && quoteDTO.bid != null && quoteDTO.toUSDRate != null)
            {
                double buyPrice = shareQuantity * securityPositionDetailDTO.positions.get(0).averagePriceRefCcy;
                double sellPrice = shareQuantity * quoteDTO.bid * quoteDTO.toUSDRate;
                // TODO handle transaction fee
                double plValue = sellPrice - buyPrice;
                mProjectedPLValue.setText(String.format("US$ %,.2f", plValue));
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
