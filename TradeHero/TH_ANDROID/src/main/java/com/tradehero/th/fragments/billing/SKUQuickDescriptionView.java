package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.THSKUDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 5:07 PM To change this template use File | Settings | File Templates. */
public class SKUQuickDescriptionView extends RelativeLayout
{
    public static final String TAG = SKUQuickDescriptionView.class.getSimpleName();

    private TextView quickDescription;
    private String skuDomain;

    //<editor-fold desc="Constructors">
    public SKUQuickDescriptionView(Context context)
    {
        super(context);
    }

    public SKUQuickDescriptionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SKUQuickDescriptionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        quickDescription = (TextView) findViewById(R.id.quick_description);
    }

    public void linkWithSkuDomain(String domain, boolean andDisplay)
    {
        this.skuDomain = domain;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayQuickDescription();
    }

    public void displayQuickDescription()
    {
        if (quickDescription != null)
        {
            if (skuDomain != null)
            {
                quickDescription.setText(getQuickTextResId());
            }
        }
    }

    public int getQuickTextResId()
    {
        switch (skuDomain)
        {
            case THSKUDetails.DOMAIN_VIRTUAL_DOLLAR:
                return R.string.store_buy_virtual_dollar_window_message;
            case THSKUDetails.DOMAIN_FOLLOW_CREDITS:
                return R.string.store_buy_follow_credits_window_message;
            case THSKUDetails.DOMAIN_STOCK_ALERTS:
                return R.string.store_buy_stock_alerts_window_message;
            case THSKUDetails.DOMAIN_RESET_PORTFOLIO:
                return R.string.store_buy_reset_portfolio_window_message;

            default:
                return R.string.na;
        }
    }
}
