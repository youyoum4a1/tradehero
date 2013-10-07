package com.tradehero.th.widget.trade;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 2:55 PM To change this template use File | Settings | File Templates. */
public class PricingBidAskView extends LinearLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = PricingBidAskView.class.getSimpleName();
    private TextView mLastPrice;
    private TextView mAskPrice;
    private TextView mBidPrice;
    private TextView mAskPriceHint;
    private TextView mBidPriceHint;
    private ProgressBar mProgressBar;
    private int activeColor;
    private int inactiveColor;

    private SecurityCompactDTO securityCompactDTO;
    private SecurityPositionDetailDTO securityPositionDetailDTO;
    private boolean buy = true;

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
        mAskPrice = (TextView) findViewById(R.id.ask_price);
        mBidPrice = (TextView) findViewById(R.id.bid_price);
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

    public void display()
    {
        if (securityCompactDTO == null)
        {
            if (mLastPrice != null)
            {
                mLastPrice.setText("");
            }
            if (mAskPrice != null)
            {
                mAskPrice.setText("");
            }
            if (mBidPrice != null)
            {
                mBidPrice.setText("");
            }
        }
        else
        {
            if (mLastPrice != null && securityCompactDTO.lastPrice == null)
            {
                mLastPrice.setText(String.format("%s-", securityCompactDTO.currencyDisplay));
            }
            else if (mLastPrice != null)
            {
                mLastPrice.setText(String.format("%s%.2f", securityCompactDTO.currencyDisplay, securityCompactDTO.lastPrice));
            }

            if (mAskPrice != null && securityCompactDTO.askPrice == null)
            {
                mAskPrice.setText("N/A");
            }
            else
            {
                mAskPrice.setText(String.format("%.2f", securityCompactDTO.askPrice));
            }

            if (mBidPrice != null && securityCompactDTO.bidPrice == null)
            {
                mBidPrice.setText("N/A");
            }
            else
            {
                mBidPrice.setText(String.format(" %.2f", securityCompactDTO.bidPrice));
            }
        }
        updateVisibilities();
    }

    private void updateVisibilities()
    {
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
