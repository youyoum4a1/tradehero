package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utils.THSignedNumber;

public class PricingBidAskView extends LinearLayout
{
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
    private QuoteDTO quoteDTO;
    private PortfolioCompactDTO portfolioCompactDTO;
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
        displayLastPriceRefCcy();
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

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            displayLastPrice();
        }
    }

    public void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;
        if (andDisplay)
        {
            displayAskPrice();
            displayBidPrice();
            displayLastPrice();
            displayLastPriceRefCcy();
            updateVisibilities();
        }
    }

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (andDisplay)
        {
            displayLastPriceRefCcy();
        }
    }

    public void display()
    {
        displayLastPrice();
        displayLastPriceRefCcy();
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

    public String getLastPriceText()
    {
        return String.format("%s %s", getCurrencyDisplay(),  buy ? getAskPriceText() : getBidPriceText());
    }

    public void displayAskPrice()
    {
        TextView askPriceView = mAskPrice;
        if (askPriceView != null)
        {
            askPriceView.setText(getAskPriceText());
        }
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
        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.ask, THSignedNumber.WITHOUT_SIGN, "");
        return thSignedNumber.toString();
    }

    public void displayBidPrice()
    {
        TextView bidPriceView = mBidPrice;
        if (bidPriceView != null)
        {
            bidPriceView.setText(getBidPriceText());
        }
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
        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.bid, THSignedNumber.WITHOUT_SIGN, "");
        return thSignedNumber.toString();
    }

    public void displayLastPriceRefCcy()
    {
        TextView lastPrice = mLastPriceUSD;
        if (lastPrice != null)
        {
            lastPrice.setText(getLastPriceRefCcyText());
        }
    }

    public String getLastPriceRefCcyText()
    {
        if (portfolioCompactDTO == null)
        {
            return getResources().getString(R.string.na);
        }
        Double priceRefCcy = quoteDTO == null ? null : quoteDTO.getPriceRefCcy(portfolioCompactDTO, buy);
        if (priceRefCcy == null)
        {
            return "= " + portfolioCompactDTO.getNiceCurrency();
        }
        else
        {
            THSignedNumber thSignedNumber = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    priceRefCcy,
                    THSignedNumber.WITHOUT_SIGN,
                    portfolioCompactDTO.getNiceCurrency());
            return String.format("= %s", thSignedNumber.toString());
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

    public String getCurrencyDisplay()
    {
        return securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay;
    }
}
