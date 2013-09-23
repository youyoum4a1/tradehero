package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.YUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 2:55 PM To change this template use File | Settings | File Templates. */
public class PricingBidAskView extends RelativeLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = PricingBidAskView.class.getSimpleName();
    private TextView mLastPrice;
    private TextView mAskPrice;
    private TextView mBidPrice;
    private ProgressBar mProgressBar;

    private SecurityCompactDTO securityCompactDTO;

    //<editor-fold desc="Constructors">
    public PricingBidAskView(Context context)
    {
        super(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PricingBidAskView(Context context, AttributeSet attrs)
    {
        super(context, attrs);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PricingBidAskView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);    //To change body of overridden methods use File | Settings | File Templates.
    }
    //</editor-fold>

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
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        display();
    }

    @Override public void display(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
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
            if (securityCompactDTO.lastPrice == null && mLastPrice != null)
            {
                mLastPrice.setText(String.format("%s-", securityCompactDTO.currencyDisplay));
            }
            else if (mLastPrice != null)
            {
                mLastPrice.setText(String.format("%s%.2f", securityCompactDTO.currencyDisplay, securityCompactDTO.lastPrice));
            }

            if (mAskPrice != null)
            {
                mAskPrice.setText(String.format("%.2f%s", securityCompactDTO.askPrice, getResources().getString(R.string.ask_with_bracket)));
            }

            if (mBidPrice != null)
            {
                mBidPrice.setText(String.format(" x %.2f%s", securityCompactDTO.bidPrice, getResources().getString(R.string.bid_with_bracket)));
            }
        }
    }
}
