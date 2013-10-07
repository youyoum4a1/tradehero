package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
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

    private TextView mSecurityType;
    private TextView mPriceAsOf;
    private TextView mCashAvailable;
    private TextView mShareAvailable;
    private TextView mQuantity;
    private TextView mTradeValue;

    private TableRow mCashAvailableRow;
    private TableRow mShareAvailableRow;
    private TableRow mQuantityRow;

    private SecurityCompactDTO securityCompactDTO;
    private SecurityPositionDetailDTO securityPositionDetailDTO;
    private QuoteDTO quoteDTO;
    private boolean buy = true;
    private double shareQuantity;
    private boolean mHighlightQuantity = false;
    private int mNormalQuantityColor;
    private int mHighlightQuantityColor;

    //<editor-fold desc="Constructors">
    public TradeQuantityView(Context context)
    {
        super(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TradeQuantityView(Context context, AttributeSet attrs)
    {
        super(context, attrs);    //To change body of overridden methods use File | Settings | File Templates.
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
        updateVisibilities();
    }

    public double getShareQuantity()
    {
        return shareQuantity;
    }

    public void setShareQuantity(double shareQuantity)
    {
        this.shareQuantity = shareQuantity;
        updateShareQuantity();
        updateTradeValue();
    }

    public boolean isHighlightQuantity()
    {
        return mHighlightQuantity;
    }

    public void setHighlightQuantity(boolean highlightQuantity)
    {
        this.mHighlightQuantity = highlightQuantity;
        updateQuantityHighlight();
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
        mCashAvailableRow = (TableRow) findViewById(R.id.cash_available_row);
        mShareAvailableRow = (TableRow) findViewById(R.id.share_available_row);
        mQuantityRow = (TableRow) findViewById(R.id.quantity_row);
        mNormalQuantityColor = getResources().getColor(android.R.color.transparent);
        mHighlightQuantityColor = getResources().getColor(R.color.trade_highlight_share_quantity);
        updateVisibilities();
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

    public void display()
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

        updateShareQuantity();
        updateTradeValue();
    }

    public void updateVisibilities()
    {
        if (mCashAvailableRow != null)
        {
            mCashAvailableRow.setVisibility(buy ? VISIBLE : GONE);
        }
        if (mShareAvailableRow != null)
        {
            mShareAvailableRow.setVisibility(buy ? GONE : VISIBLE);
        }
    }

    private void updateShareQuantity()
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

    private void updateQuantityHighlight()
    {
        if (mQuantityRow != null)
        {
            mQuantityRow.setBackgroundColor(mHighlightQuantity ? mHighlightQuantityColor : mNormalQuantityColor);
        }
    }

    private void updateTradeValue()
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
}
