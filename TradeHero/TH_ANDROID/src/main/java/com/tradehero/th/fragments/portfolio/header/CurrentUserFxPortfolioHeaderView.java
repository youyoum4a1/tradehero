package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.models.number.THSignedMoney;

/**
 * Header displayed on a Portfolio owned by the authenticated user.
 */
public class CurrentUserFxPortfolioHeaderView extends CurrentUserPortfolioHeaderView
{
    @InjectView(R.id.header_portfolio_margin_available) protected TextView marginAvailable;
    @InjectView(R.id.header_portfolio_margin_used) protected TextView marginUsed;
    @InjectView(R.id.header_portfolio_pl_unrealised) protected TextView unrealisedPl;
    @InjectView(R.id.header_portfolio_pl_realised) protected TextView realisedPl;

    //<editor-fold desc="Constructors">
    public CurrentUserFxPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public CurrentUserFxPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CurrentUserFxPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        super.linkWith(portfolioCompactDTO);

        displayMarginAvailable();
        displayMarginUsed();
        displayUnrealisedPl();
        displayRealisedPl();
    }

    @Override public void displayTotalValueTextView()
    {
        if (totalValueTextView != null)
        {
            if (portfolioCompactDTO != null)
            {
                totalValueTextView.setText(
                        THSignedMoney.builder(portfolioCompactDTO.nav)
                                .currency(portfolioCompactDTO.getNiceCurrency())
                                .build()
                                .toString());
            }
        }
    }

    public void displayMarginAvailable()
    {
        if (marginAvailable != null)
        {
            if (portfolioCompactDTO != null)
            {
                marginAvailable.setText(
                        THSignedMoney.builder(portfolioCompactDTO.marginAvailableRefCcy)
                                .currency(portfolioCompactDTO.getNiceCurrency())
                                .build()
                                .toString());
            }
        }
    }

    public void displayMarginUsed()
    {
        if (marginUsed != null)
        {
            if (portfolioCompactDTO != null)
            {
                marginUsed.setText(
                        THSignedMoney.builder(portfolioCompactDTO.marginUsedRefCcy)
                                .currency(portfolioCompactDTO.getNiceCurrency())
                                .build()
                                .toString());
            }
        }
    }

    public void displayUnrealisedPl()
    {
        if (unrealisedPl != null)
        {
            if (portfolioCompactDTO != null)
            {
                unrealisedPl.setText(
                        THSignedMoney.builder(portfolioCompactDTO.unrealizedPLRefCcy)
                                .currency(portfolioCompactDTO.getNiceCurrency())
                                .build()
                                .toString());
            }
        }
    }

    public void displayRealisedPl()
    {
        if (realisedPl != null)
        {
            if (portfolioCompactDTO != null)
            {
                // TODO
            }
        }
    }
}
