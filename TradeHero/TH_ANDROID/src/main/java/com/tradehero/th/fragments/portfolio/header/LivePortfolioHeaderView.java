package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @Bind(R.id.header_portfolio_cash_value) protected TextView cashValueTextView;
    @Bind(R.id.header_portfolio_margin_value) protected TextView marginValueTextView;
    @Bind(R.id.roi_value) protected TextView roiTextView;
    @Bind(R.id.last_updated_date) protected TextView lastUpdatedDate;
    @Bind(R.id.live_setting) public ImageButton settingBtn;

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

    @Override public void linkWith(@NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;

        if (portfolioCompactDTO.roiSinceInception != null)
        {
            THSignedPercentage.builder(portfolioCompactDTO.roiSinceInception * 100)
                    .relevantDigitCount(3)
                    .withDefaultColor()
                    .withSign()
                    .signTypeArrow()
                    .build()
                    .into(roiTextView);
        }

        if (portfolioCompactDTO.markingAsOfUtc != null)
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

        String valueString = String.format("%s %,.0f", this.portfolioCompactDTO.getNiceCurrency(), this.portfolioCompactDTO.totalValue);
        totalValueTextView.setText(valueString);

        String cashString = String.format("%s %,.0f", portfolioCompactDTO.getNiceCurrency(), this.portfolioCompactDTO.cashBalanceRefCcy);
        cashValueTextView.setText(cashString);

        if (portfolioCompactDTO.marginAvailableRefCcy != null)
        {
            String marginString = String.format("%s %,.0f", portfolioCompactDTO.getNiceCurrency(), this.portfolioCompactDTO.marginAvailableRefCcy);
            marginValueTextView.setText(marginString);
        }
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO)
    {
        // Nothing to do
    }
}