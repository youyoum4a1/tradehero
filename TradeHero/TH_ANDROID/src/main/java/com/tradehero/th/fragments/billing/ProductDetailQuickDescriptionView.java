package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.thm.R;
import com.tradehero.th.billing.ProductIdentifierDomain;

public class ProductDetailQuickDescriptionView extends RelativeLayout
{
    private TextView quickDescription;
    private ProductIdentifierDomain productDomain;

    //<editor-fold desc="Constructors">
    public ProductDetailQuickDescriptionView(Context context)
    {
        super(context);
    }

    public ProductDetailQuickDescriptionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProductDetailQuickDescriptionView(Context context, AttributeSet attrs, int defStyle)
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

    public void linkWithProductDomain(ProductIdentifierDomain domain, boolean andDisplay)
    {
        this.productDomain = domain;
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
            if (productDomain != null)
            {
                quickDescription.setText(getQuickTextResId());
            }
        }
    }

    public int getQuickTextResId()
    {
        switch (productDomain)
        {
            case DOMAIN_VIRTUAL_DOLLAR:
                return R.string.store_buy_virtual_dollar_window_message;
            case DOMAIN_FOLLOW_CREDITS:
                return R.string.store_buy_follow_credits_window_message;
            case DOMAIN_STOCK_ALERTS:
                return R.string.store_buy_stock_alerts_window_message;
            case DOMAIN_RESET_PORTFOLIO:
                return R.string.store_buy_reset_portfolio_window_message;

            default:
                return R.string.na;
        }
    }
}
