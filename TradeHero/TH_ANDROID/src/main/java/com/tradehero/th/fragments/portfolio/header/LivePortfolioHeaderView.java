package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.number.THSignedPercentage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import rx.Observable;

public class LivePortfolioHeaderView extends LinearLayout implements PortfolioHeaderView
{
    protected PortfolioCompactDTO portfolioCompactDTO;

    @Bind(R.id.header_portfolio_total_value) protected TextView totalValueTextView;
    @Bind(R.id.header_portfolio_cash_value) @Nullable protected TextView cashValueTextView;
    @Bind(R.id.roi_value) @Nullable protected TextView roiTextView;
    @Bind(R.id.last_updated_date) @Nullable protected TextView lastUpdatedDate;

    //<editor-fold desc="Constructors">
    public LivePortfolioHeaderView(Context context)
    {
        super(context);
    }

    public LivePortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LivePortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @NonNull @Override public Observable<UserAction> getUserActionObservable()
    {
        return Observable.empty();
    }

    @Override public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;

        displayTotalValueTextView();
        displayCashValueTextView();

        if (roiTextView != null)
        {
            if (portfolioCompactDTO != null && portfolioCompactDTO.roiSinceInception != null)
            {
                THSignedPercentage.builder(portfolioCompactDTO.roiSinceInception * 100)
                        .relevantDigitCount(3)
                        .withDefaultColor()
                        .withSign()
                        .signTypeArrow()
                        .build()
                        .into(roiTextView);
            }
        }

        if (lastUpdatedDate != null)
        {
            if (portfolioCompactDTO != null && portfolioCompactDTO.markingAsOfUtc != null)
            {
                DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
                lastUpdatedDate.setText(getContext().getString(
                        R.string.watchlist_marking_date,
                        sdf.format(portfolioCompactDTO.markingAsOfUtc)));
                lastUpdatedDate.setVisibility(VISIBLE);
            }
            else
            {
                lastUpdatedDate.setVisibility(GONE);
            }
        }
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
                String cashString = String.format("%s %,.0f", portfolioCompactDTO.getNiceCurrency(), this.portfolioCompactDTO.cashBalanceRefCcy);
                cashValueTextView.setText(cashString);
            }
        }
    }
}
