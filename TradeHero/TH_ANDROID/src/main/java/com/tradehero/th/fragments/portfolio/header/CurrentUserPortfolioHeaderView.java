package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.thm.R;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Header displayed on aPortfolio owned by the authenticated user.
 */
public class CurrentUserPortfolioHeaderView extends LinearLayout implements PortfolioHeaderView
{
    private PortfolioDTO portfolioDTO;

    private TextView totalValueTextView;
    private TextView cashValueTextView;

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
        initViews();
    }

    private void initViews()
    {
        totalValueTextView = (TextView) findViewById(R.id.header_portfolio_total_value);
        cashValueTextView = (TextView) findViewById(R.id.header_portfolio_cash_value);
    }

    @Override public void linkWith(PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;

        displayTotalValueTextView();
        displayCashValueTextView();
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO)
    {
        // Nothing to do
    }

    private void displayTotalValueTextView()
    {
        if (totalValueTextView != null)
        {
            if (this.portfolioDTO != null)
            {
                String valueString = String.format("%s %,.0f", this.portfolioDTO.getNiceCurrency(), this.portfolioDTO.totalValue);
                totalValueTextView.setText(valueString);
            }
        }
    }

    private void displayCashValueTextView()
    {
        if (cashValueTextView != null)
        {
            if (this.portfolioDTO != null)
            {
                String cashString = String.format("%s %,.0f", this.portfolioDTO.getNiceCurrency(), this.portfolioDTO.cashBalance);
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
