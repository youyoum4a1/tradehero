package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Header displayed on a Portfolio owned by the authenticated user.
 */
public class CurrentUserPortfolioHeaderView extends LinearLayout implements PortfolioHeaderView
{
    protected PortfolioCompactDTO portfolioCompactDTO;

    @InjectView(R.id.header_portfolio_total_value) protected TextView totalValueTextView;
    @InjectView(R.id.header_portfolio_cash_value) protected  TextView cashValueTextView;

    //<editor-fold desc="Constructors">
    public CurrentUserPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public CurrentUserPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CurrentUserPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;

        displayTotalValueTextView();
        displayCashValueTextView();
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO)
    {
        // Nothing to do
    }

    public void displayTotalValueTextView()
    {
        if (totalValueTextView != null)
        {
            if (portfolioCompactDTO != null)
            {
                String valueString = String.format("%s %,.0f", this.portfolioCompactDTO.getNiceCurrency(), this.portfolioCompactDTO.totalValue);
                totalValueTextView.setText(valueString);
            }
        }
    }

    public void displayCashValueTextView()
    {
        if (cashValueTextView != null)
        {
            if (portfolioCompactDTO != null)
            {
                String cashString = String.format("%s %,.0f", portfolioCompactDTO.getNiceCurrency(), this.portfolioCompactDTO.cashBalance);
                cashValueTextView.setText(cashString);
            }
        }
    }

    @Override public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        // Nothing to do
    }

    @Override public void setTimelineRequestedListener(OnTimelineRequestedListener timelineRequestedListener)
    {
        // Nothing to do
    }
}
