package com.tradehero.th.widget.trade;

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
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
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
        if (mLastPrice != null)
        {
            if (securityCompactDTO == null)
            {
                mLastPrice.setText("");
            }
            else if (securityCompactDTO.lastPrice == null)
            {
                mLastPrice.setText(String.format("%s-", securityCompactDTO.currencyDisplay));
            }
            else
            {
                mLastPrice.setText(String.format("%s%.2f", securityCompactDTO.currencyDisplay, securityCompactDTO.lastPrice));
            }
        }

        if (mAskPrice != null)
        {
            if (quoteDTO == null)
            {
                mAskPrice.setText("-");
            }
            else if (quoteDTO.ask == null)
            {
                mAskPrice.setText("N/A");
            }
            else
            {
                mAskPrice.setText(String.format("%.2f", quoteDTO.ask));
            }
        }

        if (mBidPrice != null)
        {
            if (quoteDTO == null)
            {
                mBidPrice.setText("-");
            }
            else if (quoteDTO.bid == null)
            {
                mBidPrice.setText("N/A");
            }
            else
            {
                mBidPrice.setText(String.format(" %.2f", quoteDTO.bid));
            }
        }

        if (mLastPriceUSD != null)
        {
            if (securityCompactDTO != null && !securityCompactDTO.lastPrice.isNaN() &&
                    quoteDTO != null && quoteDTO.toUSDRate != null && !quoteDTO.toUSDRate.isNaN())
            {
                mLastPriceUSD.setText(String.format("%s %.2f",
                        getResources().getString(R.string.usd_price_unit_left),
                        securityCompactDTO.lastPrice * quoteDTO.toUSDRate));
            }
            else
            {
                mLastPriceUSD.setText(R.string.usd_price_unit_left);
            }
        }

        updateVisibilities();
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
            mAskPriceHint.setTextColor(buy ? inactiveColor : activeColor);
        }
        if (mAskPrice != null)
        {
            mAskPrice.setTextColor(buy ? inactiveColor : activeColor);
        }
        if (mBidPriceHint != null)
        {
            mBidPriceHint.setTextColor(buy ? activeColor : inactiveColor);
        }
        if (mBidPrice != null)
        {
            mBidPrice.setTextColor(buy ? activeColor : inactiveColor);
        }
    }
}
