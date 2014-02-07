package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 2:55 PM To change this template use File | Settings | File Templates. */
public class PricingBidAskView extends LinearLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = PricingBidAskView.class.getSimpleName();
    private TextView mLastPrice;
    private TextView mLastPriceUSD;
    private TableRow mRowBidAskPrice;
    private TextView mAskPrice;
    private TextView mBidPrice;
    private TableRow mRowBidAskHint;
    private TextView mAskPriceHint;
    private TextView mBidPriceHint;
    private ProgressBar mProgressBar;
    private int activeColor;
    private int inactiveColor;

    private SecurityCompactDTO securityCompactDTO;
    private SecurityPositionDetailDTO securityPositionDetailDTO;
    private QuoteDTO quoteDTO;
    private boolean buy = true;
    private boolean refreshingQuote = false;

    //<editor-fold desc="Constructors">
    public PricingBidAskView(Context context)
    {
        super(context);
        init();
    }

    public PricingBidAskView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PricingBidAskView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
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
        displayLastPrice();
        displayLastPriceUSD();
    }

    public boolean isRefreshingQuote()
    {
        return refreshingQuote;
    }

    public void setRefreshingQuote(boolean refreshingQuote)
    {
        this.refreshingQuote = refreshingQuote;
        if (mRowBidAskHint != null && refreshingQuote)
        {
            mRowBidAskHint.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out));
        }
        if (mRowBidAskPrice != null && refreshingQuote)
        {
            mRowBidAskPrice.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out));
        }
        display();
    }
    //</editor-fold>

    private void init()
    {
        activeColor = getResources().getColor(R.color.black);
        inactiveColor = getResources().getColor(R.color.title);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initView();
    }

    private void initView()
    {
        mLastPrice = (TextView) findViewById(R.id.last_price);
        mLastPriceUSD = (TextView) findViewById(R.id.bid_price_usd);
        mRowBidAskPrice = (TableRow) findViewById(R.id.row_bid_ask_price);
        mAskPrice = (TextView) findViewById(R.id.ask_price);
        mBidPrice = (TextView) findViewById(R.id.bid_price);
        mRowBidAskHint = (TableRow) findViewById(R.id.row_bid_ask_hint);
        mAskPriceHint = (TextView) findViewById(R.id.ask_price_hint);
        mBidPriceHint = (TextView) findViewById(R.id.bid_price_hint);
        //mProgressBar = (ProgressBar) findViewById(R.id.progress);
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
            this.securityCompactDTO = securityPositionDetailDTO.security;
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
        displayLastPrice();
        displayLastPriceUSD();
        displayAskPrice();
        displayBidPrice();

        updateVisibilities();
    }

    public void displayLastPrice()
    {
        TextView   lastPriceView = mLastPrice;
        if (lastPriceView != null)
        {
            lastPriceView.setText(getLastPriceText());
        }
    }

    public void displayAskPrice()
    {
        TextView askPriceView = mAskPrice;
        if (askPriceView != null)
        {
            askPriceView.setText(getAskPriceText());
        }
    }

    public void displayBidPrice()
    {
        TextView bidPriceView = mBidPrice;
        if (bidPriceView != null)
        {
            bidPriceView.setText(getBidPriceText());
        }
    }

    public void displayLastPriceUSD()
    {
        TextView lastPrice = mLastPriceUSD;
        if (lastPrice != null)
        {
            if (quoteDTO == null || quoteDTO.ask == null || quoteDTO.bid == null || quoteDTO.toUSDRate == null )
            {
                lastPrice.setText(R.string.usd_price_unit_left);
            }
            else
            {
                THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, (buy ? quoteDTO.ask : quoteDTO.bid) * quoteDTO.toUSDRate, false);
                lastPrice.setText(String.format("= %s", thSignedNumber.toString()));
            }
        }
    }

    private void updateVisibilities()
    {
        if (mRowBidAskPrice != null)
        {
            if (!refreshingQuote)
            {
                mRowBidAskPrice.clearAnimation();
                mRowBidAskPrice.setAlpha(1);
            }
        }

        if (mRowBidAskHint != null)
        {
            if (!refreshingQuote)
            {
                mRowBidAskHint.clearAnimation();
                mRowBidAskHint.setAlpha(1);
            }
        }

        if (mAskPriceHint != null)
        {
            mAskPriceHint.setTextColor(buy ? activeColor : inactiveColor);
        }
        if (mAskPrice != null)
        {
            mAskPrice.setTextColor(buy ? activeColor : inactiveColor);
        }
        if (mBidPriceHint != null)
        {
            mBidPriceHint.setTextColor(buy ? inactiveColor : activeColor);
        }
        if (mBidPrice != null)
        {
            mBidPrice.setTextColor(buy ? inactiveColor : activeColor);
        }
    }

    public String getLastPriceText()
    {
        return String.format("%s %s", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,  buy ? getAskPriceText() : getBidPriceText());
    }

    public String getCurrencyDisplay()
    {
        return securityCompactDTO == null ? "" : securityCompactDTO.currencyDisplay;
    }

    public String getAskPriceText()
    {
        if (quoteDTO == null)
        {
            return "-";
        }
        else if (quoteDTO.ask == null)
        {
            return getResources().getString(R.string.buy_sell_ask_price_not_available);
        }
        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.ask, false, "");
        return thSignedNumber.toString();
    }

    public String getBidPriceText()
    {
        if (quoteDTO == null)
        {
            return "-";
        }
        else if (quoteDTO.bid == null)
        {
            return getResources().getString(R.string.buy_sell_bid_price_not_available);
        }
        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.bid, false, "");
        return thSignedNumber.toString();
    }
}
