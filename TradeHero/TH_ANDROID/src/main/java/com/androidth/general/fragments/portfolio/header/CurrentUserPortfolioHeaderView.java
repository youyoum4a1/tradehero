package com.androidth.general.fragments.portfolio.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.number.THSignedPercentage;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import rx.Observable;

/**
 * Header displayed on a Portfolio owned by the authenticated user.
 */
public class CurrentUserPortfolioHeaderView extends LinearLayout implements PortfolioHeaderView
{
    protected PortfolioCompactDTO portfolioCompactDTO;

    @Bind(R.id.header_portfolio_total_value) protected TextView totalValueTextView;
    @Bind(R.id.header_portfolio_cash_value) @Nullable protected TextView cashValueTextView;
    @Bind(R.id.roi_value) @Nullable protected TextView roiTextView;
    @Bind(R.id.last_updated_date) @Nullable protected TextView lastUpdatedDate;

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
        ButterKnife.bind(this);
    }

    @NonNull @Override public Observable<UserAction> getUserActionObservable()
    {
        return Observable.empty();
    }

    @Override public void linkWith(PortfolioCompactDTO portfolioDTO)
    {
        this.portfolioCompactDTO = portfolioDTO;

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
                YoYo.with(Techniques.FadeIn).duration(500).playOn(lastUpdatedDate);
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
                YoYo.with(Techniques.FadeIn).duration(500).playOn(totalValueTextView);
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
                YoYo.with(Techniques.FadeIn).duration(500).playOn(cashValueTextView);
            }
        }
    }
}
