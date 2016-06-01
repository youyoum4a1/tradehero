package com.ayondo.academy.fragments.trade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import butterknife.Bind;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.security.SecurityCircleProgressBar;

public class StockDetailActionBarRelativeLayout extends StockActionBarRelativeLayout
{
    @Bind(R.id.circle_progressbar) protected SecurityCircleProgressBar circleProgressBar;
    @Bind(R.id.action_bar_market_closed_icon) protected View marketCloseIcon;

    //<editor-fold desc="Constructors">
    public StockDetailActionBarRelativeLayout(Context context)
    {
        super(context);
    }

    public StockDetailActionBarRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StockDetailActionBarRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override public void display(@NonNull Requisite dto)
    {
        super.display(dto);
        if (dto.securityCompactDTO != null)
        {
            if (circleProgressBar != null)
            {
                circleProgressBar.display(dto.securityCompactDTO);
            }
            if (marketCloseIcon != null)
            {
                boolean marketIsOpen = dto.securityCompactDTO.marketOpen == null || dto.securityCompactDTO.marketOpen;
                marketCloseIcon.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
            }
        }
    }
}
