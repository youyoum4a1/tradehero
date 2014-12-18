package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

/**
 * Header displayed on a Portfolio owned by the authenticated user.
 */
public class CurrentUserFxPortfolioHeaderView extends CurrentUserPortfolioHeaderView
{
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @InjectView(R.id.margin_close_out_status) protected TextView marginCloseOutStatus;
    @InjectView(R.id.header_portfolio_margin_available) protected TextView marginAvailable;
    @InjectView(R.id.header_portfolio_margin_used) protected TextView marginUsed;
    @InjectView(R.id.header_portfolio_pl_unrealised) protected TextView unrealisedPl;

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
            if (portfolioCompactDTO != null
                    && portfolioCompactDTO.marginCloseOutPercent != null)
            {
                int labelResId = portfolioCompactDTOUtil.getMarginCloseOutLabelResId(getResources(), portfolioCompactDTO.marginCloseOutPercent);
                marginCloseOutStatus.setText(getResources().getString(
                        labelResId,
                        THSignedNumber.builder(portfolioCompactDTO.marginCloseOutPercent)
                                .relevantDigitCount(2)
                                .build().toString()));
                marginCloseOutStatus.setBackgroundColor(portfolioCompactDTOUtil.getMarginCloseOutColor(
                                getResources(),
                                portfolioCompactDTO.marginCloseOutPercent));
                marginCloseOutStatus.setVisibility(VISIBLE);
            }
            else
            {
                marginCloseOutStatus.setVisibility(GONE);
            }
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
                    THSignedMoney unrealised = THSignedMoney.builder(portfolioCompactDTO.unrealizedPLRefCcy)
                            .currency(portfolioCompactDTO.getNiceCurrency())
                            .build();
                    unrealisedPl.setText(unrealised.toString());
                    unrealisedPl.setTextColor(unrealised.getColor());
                }
                else
                {
                    unrealisedPl.setText(R.string.na);
                }
            }
        }
    }
}
