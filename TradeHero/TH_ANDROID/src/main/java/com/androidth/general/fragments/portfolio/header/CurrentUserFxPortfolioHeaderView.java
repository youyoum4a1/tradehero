package com.androidth.general.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.models.number.THSignedMoney;

/**
 * Header displayed on a Portfolio owned by the authenticated user.
 */
public class CurrentUserFxPortfolioHeaderView extends CurrentUserPortfolioHeaderView
{
    @Bind(R.id.margin_close_out_status) protected MarginCloseOutStatusTextView marginCloseOutStatus;
    @Bind(R.id.header_portfolio_margin_available) protected TextView marginAvailable;
    @Bind(R.id.header_portfolio_margin_used) protected TextView marginUsed;
    @Bind(R.id.header_portfolio_pl_unrealised) protected TextView unrealisedPl;

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

        displayMarginCloseOutStatus();
        displayMarginAvailable();
        displayMarginUsed();
        displayUnrealisedPl();
    }

    @Override public void displayTotalValueTextView()
    {
        if (totalValueTextView != null)
        {
            if (portfolioCompactDTO != null)
            {
                if (portfolioCompactDTO.nav != null)
                {
                    totalValueTextView.setText(
                            THSignedMoney.builder(portfolioCompactDTO.nav)
                                    .currency(portfolioCompactDTO.getNiceCurrency())
                                    .build()
                                    .toString());
                }
                else
                {
                    totalValueTextView.setText(R.string.na);
                }
            }
        }
    }

    public void displayMarginCloseOutStatus()
    {
        if (marginCloseOutStatus != null)
        {
            marginCloseOutStatus.linkWith(portfolioCompactDTO);
        }
    }

    public void displayMarginAvailable()
    {
        if (marginAvailable != null)
        {
            if (portfolioCompactDTO != null)
            {
                if (portfolioCompactDTO.marginAvailableRefCcy != null)
                {
                    marginAvailable.setText(
                            THSignedMoney.builder(portfolioCompactDTO.marginAvailableRefCcy)
                                    .currency(portfolioCompactDTO.getNiceCurrency())
                                    .build()
                                    .toString());
                }
                else
                {
                    marginAvailable.setText(R.string.na);
                }
            }
        }
    }

    public void displayMarginUsed()
    {
        if (marginUsed != null)
        {
            if (portfolioCompactDTO != null)
            {
                if (portfolioCompactDTO.marginUsedRefCcy != null)
                {
                    marginUsed.setText(
                            THSignedMoney.builder(portfolioCompactDTO.marginUsedRefCcy)
                                    .currency(portfolioCompactDTO.getNiceCurrency())
                                    .build()
                                    .toString());
                }
                else
                {
                    marginUsed.setText(R.string.na);
                }
            }
        }
    }

    public void displayUnrealisedPl()
    {
        if (unrealisedPl != null)
        {
            if (portfolioCompactDTO != null)
            {
                if (portfolioCompactDTO.unrealizedPLRefCcy != null)
                {
                    THSignedMoney.builder(portfolioCompactDTO.unrealizedPLRefCcy)
                            .currency(portfolioCompactDTO.getNiceCurrency())
                            .withOutSign()
                            .withDefaultColor()
                            .build()
                            .into(unrealisedPl);
                }
                else
                {
                    unrealisedPl.setText(R.string.na);
                }
            }
        }
    }
}
