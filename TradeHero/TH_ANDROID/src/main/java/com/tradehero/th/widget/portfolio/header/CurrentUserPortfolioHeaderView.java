package com.tradehero.th.widget.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 21/10/13
 * Header displayed on aPortfolio owned by the authenticated user.
 */
public class CurrentUserPortfolioHeaderView extends RelativeLayout implements PortfolioHeaderView
{
    @Inject Lazy<PortfolioCompactCache> portfolioCache;
    private OwnedPortfolioId portfolioId;
    private PortfolioCompactDTO portfolio;

    private TextView totalValueTextView;
    private TextView cashValueTextView;

    //<editor-fold desc="Description">
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
        DaggerUtils.inject(this);
        initViews();
    }

    private void initViews()
    {
        totalValueTextView = (TextView) findViewById(R.id.header_portfolio_total_value);
        cashValueTextView = (TextView) findViewById(R.id.header_portfolio_cash_value);
    }

    @Override public void bindOwnedPortfolioId(OwnedPortfolioId id)
    {
        this.portfolio = this.portfolioCache.get().get(id.getPortfolioId());

        if (portfolio != null)
        {
            if (totalValueTextView != null)
            {
                String valueString = String.format("US$ %,.0f", portfolio.totalValue);
                totalValueTextView.setText(valueString);
            }

            if (cashValueTextView != null)
            {
                String cashString = String.format("US$ %,.0f", portfolio.cashBalance);
                cashValueTextView.setText(cashString);
            }
        }
    }
}
