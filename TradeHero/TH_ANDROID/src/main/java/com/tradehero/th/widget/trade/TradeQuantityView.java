package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
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

    private SecurityCompactDTO securityCompactDTO;
    private boolean buy;
    private double shareQuantity;

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
        updateVisibilities();
        display();
    }

    @Override public void display(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        display();
    }

    public void display()
    {
        if (mSecurityType != null && securityCompactDTO != null)
        {
            mSecurityType.setText(SecurityType.getStringResourceId(securityCompactDTO.securityType));
        }
        else if (mSecurityType != null)
        {
            mSecurityType.setText("");
        }

        if (mPriceAsOf != null && securityCompactDTO != null)
        {
            mPriceAsOf.setText(DateUtils.getFormatedTrendDate(securityCompactDTO.lastPriceDateAndTimeUtc));
        }
        else if (mPriceAsOf != null)
        {
            mPriceAsOf.setText("");
        }

        double cashAvailable = THUser.getCurrentUser().portfolio.cashBalance;
        if (mCashAvailable != null)
        {
            mCashAvailable.setText(String.format("US$ %,f", cashAvailable));
        }

        // TODO populate shareAvailable

        updateShareQuantity();
        updateTradeValue();
    }

    public void updateVisibilities()
    {
        if (mCashAvailable != null)
        {
            mCashAvailable.setVisibility(buy ? VISIBLE : GONE);
        }
        if (mShareAvailable != null)
        {
            mShareAvailable.setVisibility(buy ? GONE : VISIBLE);
        }
    }

    private void updateShareQuantity()
    {
        if (mQuantity != null)
        {
            mQuantity.setText(String.format("%,f", shareQuantity));
        }
    }

    private void updateTradeValue()
    {
        if (mTradeValue != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.isLastPriceNotNullOrZero())
            {
                mTradeValue.setText(String.format("%,d", (int) Math.floor((shareQuantity * securityCompactDTO.lastPrice))));
            }
            else
            {
                mTradeValue.setText(String.format("%s", "-"));
            }
        }
    }
}
